package applab.client.farmerlink;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class AddFarmersActivity extends ListActivity {
	ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<String> farmers  = new ArrayList<String>();
	ArrayAdapter<String> addedFarmersAdapter;
	private Button addFarmerButton;
	private Button nextButton;
	private Button backButton;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_farmers);
        
        // Pick data from intent
        Intent intent = getIntent();
        String district = intent.getStringExtra("district");
        String crop  = intent.getStringExtra("crop");
        
        addedFarmersAdapter = new ArrayAdapter<String>(this, R.layout.simple_list, R.id.sampletext, listItems);
        setListAdapter(addedFarmersAdapter);   

        farmers = Repository.getFarmersByDistrictAndCrop(district, crop);
        // Add adapter for getting farmers
        // TODO: Change this to cursorAdapter
        AutoCompleteTextView farmerName = (AutoCompleteTextView) findViewById(R.id.farmer);
        ArrayAdapter<String> farmerAdapter = 
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, farmers);
        farmerName.setAdapter(farmerAdapter);
        
        addFarmerButton = (Button) findViewById(R.id.addFarmerButton);
        addFarmerButton.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View arg0) {
        	    AutoCompleteTextView farmerName = (AutoCompleteTextView) findViewById(R.id.farmer);
				EditText quantity = (EditText) findViewById(R.id.quantity);
				listItems.add(farmerName.getText().toString() + ", " + quantity.getText().toString());
				farmerName.setText("");
				quantity.setText("");
				addedFarmersAdapter.notifyDataSetChanged();
			}
        });
        
        nextButton = (Button) findViewById(R.id.next_market_prices);
        nextButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindMarketsActivity.class);
                intent.putExtra(GlobalConstants.SELECTED_FARMERS, listItems);
                startActivity(intent);
            }
        });
        
        backButton = (Button) findViewById(R.id.back_find_farmers);
        backButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
                startActivity(intent);
                
            }
        });
    }
}
