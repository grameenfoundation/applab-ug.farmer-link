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
    ArrayList<String> farmers = new ArrayList<String>();
    ArrayList<Farmer> addedFarmers = new ArrayList<Farmer>();
    ArrayAdapter<String> addedFarmersAdapter;
    private Button addFarmerButton;
    private Button nextButton;
    private Button backButton;
    String district;
    String crop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_farmers);

        // Pick data from intent
        /*
         * Bundle bundle = getIntent().getBundleExtra("bundle"); district = bundle.getString(GlobalConstants.DISTRICT);
         * crop = bundle.getString(GlobalConstants.CROP);
         */
        district = MarketSaleObject.getMarketObject().getDistrictName();
        crop = MarketSaleObject.getMarketObject().getCropName();

        addedFarmersAdapter = new ArrayAdapter<String>(this, R.layout.simple_list, R.id.sampletext, listItems);
        setListAdapter(addedFarmersAdapter);

        farmers = Repository.getFarmersByDistrictAndCrop(district, crop);
        // Add adapter for getting farmers
        // TODO: Change this to cursorAdapter
        AutoCompleteTextView farmerName = (AutoCompleteTextView)findViewById(R.id.farmer);
        ArrayAdapter<String> farmerAdapter =
                new ArrayAdapter<String>(this, R.layout.simple_list, R.id.sampletext, farmers);
        farmerName.setAdapter(farmerAdapter);

        addFarmerButton = (Button)findViewById(R.id.addFarmerButton);
        addFarmerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AutoCompleteTextView farmerName = (AutoCompleteTextView)findViewById(R.id.farmer);
                EditText quantity = (EditText)findViewById(R.id.quantity);
                Farmer farmer = new Farmer("CD-2320", farmerName.getText().toString(), Double.parseDouble(quantity.getText().toString()));
                addedFarmers.add(farmer);
                listItems.add(farmer.toString());
                farmerName.setText("");
                quantity.setText("");
                addedFarmersAdapter.notifyDataSetChanged();
            }
        });

        nextButton = (Button)findViewById(R.id.next_market_prices);
        nextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	Intent intent;
            	if (MarketSaleObject.getMarketObject().getSelectedOption().equalsIgnoreCase("buying")) {
            		intent = new Intent(getApplicationContext(), TransportEstimatorBuyerActivity.class);
            	} else {
            		intent = new Intent(getApplicationContext(), FindMarketsActivity.class);
            	}
                // intent.putExtra(GlobalConstants.DISTRICT, district);
                // intent.putExtra(GlobalConstants.CROP, crop);
                MarketSaleObject.getMarketObject().setFarmers(addedFarmers);
                // intent.putExtra(GlobalConstants.ADDED_FARMERS, addedFarmers);
                startActivity(intent);
            }
        });

        backButton = (Button)findViewById(R.id.back_find_farmers);
        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	Intent intent;
            	if (MarketSaleObject.getMarketObject().getSelectedOption().equalsIgnoreCase("buying")) {
            		intent = new Intent(getApplicationContext(), FindSuppliersActivity.class);
            	} else {
            		intent = new Intent(getApplicationContext(), FindFarmersActivity.class);
            	}
                startActivity(intent);

            }
        });
    }
}
