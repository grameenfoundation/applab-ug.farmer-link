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

public class AddBuyerActivity extends Activity {
    
    private Button nextButton;
    private Button backButton;
    private EditText buyerText;
    private EditText quantityText;
    private TextView cropTextView; 
    private EditText priceText;
    private double quantity;
    private String selectedOption;
    private String crop;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_buyer);
        cropTextView = (TextView)findViewById(R.id.commodity_name);
        cropTextView.setText(MarketSaleObject.getMarketObject().getCropName());
        selectedOption = MarketSaleObject.getMarketObject().getSelectedOption();
        
        crop = MarketSaleObject.getMarketObject().getCropName();
        String displayTitle = this.getString(R.string.app_name) + " - " + crop;
        setTitle(displayTitle);
        
        nextButton = (Button) findViewById(R.id.next_select_suppliers);
        
        nextButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                String buyerName;
                buyerText = (EditText) findViewById(R.id.buyer_name);
                buyerName = buyerText.getText().toString();
                priceText = (EditText) findViewById(R.id.price_value);
                String priceTextValue = priceText.getText().toString();
                if (priceTextValue.trim().length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Please enter a price",
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                
                MarketSaleObject.getMarketObject().setMarketPrices(new MarketPrices(buyerName, priceTextValue, priceTextValue));
                
                quantityText = (EditText) findViewById(R.id.quantity_amount);
                if (quantityText.getText().toString().trim().length() > 0) {
                    quantity = Double.parseDouble(quantityText.getText().toString());
                    MarketSaleObject.getMarketObject().setInitialQuantity(quantity);
                    
                    // load intent and show summary activity
                    Intent intent = new Intent(getApplicationContext(), FindSuppliersActivity.class);
                    intent.putExtra("source", "Buyer: ");
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
        
        backButton = (Button) findViewById(R.id.back_find_farmers);
        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindFarmersActivity.class);
                startActivity(intent);
            }
            
        });
    }

}
