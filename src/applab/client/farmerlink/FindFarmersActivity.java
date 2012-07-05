package applab.client.farmerlink;

import java.util.ArrayList;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class FindFarmersActivity extends Activity implements OnItemSelectedListener {
	
	private String selectedDistrict;
	private String selectedCrop;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.find_farmers);
	    String selectedOption = getIntent().getStringExtra("selectedOption");
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
	    	ArrayAdapter districtAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, districts);
	    	districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	districtSpinner.setAdapter(districtAdapter);
	    	districtSpinner.setOnItemSelectedListener(this);
	    	
	    	Spinner cropSpinner = (Spinner) findViewById(R.id.crop_spinner);
	    	ArrayAdapter cropAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, crops);
	    	cropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	cropSpinner.setAdapter(cropAdapter);
	    	cropSpinner.setOnItemSelectedListener(this);
	    	
	    } else if (selectedOption.equalsIgnoreCase("buying")) {
	    	
	    }
	}
	
	public void buttonClicked (View view) {
		
		switch (view.getId()) {
		case R.id.back_button:
				Intent intent = new Intent(this, OptionsActivity.class);
				startActivity(intent);
			break;
		case R.id.next_button:
			Intent nextIntent = new Intent(this, AddFarmersActivity.class);
			nextIntent.putExtra("district", selectedDistrict);
			nextIntent.putExtra("crop", selectedCrop);
			startActivity(nextIntent);
			break;
		
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (view.getId()) {
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
