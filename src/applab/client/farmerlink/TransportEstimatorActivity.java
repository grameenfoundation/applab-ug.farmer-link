package applab.client.farmerlink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TransportEstimatorActivity extends Activity{
	 private Button nextButton;
	 private Button backButton;
	 private TextView marketTextView;
	 private TextView cropTextView;
	 private TextView quantityView;	 
	 private EditText transportText;
	 private double transportCosts;
	 private String crop;
	 
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.transport_estimator_markets);
	        crop = MarketSaleObject.getMarketObject().getCropName();
	        String displayTitle = this.getString(R.string.app_name) + " - " + crop;
	        setTitle(displayTitle);
	        marketTextView = (TextView) findViewById(R.id.market_name);
	        marketTextView.setText(MarketSaleObject.getMarketObject().getMarketPrices().getMarketName());
	        cropTextView = (TextView)findViewById(R.id.commodity_name);
	        cropTextView.setText(MarketSaleObject.getMarketObject().getCropName());
	        quantityView = (TextView) findViewById(R.id.quantity_amount);
	        quantityView.setText(String.valueOf(MarketSaleObject.getMarketObject().getTotalQuantity()) + " Kg");
	        nextButton = (Button) findViewById(R.id.next_projected_sales);
	        
	        nextButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
				    transportText = (EditText) findViewById(R.id.transport_cost);
				    if (transportText.getText().toString().trim().length() > 0) {
			            transportCosts = Double.parseDouble(transportText.getText().toString());
			            MarketSaleObject.getMarketObject().setTransportCost(transportCosts);
			            
			            // load intent and show summary activity
						Intent intent = new Intent(getApplicationContext(), ProjectedSalesActivity.class);
						intent.putExtra("source", "Market: ");
						startActivity(intent);
				    }
				    else {
				    	Toast toast = Toast.makeText(getApplicationContext(),
								"Please enter a transport cost",
								Toast.LENGTH_LONG);
						toast.show();
				    }
				}
	        	
	        });
	        
	        backButton = (Button) findViewById(R.id.back_find_markets);
	        backButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), FindMarketsActivity.class);
					startActivity(intent);
				}
	        });
	    }
}
