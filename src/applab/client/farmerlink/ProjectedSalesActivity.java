package applab.client.farmerlink;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import applab.client.farmerlink.provider.FarmerTransactionAssociationProviderAPI;
import applab.client.farmerlink.provider.TransactionProviderAPI;
import applab.client.farmerlink.tasks.UploadTransactions;
import applab.client.farmerlink.utilities.PricesFormatter;

public class ProjectedSalesActivity extends ListActivity {
    private TextView marketTextView;
    private TextView cropTextView;
    private TextView quantityTextView;
    private TextView priceTextView;
    private TextView totalValueTextView;
    private TextView transactFeeTextView;
    private TextView transportTextView;
    private Button nextButton;
    private Button backButton;
    private String crop;
    private String source;
    
    private ArrayList<Farmer> farmers;
    private TextView marketView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.projected_sales);
        setTextViews();
        crop = MarketSaleObject.getMarketObject().getCropName();
        source = getIntent().getStringExtra("source");
        String displayTitle = this.getString(R.string.app_name) + " - " + crop;
        setTitle(displayTitle);
        
        farmers = MarketSaleObject.getMarketObject().getFarmers();
        setListAdapter(new FarmerAdapter());

        nextButton = (Button)findViewById(R.id.next_finish);
        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO: Add committing information and logging transaction
            	Calendar today = Calendar.getInstance();
            	//save the transaction details to our database
            	ContentValues values = new ContentValues();
            	if (MarketSaleObject.getMarketObject().getTransactionType().equalsIgnoreCase(MarketSaleObject.MARKETSALE)) {
            		values.put(TransactionProviderAPI.TransactionColumns.BUYER_NAME, MarketSaleObject.getMarketObject().getMarketPrices().getMarketName());
            	}
            	else {
            		values.put(TransactionProviderAPI.TransactionColumns.BUYER_NAME, MarketSaleObject.getMarketObject().getBuyer().getName());
            	}
            	
            	values.put(TransactionProviderAPI.TransactionColumns.BUYER_NAME, "market");
            	values.put(TransactionProviderAPI.TransactionColumns.TRANSACTION_TYPE, TransactionProviderAPI.SALE_MARKET);
            	values.put(TransactionProviderAPI.TransactionColumns.CROP, MarketSaleObject.getMarketObject().getCropName());
            	values.put(TransactionProviderAPI.TransactionColumns.DISTRICT, MarketSaleObject.getMarketObject().getDistrictName());
            	values.put(TransactionProviderAPI.TransactionColumns.STATUS, TransactionProviderAPI.UNSYNCHED);
            	values.put(TransactionProviderAPI.TransactionColumns.QUANTITY, MarketSaleObject.getMarketObject().getTotalQuantity());
            	values.put(TransactionProviderAPI.TransactionColumns.TRANSACTION_FEE, MarketSaleObject.getMarketObject().getTotalTransactionFee());
            	values.put(TransactionProviderAPI.TransactionColumns.TRANSPORT_FEE, MarketSaleObject.getMarketObject().getTransportCost());
            	values.put(TransactionProviderAPI.TransactionColumns.UNITPRICE, MarketSaleObject.getMarketObject().getMarketPrices().getWholesalePrice());
            	values.put(TransactionProviderAPI.TransactionColumns.TRANSACTION_DATE,today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) 
            			+ "-" + today.get(Calendar.DATE) + " 00:00:00");
            	Uri transactionUri = MarketLinkApplication.getInstance().getContentResolver().insert(TransactionProviderAPI.TransactionColumns.CONTENT_URI, values);
            	
            	String selection = TransactionProviderAPI.TransactionColumns._ID + "=?";
            	String[] selectionArgs = {transactionUri.getPathSegments().get(1)};
            	Cursor transactionCursor = MarketLinkApplication.getInstance().getContentResolver().query(TransactionProviderAPI.TransactionColumns.CONTENT_URI, null, selection, selectionArgs, null);
            	transactionCursor.moveToFirst();
            	String transactionId = transactionUri.getPathSegments().get(1);
            	//save each farmer who participated in the transaction and their quota
            	for (Farmer farmer : farmers) {
            		ContentValues farmerTransactionValues = new ContentValues();
            		farmerTransactionValues.put(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.FARMER_ID, farmer.getId());
            		farmerTransactionValues.put(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.FARMER_QUOTA, farmer.getQuantity());
            		farmerTransactionValues.put(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.TRANSACTION_ID, transactionId);
            		farmerTransactionValues.put(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.STATUS, FarmerTransactionAssociationProviderAPI.UNSYNCHED);
            		farmerTransactionValues.put(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.FARMER_REVENUE, farmer.computeRevenue(MarketSaleObject.getMarketObject().getMarketPrices().getWholesalePriceValue()));
            		farmerTransactionValues.put(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.TRANSACTION_FEE_QUOTA, farmer.computeTransactionFee(MarketSaleObject.getMarketObject().getMarketPrices().getWholesalePriceValue()));
            		farmerTransactionValues.put(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.TRANSPORT_FEE_QUOTA, Math.ceil(MarketSaleObject.getMarketObject().getTransportCost()/farmers.size()));
            		farmerTransactionValues.put(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.FARMER_NAME, farmer.getName());
            		MarketLinkApplication.getInstance().getContentResolver().insert(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.CONTENT_URI, farmerTransactionValues);
            	}
            	transactionCursor.close();
            	UploadTransactions upload = new UploadTransactions();
            	String url = getString(R.string.server) + "/"
        				+ "FarmerLink"
        				+ getString(R.string.upload_url);
            	int success = upload.uploadFarmerTransactions(url, getApplicationContext());
            	if (success == 0) {
            		for (Farmer farmer : farmers) {
                		ContentValues farmerTransactionValues = new ContentValues();
                		farmerTransactionValues.put(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.STATUS, FarmerTransactionAssociationProviderAPI.SYNCHED);
                		String where = FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.TRANSACTION_ID + "=?" + " AND "
                		+ FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.FARMER_ID + "=?";
                		String[] whereArgs = {transactionId, farmer.getId()};
                		MarketLinkApplication.getInstance().getContentResolver().update(FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns.CONTENT_URI, farmerTransactionValues, where, whereArgs);
                	}
            		ContentValues updateValues = new ContentValues();
            		updateValues.put(TransactionProviderAPI.TransactionColumns.STATUS, TransactionProviderAPI.SYNCHED);
            		String where = TransactionProviderAPI.TransactionColumns._ID + "=?";
            		String[] whereArgs = {transactionId};
            		MarketLinkApplication.getInstance().getContentResolver().update(TransactionProviderAPI.TransactionColumns.CONTENT_URI, updateValues, where, whereArgs);
	                Intent intent = new Intent(getApplicationContext(), FinishSellActivity.class);
	                startActivity(intent);
              }
            }
        });
        
        backButton = (Button) findViewById(R.id.back_estimate_transport);
        backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				if(source.equalsIgnoreCase("Buyer: ")) {
					intent = new Intent(getApplicationContext(), TransportEstimatorBuyerActivity.class);
				} else {
					intent = new Intent(getApplicationContext(), TransportEstimatorActivity.class);
				}
				startActivity(intent);
			}
        	
        });
    }
    /**
     * Sets text values
     */
    private void setTextViews() {
        
        marketView = (TextView)findViewById(R.id.market);
        marketView.setText(getIntent().getStringExtra("source"));
        
        marketTextView = (TextView)findViewById(R.id.market_name);
        marketTextView.setText(MarketSaleObject.getMarketObject().getMarketPrices().getMarketName());

        cropTextView = (TextView)findViewById(R.id.crop_name);
        cropTextView.setText(MarketSaleObject.getMarketObject().getCropName());

        quantityTextView = (TextView)findViewById(R.id.quantity_value);
        quantityTextView.setText(PricesFormatter.formatPrice(MarketSaleObject.getMarketObject().getTotalQuantity()) + " Kg");

        priceTextView = (TextView)findViewById(R.id.kg_price_value);
        priceTextView.setText(PricesFormatter.formatPrice(MarketSaleObject.getMarketObject().getMarketPrices().getWholesalePriceValue()) + " Shs");

        totalValueTextView = (TextView)findViewById(R.id.total_value);
        totalValueTextView.setText(PricesFormatter.formatPrice(MarketSaleObject.getMarketObject().getTotalValue()) + " Shs");

        transportTextView = (TextView)findViewById(R.id.transport_value);
        transportTextView.setText(PricesFormatter.formatPrice(MarketSaleObject.getMarketObject().getTransportCost()) + " Shs");

        transactFeeTextView = (TextView)findViewById(R.id.transaction_fee_value);
        transactFeeTextView.setText(PricesFormatter.formatPrice(MarketSaleObject.getMarketObject().getTotalTransactionFee()) + " Shs");
    }

    class FarmerAdapter extends ArrayAdapter<Farmer> {
    	
    	private int[] colors = new int[] { 0x00000000, 0x30A9A9A9 };

        FarmerAdapter() {
            
            super(ProjectedSalesActivity.this, R.layout.projected_sales_list, R.id.farmer_name_text, farmers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            double revenue = farmers.get(position).computeRevenue(MarketSaleObject.getMarketObject().getMarketPrices().getWholesalePriceValue());
            double transactionFee = farmers.get(position).computeTransactionFee(MarketSaleObject.getMarketObject().getMarketPrices().getWholesalePriceValue());
            
            int colorPos = position % colors.length;
			
            
            View row = inflater.inflate(R.layout.projected_sales_list, parent, false);
            TextView marketView = (TextView)row.findViewById(R.id.farmer_name_text);
            marketView.setBackgroundColor(colors[colorPos]);
            marketView.setText("Farmer Name : " + farmers.get(position).getName());

            TextView farmerIdView = (TextView)row.findViewById(R.id.farmer_id_text);
            farmerIdView.setBackgroundColor(colors[colorPos]);
            farmerIdView.setText("Farmer ID : " + farmers.get(position).getId());
            
            TextView quantityView = (TextView)row.findViewById(R.id.quantity_text);
            quantityView.setBackgroundColor(colors[colorPos]);
            quantityView.setText("Quantity : " + PricesFormatter.formatPrice(farmers.get(position).getQuantity()) + " Kg");
          
            TextView revenueView = (TextView)row.findViewById(R.id.revenue_text);
            revenueView.setBackgroundColor(colors[colorPos]);
            revenueView.setText("Revenue : " + PricesFormatter.formatPrice(revenue) + " Shs");
            
            TextView transportView = (TextView)row.findViewById(R.id.transport_text);
            transportView.setBackgroundColor(colors[colorPos]);
            transportView.setText("Transport Cost : " + PricesFormatter.formatPrice(Math.ceil(MarketSaleObject.getMarketObject().getTransportCost()/farmers.size())) + " Shs");
            
            TextView transactionFeeView = (TextView)row.findViewById(R.id.transaction_fee_text);
            transactionFeeView.setBackgroundColor(colors[colorPos]);
            transactionFeeView.setText("Transaction Fee : " + PricesFormatter.formatPrice(transactionFee) + " Shs");           

            return row;
        }
    }
}
