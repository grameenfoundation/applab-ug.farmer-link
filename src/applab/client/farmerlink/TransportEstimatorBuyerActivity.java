package applab.client.farmerlink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TransportEstimatorBuyerActivity extends Activity {
    
    private Button nextButton;
    private Button backButton;
    private EditText buyerText;
    private TextView cropTextView;
    private TextView quantityView;  
    private EditText transportText;
    private EditText priceText;
    private double transportCosts;
    private String selectedOption;
    private String crop;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transport_estimator_find_buyer);        
        cropTextView = (TextView)findViewById(R.id.commodity_name);
        cropTextView.setText(MarketSaleObject.getMarketObject().getCropName());
        quantityView = (TextView) findViewById(R.id.quantity_amount);
        quantityView.setText(String.valueOf(MarketSaleObject.getMarketObject().getTotalQuantity()) + " Kg");
        selectedOption = MarketSaleObject.getMarketObject().getSelectedOption();
        
        crop = MarketSaleObject.getMarketObject().getCropName();
        String displayTitle = this.getString(R.string.app_name) + " - " + crop;
        setTitle(displayTitle);
        
        nextButton = (Button) findViewById(R.id.next_projected_sales);
        
        nextButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                buyerText = (EditText) findViewById(R.id.buyer_name);
                String buyerName = buyerText.getText().toString();
                
                priceText = (EditText) findViewById(R.id.price_value);
                String price = priceText.getText().toString();
                
                MarketSaleObject.getMarketObject().setMarketPrices(new MarketPrices(buyerName, price, price));
                
                transportText = (EditText) findViewById(R.id.transport_cost);
                transportCosts = Double.parseDouble(transportText.getText().toString());
                MarketSaleObject.getMarketObject().setTransportCost(transportCosts);
                
                // load intent and show summary activity
                Intent intent = new Intent(getApplicationContext(), ProjectedSalesActivity.class);
                intent.putExtra("source", "Buyer: ");
                //intent.putExtra("crop", crop);
                startActivity(intent);
            }
            
        });
        
        backButton = (Button) findViewById(R.id.back_find_markets);
        backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					Intent intent = null;
					if(selectedOption.equalsIgnoreCase("buying")) {
						intent = new Intent(getApplicationContext(), AddFarmersActivity.class);
					} else if (selectedOption.equalsIgnoreCase("selling")) {
						intent = new Intent(getApplicationContext(), PotentialBuyersActivity.class);
					}
	        		startActivity(intent);
			}
        	
        });
    }

}
