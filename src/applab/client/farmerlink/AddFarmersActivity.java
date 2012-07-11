package applab.client.farmerlink;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AddFarmersActivity extends ListActivity implements TextWatcher {
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> farmers = new ArrayList<String>();
    ArrayList<Farmer> addedFarmers = new ArrayList<Farmer>();
    ArrayAdapter<String> addedFarmersAdapter;
    private Button addFarmerButton;
    private Button nextButton;
    private Button backButton;
    String district;
    String crop;
    private Double maximumQuantity = 1000.0;
    
    Context context = this; //http://code.google.com/p/android/issues/detail?id=11199
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_farmers);

        EditText quantity = (EditText)findViewById(R.id.quantity);
        quantity.addTextChangedListener(this);
        
        district = MarketSaleObject.getMarketObject().getDistrictName();
        crop = MarketSaleObject.getMarketObject().getCropName();
        String displayTitle = getResources().getString(R.string.app_name) + " - " + crop;
        setTitle(displayTitle);

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
                
                if ((Double.compare(maximumQuantity, Double.parseDouble(quantity.getText().toString()))) >= 0) {
                	final Farmer farmer = new Farmer("CD-2320", farmerName.getText().toString(), Double.parseDouble(quantity.getText().toString()));
                	//Check if selected farmer already exists in collection
                	if(addedFarmers.contains(farmer)) {
                		AlertDialog.Builder builder = new AlertDialog.Builder(context);
                		builder.setMessage("This farmer has already been added. Do you want to update their details?\n(Selecting no will ignore this farmer)")
                			   .setCancelable(false)
                		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                		    	   public void onClick(DialogInterface dialog, int id) {
                		    		   int position = 0;
                		    		   for(int intIndex = 0; intIndex < addedFarmers.size(); intIndex++) {
                		    			   //for (Farmer addedFarmer : addedFarmers) {
                		    			   if (addedFarmers.get(intIndex).getName().equalsIgnoreCase(farmer.getName())) {
                		    				   position = intIndex;
                		    			   }
                		    		   }
                		    		   addedFarmers.set(position, farmer);
                		    		   position = 0;
                		    		   for(int intIndex = 0; intIndex < listItems.size(); intIndex++) {
                		    			   if (listItems.get(intIndex).equalsIgnoreCase(farmer.toString())) {
                		    				   position = intIndex;
                		    			   }
                		    		   }
                		    		   listItems.set(position, farmer.toString());
                		    		   addedFarmersAdapter.notifyDataSetChanged();
                		    	   }
                		       })
                		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                		    	   public void onClick(DialogInterface dialog, int id) {
                		    		   //Do nothing
                		    	   }
                		       });
                	
                		AlertDialog alert = builder.create();
                		alert.show();
                	} else {
                		addedFarmers.add(farmer);
                		listItems.add(farmer.toString());
                	}
                	farmerName.setText("");
                	quantity.setText("");
                	addedFarmersAdapter.notifyDataSetChanged();
                     } else {
                    	 quantity.setText("");
        	          Toast toast = Toast.makeText(getApplicationContext(), "Quantity entered must be less than 1000kg", Toast.LENGTH_LONG);
        	          toast.show();
                  }
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
            		intent.putExtra("selectedOption", "selling");
            	}
                startActivity(intent);

            }
        });
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Farmer farmer = addedFarmers.get(position);
        AutoCompleteTextView farmerName = (AutoCompleteTextView)findViewById(R.id.farmer);
        EditText quantity = (EditText)findViewById(R.id.quantity);
        farmerName.setText(farmer.getName());
        quantity.setText(Double.toString(farmer.getQuantity()));
    }

	@Override
	public void afterTextChanged(Editable arg0) {
		//Nothing
        //EditText quantity = (EditText)findViewById(R.id.quantity);
        //if ((Double.compare(Double.parseDouble(quantity.getText().toString()), 1000.0) > 0)) {

        	//quantity.setText("");
        //}
        
        
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		
	}
    
}
