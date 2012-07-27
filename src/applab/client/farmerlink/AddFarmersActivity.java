package applab.client.farmerlink;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import applab.client.farmerlink.parsers.MarketPricesParser;
import applab.client.farmerlink.tasks.DownloadFarmersAndMarketPrices;

public class AddFarmersActivity extends ListActivity implements TextWatcher {
	ArrayList<String> listItems = new ArrayList<String>();
	List<String> farmers = new ArrayList<String>();
	ArrayList<Farmer> addedFarmers = new ArrayList<Farmer>();
	ArrayAdapter<String> addedFarmersAdapter;
	private Button addFarmerButton;
	private Button nextButton;
	private Button backButton;
	String district;
	String crop;
	private Double maximumQuantity = 1000.0;

	Context context = this; // http://code.google.com/p/android/issues/detail?id=11199

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_farmers);

		EditText quantity = (EditText) findViewById(R.id.quantity);
		quantity.addTextChangedListener(this);

		district = MarketSaleObject.getMarketObject().getDistrictName();
		crop = MarketSaleObject.getMarketObject().getCropName();
		String displayTitle = getResources().getString(R.string.app_name)
				+ " - " + crop;
		setTitle(displayTitle);
		
		ArrayList<Farmer> existingFarmers = MarketSaleObject.getMarketObject().getFarmers();

		if ((existingFarmers != null) && (existingFarmers.size() > 0)) {
			for (Farmer existingFarmer: existingFarmers) {
				addedFarmers.add(existingFarmer);
				listItems.add(existingFarmer.toString());
			}
		}
		addedFarmersAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_list, R.id.sampletext, listItems);
		setListAdapter(addedFarmersAdapter);

		if (MarketPricesParser.getFarmers().size() > 0) {
			populateFarmersList();
		} else {
			DownloadFarmersAndMarketPrices downloadFarmersAndMarketPrices = new DownloadFarmersAndMarketPrices(
					getString(R.string.server) + "/"
							+ "FarmerLink"
							+ getString(R.string.farmers_market_prices));
			downloadFarmersAndMarketPrices.downloadFarmersAndMarketPrices(
					district, crop);
			populateFarmersList();
		}
		// Add adapter for getting farmers
		// TODO: Change this to cursorAdapter
		AutoCompleteTextView farmerName = (AutoCompleteTextView) findViewById(R.id.farmer);
		ArrayAdapter<String> farmerAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_list, R.id.sampletext, farmers);
		farmerName.setAdapter(farmerAdapter);

		ListView listView = (ListView) findViewById(android.R.id.list);
		registerForContextMenu(listView);

		addFarmerButton = (Button) findViewById(R.id.addFarmerButton);
		addFarmerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AutoCompleteTextView farmerName = (AutoCompleteTextView) findViewById(R.id.farmer);
				EditText quantity = (EditText) findViewById(R.id.quantity);
				
				if ((Double.compare(maximumQuantity,
						Double.parseDouble(quantity.getText().toString()))) >= 0) {
					final Farmer farmer = new Farmer("CD-2320", farmerName
							.getText().toString(), Double.parseDouble(quantity
							.getText().toString()));
					// Check if selected farmer already exists in collection
					if (addedFarmers.contains(farmer)) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setMessage(
								"This farmer has already been added. Do you want to update their details?\n(Selecting no will ignore this farmer)")
								.setCancelable(false)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												int position = 0;
												for (int intIndex = 0; intIndex < addedFarmers
														.size(); intIndex++) {
													// for (Farmer addedFarmer :
													// addedFarmers) {
													if (addedFarmers
															.get(intIndex)
															.getName()
															.equalsIgnoreCase(
																	farmer.getName())) {
														position = intIndex;
													}
												}
												addedFarmers.set(position,
														farmer);
												position = 0;
												for (int intIndex = 0; intIndex < listItems
														.size(); intIndex++) {
													if (listItems
															.get(intIndex)
															.equalsIgnoreCase(
																	farmer.toString())) {
														position = intIndex;
													}
												}
												listItems.set(position,
														farmer.toString());
												addedFarmersAdapter
														.notifyDataSetChanged();
											}
										})
								.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// Do nothing
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
					Toast toast = Toast.makeText(getApplicationContext(),
							"Quantity entered must be less than 1000kg",
							Toast.LENGTH_LONG);
					toast.show();
				}
			}

		});

		nextButton = (Button) findViewById(R.id.next_market_prices);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				if (MarketSaleObject.getMarketObject().getSelectedOption()
						.equalsIgnoreCase("buying")) {
					intent = new Intent(getApplicationContext(),
							TransportEstimatorBuyerActivity.class);
				} else {
					intent = new Intent(getApplicationContext(),
							FindMarketsActivity.class);
				}

				MarketSaleObject.getMarketObject().setFarmers(addedFarmers);
				startActivity(intent);
			}
		});

		backButton = (Button) findViewById(R.id.back_find_farmers);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				if (MarketSaleObject.getMarketObject().getSelectedOption()
						.equalsIgnoreCase("buying")) {
					intent = new Intent(getApplicationContext(),
							FindSuppliersActivity.class);
				} else {
					intent = new Intent(getApplicationContext(),
							FindFarmersActivity.class);
					intent.putExtra("selectedOption", "selling");
				}
				startActivity(intent);

			}
		});
	}

	private void populateFarmersList() {
		List<Farmer> farmerList = MarketPricesParser.getFarmers();
		for (Farmer farmer : farmerList) {
			farmers.add(farmer.getName());
		}
		Log.d("FARMER COUNT", String.valueOf(farmerList.size()));
	}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(menu, v, menuInfo);
			menu.setHeaderTitle("Select option");
			String edit = "Edit";
			String delete = "Remove";
			menu.add(0, v.getId(), 0, edit);
			menu.add(0, v.getId(), 0, delete);
		}
		
	/**	
	 * Obsoleted by the context menu edit option
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Farmer farmer = addedFarmers.get(position);
		AutoCompleteTextView farmerName = (AutoCompleteTextView) findViewById(R.id.farmer);
		EditText quantity = (EditText) findViewById(R.id.quantity);
		farmerName.setText(farmer.getName());
		quantity.setText(Double.toString(farmer.getQuantity()));
	}
	*/
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if(item.getTitle() =="Edit") {
			editSelectedFarmer(info.id);
		} else if (item.getTitle() == "Remove") {
			deleteSelectedFarmer(info.id);
		} else {
			return false;
		}
		return true;
	}

	private void deleteSelectedFarmer(final long selectedFarmerId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(
				"Please confirm that you want to remove this farmer from the selection.")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int id) {
								addedFarmers.remove((int) selectedFarmerId);
								listItems.remove((int) selectedFarmerId);
								addedFarmersAdapter.notifyDataSetChanged();
							}
						})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int id) {
								// Do nothing
							}
						});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void editSelectedFarmer(long id) {
		Farmer farmer = addedFarmers.get((int) id);
		AutoCompleteTextView farmerName = (AutoCompleteTextView) findViewById(R.id.farmer);
		EditText quantity = (EditText) findViewById(R.id.quantity);
		farmerName.setText(farmer.getName());
		quantity.setText(Double.toString(farmer.getQuantity()));
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// Nothing
		// EditText quantity = (EditText)findViewById(R.id.quantity);
		// if
		// ((Double.compare(Double.parseDouble(quantity.getText().toString()),
		// 1000.0) > 0)) {

		// quantity.setText("");
		// }

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

}
