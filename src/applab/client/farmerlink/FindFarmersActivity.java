package applab.client.farmerlink;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import applab.client.farmerlink.listeners.DistrictsAndCropsDownloadListener;
import applab.client.farmerlink.parsers.DistrictsAndCropsParser;
import applab.client.farmerlink.tasks.DistrictsAndCropsDownloadTask;
import applab.client.farmerlink.tasks.DownloadDistrictsAndCrops;
import applab.client.farmerlink.tasks.FarmersAndMarketPricesDownload;
import applab.client.farmerlink.tasks.FarmersAndMarketPricesDownloadTask;

public class FindFarmersActivity extends Activity implements OnItemSelectedListener, DistrictsAndCropsDownloadListener {
	
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
	    	/*DistrictsAndCropsDownloadTask districtsAndCropsTask = new DistrictsAndCropsDownloadTask();
	    	districtsAndCropsTask.setDownloaderListener(this);
	    	districtsAndCropsTask.execute();*/
	    	DownloadDistrictsAndCrops ddc = new DownloadDistrictsAndCrops();
	    	ddc.download();
	    	
	    	String [] districts = new String[] {"Select District", "Abim", "Pader", "Kitgum", "Nwoya"};
	    	districts = DistrictsAndCropsParser.getDistricts().toArray(new String[DistrictsAndCropsParser.getDistricts().size()]);
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
				if (!selectedDistrict.equalsIgnoreCase("Select District") && !selectedCrop.equalsIgnoreCase("Select Crop")) {
					FarmersAndMarketPricesDownload farmersAndMarketPricesDownload = new FarmersAndMarketPricesDownload(selectedDistrict, selectedCrop);
					List<String> farmersAndMarketPrices = farmersAndMarketPricesDownload.downloadFarmersAndMarketPrices();
					//MarketSaleObject.getMarketObject().setFarmers(farmersAndMarketPrices);
					//MarketSaleObject.getMarketObject().setMarketPrices(marketPrices);
					Intent nextIntent = new Intent(this, AddFarmersActivity.class);
					nextIntent.putStringArrayListExtra("farmersAndMarketPrices", (ArrayList<String>) farmersAndMarketPrices);
					
					MarketSaleObject.getMarketObject().setCropName(selectedCrop);
					MarketSaleObject.getMarketObject().setDistrictName(selectedDistrict);
					startActivity(nextIntent);
				}
		    else {
	          Toast toast = Toast.makeText(getApplicationContext(), "Please select a district and a crop", Toast.LENGTH_LONG);
	          toast.show();
	       }	
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

	@Override
	public void districtsAndCropsDownloadingComplete(List<String> result) {
		Log.i("DOWNLOAD COMPLETE", "done with download");
	}

}
