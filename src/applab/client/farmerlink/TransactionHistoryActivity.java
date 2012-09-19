package applab.client.farmerlink;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class TransactionHistoryActivity extends ListActivity {
    
    private TextView marketTextView;
    private TextView cropTextView;
    private TextView quantityTextView;
    private TextView priceTextView;
    private TextView totalValueTextView;
    private TextView transportTextView;
    private Button sellingButton;
    private Button buyingButton;
    private String crop;
    private String selectedOption = "Buying";
    
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history);
        setTextViews();
    }

    private void setTextViews() {
        // TODO Auto-generated method stub
        
    }
    

}