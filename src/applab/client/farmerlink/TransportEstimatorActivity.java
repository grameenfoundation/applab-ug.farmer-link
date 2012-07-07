package applab.client.farmerlink;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TransportEstimatorActivity extends Activity{
	 private Button nextButton;
	 private TextView marketTextView;
	 private TextView cropTextView;
	 private TextView quantityView;	 
	 private EditText transportText;
	 private double transportCosts;
	 
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.transport_estimator_markets);
	        marketTextView = (TextView) findViewById(R.id.market_name);
	        marketTextView.setText(MarketSaleObject.getMarketObject().getMarketPrices().getMarketName());
	        cropTextView = (TextView)findViewById(R.id.commodity_name);
	        cropTextView.setText(MarketSaleObject.getMarketObject().getCropName());
	        quantityView = (TextView) findViewById(R.id.quantity_amount);
	        quantityView.setText(String.valueOf(MarketSaleObject.getMarketObject().getTotalQuantity()));
	        nextButton = (Button) findViewById(R.id.next_finish_sell);
	        
	        nextButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
				    transportText = (EditText) findViewById(R.id.transport_cost);
		            transportCosts = Double.parseDouble(transportText.getText().toString());
		            MarketSaleObject.getMarketObject().setTransportCost(transportCosts);
		            
		            // load intent and show summary activity
					Intent intent = new Intent(getApplicationContext(), ProjectedSalesActivity.class);
					intent.putExtra("source", "Market: ");
					//intent.putExtra("crop", crop);
					startActivity(intent);
				}
	        	
	        });
	    }
}
