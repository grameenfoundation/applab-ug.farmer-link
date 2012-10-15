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

public class TransactionFeeBuyingActivity extends Activity{
     private Button nextButton;
     private Button backButton;
     private String crop;
     private double transactionFee;
     private EditText transactionFeeText;
     private String source;
     
     @Override
        public void onCreate(Bundle savedInstanceState) {
            
            super.onCreate(savedInstanceState);
            setContentView(R.layout.transaction_fee_buying);
            crop = MarketSaleObject.getMarketObject().getCropName();
            String displayTitle = this.getString(R.string.app_name) + " - " + crop;
            setTitle(displayTitle);
            nextButton = (Button) findViewById(R.id.next_finish_sell);
            source = getIntent().getStringExtra("source");
            
            nextButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    transactionFeeText = (EditText) findViewById(R.id.transaction_fee);
                    if (transactionFeeText.getText().toString().trim().length() > 0) {
                        transactionFee = Double.parseDouble(transactionFeeText.getText().toString());
                        MarketSaleObject.getMarketObject().setTransactionFee(transactionFee);
                        
                        // load intent and show summary activity
                        Intent intent = new Intent(getApplicationContext(), ActualSalesBuyingActivity.class);
                        intent.putExtra("source", source);
                        startActivity(intent);
                    }
                    else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Please enter a transaction fee",
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                }                
            });
            
            backButton = (Button) findViewById(R.id.back_projected_sales);
            backButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ProjectedSalesBuyingActivity.class);
                    intent.putExtra("source", source);
                    startActivity(intent);
                }
            });
        }
}
