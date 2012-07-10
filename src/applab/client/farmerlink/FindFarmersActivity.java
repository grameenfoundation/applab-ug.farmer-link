package applab.client.farmerlink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class FindFarmersActivity extends Activity implements OnItemSelectedListener {
	
	private String selectedDistrict;
	private String selectedCrop;
	private Button nextButton;
	private String selectedOption;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.find_farmers);
	    selectedOption = getIntent().getStringExtra("selectedOption");
        MarketSaleObject.getMarketObject().setSelectedOption(selectedOption);
	    
	    // Initialize Strings fro district and crops
	    selectedDistrict = "";
	    selectedCrop = "";
	    
	    nextButton = (Button) findViewById(R.id.next_button);
	    nextButton.setOnClickListener(new Button.OnClickListener() {
            
	        @Override
            public void onClick(View view) {
                buttonClicked(view);                
            }
        });
	    if (selectedOption.equalsIgnoreCase("selling")) {
	    	//Assume that we query the URL which will return the districts and crops
	    	//in JSON format (array of arrays)
	    	//Sample format that will be returned by URL
	    	// {"districts": ["Abim", "Pader","Nwoya","Kitgum"], "crops":["Simsim", "Beans","Bananas","Maize"]}
	   /* 	String jsonString = "{\"districts\": [\"Abim\", \"Pader\",\"Nwoya\",\"Kitgum\"], \"crops\":[\"Simsim\", \"Beans\",\"Bananas\",\"Maize\"]}";
	    	JSONParser parser = new JSONParser();
	    	Object parsedObject;
	    	
	    	try {
	    		parsedObject = parser.parse(jsonString);
	    	} catch(ParseException e) {
	    		
	    	}
*/	    	//At this point assume that you have an array of districts and another of crops
	    	String [] districts = new String[] {"Select District", "Abim", "Pader", "Kitgum", "Nwoya"};
	    	String [] crops = new String[] {"Select Crop", "Cotton", "Beans", "Bananas"};
	    	
	    	Spinner districtSpinner = (Spinner) findViewById(R.id.district_spinner);
	    	ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, districts);
	    	districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	districtSpinner.setAdapter(districtAdapter);
	    	districtSpinner.setOnItemSelectedListener(this);
	    	
	    	Spinner cropSpinner = (Spinner) findViewById(R.id.crop_spinner);
	    	ArrayAdapter<String> cropAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, crops);
	    	cropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	cropSpinner.setAdapter(cropAdapter);
	    	cropSpinner.setOnItemSelectedListener(this);
	    	
	    } else if (selectedOption.equalsIgnoreCase("buying")) {
	    	String [] districts = new String[] {"Select District", "Abim", "Pader", "Kitgum", "Nwoya"};
	    	String [] crops = new String[] {"Select Crop", "Cotton", "Beans", "Bananas"};
	    	
	    	Spinner districtSpinner = (Spinner) findViewById(R.id.district_spinner);
	    	ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, districts);
	    	districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	districtSpinner.setAdapter(districtAdapter);
	    	districtSpinner.setOnItemSelectedListener(this);
	    	
	    	Spinner cropSpinner = (Spinner) findViewById(R.id.crop_spinner);
	    	ArrayAdapter<String> cropAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, crops);
	    	cropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	cropSpinner.setAdapter(cropAdapter);
	    	cropSpinner.setOnItemSelectedListener(this);
	    	
	    }
	}
	
	public void buttonClicked (View view) {
		
		switch (view.getId()) {
		case R.id.back_button:
				Intent intent = new Intent(this, OptionsActivity.class);
				startActivity(intent);
			break;
		case R.id.next_button:
			if (selectedOption.equalsIgnoreCase("selling")) {
		    //if (!selectedDistrict.equals("") && !selectedCrop.equals("")) {
		        Intent nextIntent = new Intent(this, AddFarmersActivity.class);
		       /* Bundle bundle  = new Bundle();
	            bundle.putString(GlobalConstants.DISTRICT, selectedDistrict);
		        bundle.putString(GlobalConstants.CROP, selectedCrop);
		        //nextIntent.putExtras(bundle);
		        */
		        MarketSaleObject.getMarketObject().setCropName(selectedCrop);
		        MarketSaleObject.getMarketObject().setDistrictName(selectedDistrict);
	            startActivity(nextIntent);
		//    }
		//    else {
	    //      Toast.makeText(getApplicationContext(), "Please select a district and a crop", Toast.LENGTH_LONG);
	    //   }	
			} else if (selectedOption.equalsIgnoreCase("buying")) {
				Intent buyingIntent = new Intent(this, FindSuppliersActivity.class);
		        MarketSaleObject.getMarketObject().setCropName(selectedCrop);
		        MarketSaleObject.getMarketObject().setDistrictName(selectedDistrict);
				startActivity(buyingIntent);
			}
			break;
		
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.district_spinner:
			selectedDistrict = (String) parent.getItemAtPosition(position);
			break;
		case R.id.crop_spinner:
			selectedCrop = (String) parent.getItemAtPosition(position);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

}
