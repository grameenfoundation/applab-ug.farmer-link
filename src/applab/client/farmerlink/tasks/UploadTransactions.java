package applab.client.farmerlink.tasks;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.AbstractHttpEntity;

import android.database.Cursor;
import android.util.Log;
import applab.client.farmerlink.GlobalConstants;
import applab.client.farmerlink.MarketLinkApplication;
import applab.client.farmerlink.provider.FarmerTransactionAssociationProviderAPI;
import applab.client.farmerlink.provider.TransactionProviderAPI;
import applab.client.farmerlink.utilities.XmlEntityBuilder;

public class UploadTransactions {

	public void uploadFarmerTransactions() {
		String selection = TransactionProviderAPI.TransactionColumns.STATUS
				+ "=?";
		String[] selectionArgs = { TransactionProviderAPI.UNSYNCHED };
		Cursor transactionCursor = MarketLinkApplication
				.getInstance()
				.getContentResolver()
				.query(TransactionProviderAPI.TransactionColumns.CONTENT_URI,
						null, selection, selectionArgs, null);
		transactionCursor.moveToFirst();
		List<Transaction> transactions = new ArrayList<Transaction>();
		for (int i = 0; i < transactionCursor.getCount(); i++) {
			Transaction transaction = new Transaction(
					transactionCursor
							.getString(transactionCursor
									.getColumnIndex(TransactionProviderAPI.TransactionColumns._ID)),
					transactionCursor.getString(transactionCursor
							.getColumnIndex(TransactionProviderAPI.TransactionColumns.TRANSACTION_TYPE)),
					transactionCursor.getString(transactionCursor
							.getColumnIndex(TransactionProviderAPI.TransactionColumns.TRANSACTION_DATE)),
					transactionCursor.getString(transactionCursor
							.getColumnIndex(TransactionProviderAPI.TransactionColumns.DISTRICT)),
					transactionCursor.getString(transactionCursor
							.getColumnIndex(TransactionProviderAPI.TransactionColumns.CROP)),
					transactionCursor.getString(transactionCursor
							.getColumnIndex(TransactionProviderAPI.TransactionColumns.QUANTITY)),
					transactionCursor.getString(transactionCursor
							.getColumnIndex(TransactionProviderAPI.TransactionColumns.TRANSPORT_FEE)),
					transactionCursor.getString(transactionCursor
							.getColumnIndex(TransactionProviderAPI.TransactionColumns.TRANSACTION_FEE)),
					transactionCursor.getString(transactionCursor
							.getColumnIndex(TransactionProviderAPI.TransactionColumns.UNITPRICE)),
					transactionCursor.getString(transactionCursor
							.getColumnIndex(TransactionProviderAPI.TransactionColumns.BUYER_NAME)));

			transactions.add(transaction);
		}
		for (Transaction transaction : transactions) {
			List<Farmer> farmers = getTransactionFarmers(transaction);
			transaction.attributes = extractAttributes(transaction);
			try {
				sendTransactionToSalesforce(transaction, farmers);
			} catch (UnsupportedEncodingException e) {
				Log.e("ERROR", e.getMessage());
			}
		}
	}

	static AbstractHttpEntity sendTransactionToSalesforce(Transaction transaction, List<Farmer> farmers) throws UnsupportedEncodingException {

        XmlEntityBuilder xmlRequest = new XmlEntityBuilder();
        xmlRequest.writeStartElement("FarmerLinkPost", GlobalConstants.XMLNAMESPACE);
        xmlRequest.writeStartElement("transaction");
        xmlRequest.writeStartElement("transactionDetails", transaction.attributes);
        xmlRequest.writeEndElement();
        for (Farmer farmer : farmers) {
        	xmlRequest.writeStartElement("farmer", getFarmerAttributes(farmer));
        	xmlRequest.writeEndElement();
        }
        xmlRequest.writeEndElement();  
        xmlRequest.writeEndElement();
        return xmlRequest.getEntity();
    }

	private HashMap<String, String> extractAttributes(Transaction transaction) {
		HashMap<String, String> transactionAttributes = new HashMap<String, String>();
		transactionAttributes.put("transactionId", transaction.transactionId);
		transactionAttributes.put("transactionType",
				transaction.transactionType);
		transactionAttributes.put("transactionDate",
				transaction.transactionDate);
		transactionAttributes.put("district", transaction.district);
		transactionAttributes.put("crop", transaction.crop);
		transactionAttributes.put("quantity", transaction.quantity);
		transactionAttributes.put("transactionFee", transaction.transactionFee);
		transactionAttributes.put("unitPrice", transaction.unitPrice);
		transactionAttributes.put("buyer", transaction.buyer);
		transactionAttributes.put("transportFee", transaction.transportFee);
		return transactionAttributes;
	}

	private List<Farmer> getTransactionFarmers(Transaction transaction) {
		List<Farmer> transactionFarmers = new ArrayList<Farmer>();
		String selection = FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.TRANSACTION_ID
				+ "=?";
		String[] selectionArgs = { transaction.transactionId };
		Cursor transactionFarmerCursor = MarketLinkApplication
				.getInstance()
				.getContentResolver()
				.query(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.CONTENT_URI,
						null, selection, selectionArgs, null);
		transactionFarmerCursor.moveToFirst();
		for (int count = 0; count < transactionFarmerCursor.getCount(); count++) {
			Farmer transactionFarmer = new Farmer(
					transactionFarmerCursor
							.getString(transactionFarmerCursor
									.getColumnIndex(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.FARMER_ID)),
					transactionFarmerCursor.getString(transactionFarmerCursor
							.getColumnIndex(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.FARMER_NAME)),
					transactionFarmerCursor.getString(transactionFarmerCursor
							.getColumnIndex(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.FARMER_REVENUE)),
					transactionFarmerCursor.getString(transactionFarmerCursor
							.getColumnIndex(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.TRANSPORT_FEE_QUOTA)),
					transactionFarmerCursor.getString(transactionFarmerCursor
							.getColumnIndex(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.FARMER_QUOTA)),
					transactionFarmerCursor.getString(transactionFarmerCursor
							.getColumnIndex(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.TRANSACTION_FEE_QUOTA)));

			transactionFarmers.add(transactionFarmer);
		}
		return transactionFarmers;
	}

	private static HashMap<String, String> getFarmerAttributes(Farmer farmer) {
		HashMap<String, String> farmerAttributes = new HashMap<String, String>();
		farmerAttributes.put("farmerId", farmer.farmerId);
		farmerAttributes.put("farmerName", farmer.farmerName);
		farmerAttributes.put("farmerRevenue", farmer.farmerRevenue);
		farmerAttributes.put("transportFee", farmer.transportFee);
		farmerAttributes.put("quantity", farmer.quantity);
		farmerAttributes.put("transactionFee", farmer.transactionFee);
		return farmerAttributes;
	}
	private class Transaction {
		String transactionId;
		String transactionType;
		String transactionDate;
		String district;
		String crop;
		String quantity;
		String transportFee;
		String transactionFee;
		String unitPrice;
		String buyer;

		HashMap<String, String> attributes;
		
		Transaction(String transactionId, String transactionType,
				String transactionDate, String district, String crop,
				String quantity, String transportFee, String transactionFee,
				String unitPrice, String buyer) {
			this.transactionId = transactionId;
			this.transactionType = transactionType;
			this.transactionDate = transactionDate;
			this.district = district;
			this.crop = crop;
			this.quantity = quantity;
			this.transportFee = transportFee;
			this.transactionFee = transactionFee;
			this.unitPrice = unitPrice;
			this.buyer = buyer;
		}
	}

	private class Farmer {
		String farmerId;
		String farmerName;
		String farmerRevenue;
		String transportFee;
		String quantity;
		String transactionFee;

		Farmer(String farmerId, String farmerName, String farmerRevenue,
				String transportFee, String quantity, String transactionFee) {

			this.farmerId = farmerId;
			this.farmerName = farmerName;
			this.farmerRevenue = farmerRevenue;
			this.transportFee = transportFee;
			this.quantity = quantity;
			this.transactionFee = transactionFee;
		}
	}
}
