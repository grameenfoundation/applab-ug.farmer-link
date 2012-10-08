package applab.client.farmerlink;

import java.util.Collections;
import java.util.List;

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
	private Spinner districtSpinner;
	private List<String> districts;
	private List<String> crops;

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

		districts = Repository.getDistricts(getString(R.string.server) + "/" + "FarmerLink"
				+ getString(R.string.districts_crops));
		Collections.sort(districts);
		districts.add(0, "Select District");
		crops = Repository.getCropsFromDb();
		if (crops.contains("Select Crop")) {
			crops.remove("Select crop");
		}
		crops.add(0, "Select Crop");

		if (selectedOption.equalsIgnoreCase("selling")) {

			districtSpinner = (Spinner) findViewById(R.id.district_spinner);
			districtSpinner.setAdapter(null);
			ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(
					this, R.layout.custom_spinner, districts.toArray(new String[districts.size()]));
			districtAdapter
					.setDropDownViewResource(R.layout.custom_spinner);
			districtSpinner.setAdapter(districtAdapter);
			districtSpinner.setOnItemSelectedListener(this);
			
			//If a district has already been selected (maybe CKW is coming "back"
			//set that district to that value
			if(MarketSaleObject.getMarketObject().getDistrictName() != null) {
				int districtPosition = districtAdapter.getPosition(MarketSaleObject.getMarketObject().getDistrictName());
				districtSpinner.setSelection(districtPosition);
			}

			Spinner cropSpinner = (Spinner) findViewById(R.id.crop_spinner);
			ArrayAdapter<String> cropAdapter = new ArrayAdapter<String>(this,
					R.layout.custom_spinner, crops.toArray(new String[crops.size()]));
			cropAdapter
					.setDropDownViewResource(R.layout.custom_spinner);
			cropSpinner.setAdapter(cropAdapter);
			cropSpinner.setOnItemSelectedListener(this);
			
			if(MarketSaleObject.getMarketObject().getCropName() != null) {
				int cropPosition = cropAdapter.getPosition(MarketSaleObject.getMarketObject().getCropName());
				cropSpinner.setSelection(cropPosition);
			}
			

		} else if (selectedOption.equalsIgnoreCase("buying")) {
			
			districtSpinner = (Spinner) findViewById(R.id.district_spinner);
			ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(
					this, R.layout.custom_spinner, districts.toArray(new String[districts.size()]));
			districtAdapter
					.setDropDownViewResource(R.layout.custom_spinner);
			districtSpinner.setAdapter(districtAdapter);
			districtSpinner.setOnItemSelectedListener(this);
			
			//If a district has already been selected (maybe CKW is coming "back"
			//set that district to that value
			if(MarketSaleObject.getMarketObject().getDistrictName() != null) {
				int districtPosition = districtAdapter.getPosition(MarketSaleObject.getMarketObject().getDistrictName());
				districtSpinner.setSelection(districtPosition);
			}

			Spinner cropSpinner = (Spinner) findViewById(R.id.crop_spinner);
			ArrayAdapter<String> cropAdapter = new ArrayAdapter<String>(this,
					R.layout.custom_spinner, crops.toArray(new String[crops.size()]));
			cropAdapter
					.setDropDownViewResource(R.layout.custom_spinner);
			cropSpinner.setAdapter(cropAdapter);
			cropSpinner.setOnItemSelectedListener(this);
			
			if(MarketSaleObject.getMarketObject().getCropName() != null) {
				int cropPosition = cropAdapter.getPosition(MarketSaleObject.getMarketObject().getCropName());
				cropSpinner.setSelection(cropPosition);
			}

		}
	}

	public void buttonClicked(View view) {

		switch (view.getId()) {
		case R.id.back_button:
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
			break;
		case R.id.next_button:
			if (selectedDistrict.equalsIgnoreCase("Select District") && selectedCrop.equalsIgnoreCase("Select Crop")) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Please select a district and a crop",
						Toast.LENGTH_LONG);
				toast.show();
			}
			else if (selectedDistrict.equalsIgnoreCase("Select District")) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Please select a district",
						Toast.LENGTH_LONG);
				toast.show();
			}
			else if (selectedCrop.equalsIgnoreCase("Select Crop")) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Please select a crop",
						Toast.LENGTH_LONG);
				toast.show();
			}
			else if (selectedOption.equalsIgnoreCase("selling") && (!selectedCrop.equalsIgnoreCase("Select Crop") && (!selectedDistrict.equalsIgnoreCase("Select District")))) {
				Intent nextIntent = new Intent(this,
						AddFarmersActivity.class);
				MarketSaleObject.getMarketObject()
						.setCropName(selectedCrop);
				MarketSaleObject.getMarketObject().setDistrictName(
						selectedDistrict);
				nextIntent.putExtra("source", "FindFarmer");
				startActivity(nextIntent);
			
			} 
			else if (selectedOption.equalsIgnoreCase("buying") && (!selectedCrop.equalsIgnoreCase("Select Crop") && (!selectedDistrict.equalsIgnoreCase("Select District")))) {
				Intent buyingIntent = new Intent(this,
						AddBuyerActivity.class);
				MarketSaleObject.getMarketObject().setCropName(selectedCrop);
				MarketSaleObject.getMarketObject().setDistrictName(
						selectedDistrict);
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
