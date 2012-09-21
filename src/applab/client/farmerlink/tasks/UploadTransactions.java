package applab.client.farmerlink.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;
import applab.client.farmerlink.GlobalConstants;
import applab.client.farmerlink.MarketLinkApplication;
import applab.client.farmerlink.provider.FarmerTransactionAssociationProviderAPI;
import applab.client.farmerlink.provider.TransactionProviderAPI;
import applab.client.farmerlink.utilities.HttpHelpers;
import applab.client.farmerlink.utilities.XmlEntityBuilder;

public class UploadTransactions {

	private static String imei;
	public int uploadFarmerTransactions(String url, Context context) {
		int status = -1;
		imei = getImei(context);
		Log.i("IMEI", " " + imei);
		String selection = TransactionProviderAPI.TransactionColumns.STATUS
				+ "=?";
		String[] selectionArgs = { TransactionProviderAPI.UNSYNCHED };
		Cursor transactionCursor = MarketLinkApplication
				.getInstance()
				.getContentResolver()
				.query(TransactionProviderAPI.TransactionColumns.CONTENT_URI,
						null, selection, selectionArgs, null);
		if (transactionCursor.getCount() == 0) {
			return status;
		}
		else {
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
			transactionCursor.close();
			for (Transaction transaction : transactions) {
				List<Farmer> farmers = getTransactionFarmers(transaction);
				transaction.attributes = extractAttributes(transaction);
				try {
					int networkTimeout = 5 * 60 * 1000;
					InputStream statusStream;
					statusStream = HttpHelpers.postJsonRequestAndGetStream(url, (StringEntity)sendTransactionToSalesforce(transaction, farmers), networkTimeout);
					Writer writer = new StringWriter();
					 
		            char[] buffer = new char[1024];
		            try {
		                Reader reader = new BufferedReader(
		                        new InputStreamReader(statusStream, "UTF-8"));
		                int n;
		                while ((n = reader.read(buffer)) != -1) {
		                    writer.write(buffer, 0, n);
		                }
		            } finally {
		            	statusStream.close();
		            	status = Integer.parseInt(writer.toString().trim());
		            }
		            Log.i("OUTPUT", " "+writer.toString());
				} catch (UnsupportedEncodingException e) {
					Log.e("ERROR", e.getMessage());
				} catch (IOException ex) {
					Log.e("ERROR", ex.getMessage());
				}
				if (status == 0) {
					for (Farmer farmer : farmers) {
						ContentValues farmerTransactionUpdate = new ContentValues();
						farmerTransactionUpdate.put(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.STATUS, FarmerTransactionAssociationProviderAPI.SYNCHED);
						String where = FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns._ID + "=?";
						String[] whereArgs = {farmer.farmerId};
						MarketLinkApplication.getInstance().getContentResolver().update(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.CONTENT_URI, farmerTransactionUpdate, where, whereArgs);
					}
					ContentValues transactionUpdate = new ContentValues();
					transactionUpdate.put(TransactionProviderAPI.TransactionColumns.STATUS, TransactionProviderAPI.SYNCHED);
					String where = TransactionProviderAPI.TransactionColumns._ID + "=?";
					String[] whereArgs = {transaction.transactionId};
					MarketLinkApplication.getInstance().getContentResolver().update(TransactionProviderAPI.TransactionColumns.CONTENT_URI, transactionUpdate, where, whereArgs);
				}
			}
			return status;
		}
	}

	
	private String getImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
	}
	static AbstractHttpEntity sendTransactionToSalesforce(Transaction transaction, List<Farmer> farmers) throws UnsupportedEncodingException {

        XmlEntityBuilder xmlRequest = new XmlEntityBuilder();
        xmlRequest.writeStartElement("FarmerLinkPost", GlobalConstants.XMLNAMESPACE);
        xmlRequest.writeStartElement("transaction");
        xmlRequest.writeStartElement("imei");
        xmlRequest.writeText(imei);
        xmlRequest.writeEndElement();
        
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
		transactionAttributes.put("name", transaction.buyer);
		transactionAttributes.put("transportFee", transaction.transportFee);
		transactionAttributes.put("revenue", String.valueOf(Double.parseDouble(transaction.quantity) * Double.parseDouble(transaction.unitPrice)));
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
		transactionFarmerCursor.close();
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
