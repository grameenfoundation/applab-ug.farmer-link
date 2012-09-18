package applab.client.farmerlink;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PotentialBuyersActivity extends ListActivity {

	List<Buyer> buyers = new ArrayList<Buyer>();
	private TextView cropTextView;
	String crop;
	String district;
	static final int PROGRESS_DIALOG = 0;
	ProgressDialog progressDialog;
	private static final Pattern numberPattern = Pattern.compile("^7\\d{8}$");

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.potential_buyers);

	}
	
	@Override
	public void onResume() {
		super.onResume();

		cropTextView = (TextView) findViewById(R.id.crop);
		district = MarketSaleObject.getMarketObject().getDistrictName();
		crop = MarketSaleObject.getMarketObject().getCropName();
		String displayTitle = this.getString(R.string.app_name) + " - " + crop;
		setTitle(displayTitle);

		cropTextView.setText("Crop : " + crop);

		//if (Repository.buyersInDb(district, crop)) {
		new LoadBuyers().execute(getString(R.string.server) + "/" + "FarmerLink" + getString(R.string.buyers));
		ListView listView = (ListView) findViewById(android.R.id.list);
		registerForContextMenu(listView);
		if(buyers.size()==0) {
			buyers.add(new Buyer("NONE", null, null));
		}
		
		setListAdapter(new BuyersAdapter());
		
		Log.i("BUYERCOUNT", String.valueOf(buyers.size()));
		for (Buyer buyer : buyers) {
			Log.i("BUYER", buyer.getName() + "-" + buyer.getLocation());
		}

		Button backButton = (Button) findViewById(R.id.back_find_markets);
		backButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						FindMarketsActivity.class);
				startActivity(intent);
			}
		});
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		Buyer buyer = buyers.get(position);
		String url = "tel:" + formatNumber(buyer.getTelephone());
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
		startActivity(intent);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Select option");
		String edit = "Call buyer";
		String delete = "Select buyer";
		menu.add(0, v.getId(), 0, edit);
		menu.add(0, v.getId(), 0, delete);
	}
	/*
	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
	    if (isFinalized) {
	    	menu.getItem(1).setEnabled(false);
	    }
	    return false;
	}*/
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Buyer buyer = buyers.get((int) info.id);
		Log.d("buyer", buyer.getName());
		if ((buyer !=null) && !(buyer.getName().equalsIgnoreCase("NONE"))) {
		if(item.getTitle() =="Call buyer") {
			callBuyer(info.id);
		} else if (item.getTitle() == "Select buyer") {
			selectBuyer(info.id);
		} else {
			return false;
		}
		} else {
			return false;
		}
		return true;
	}

	
	private void callBuyer(long id) {
		Buyer buyer = buyers.get((int) id);
		String url = "tel:" + formatNumber(buyer.getTelephone());
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
		startActivity(intent);
		
	}
	
	private void selectBuyer(long id) {
		Buyer buyer = buyers.get((int) id);
		MarketSaleObject.getMarketObject().setBuyer(buyer);
		Intent intent = new Intent(getApplicationContext(),
				TransportEstimatorBuyerActivity.class);
		startActivity(intent);
	}


	class BuyersAdapter extends ArrayAdapter<Buyer> {

		BuyersAdapter() {
			super(PotentialBuyersActivity.this, R.layout.potential_buyers_list,
					R.id.name, buyers);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();

			View row = inflater.inflate(R.layout.potential_buyers_list, parent,
					false);
			Log.d("Number of buyers:", String.valueOf(buyers.size()));

			if(buyers.size()> 0) {
				TextView marketView = (TextView) row.findViewById(R.id.name);
				TextView retailPriceView = (TextView) row.findViewById(R.id.telephone);
				if(buyers.get(position).getName().equalsIgnoreCase("NONE")) {
					retailPriceView.setText("No buyers found for " + crop + " in " + district);
				} else {
				
					marketView.setText("Name : " + buyers.get(position).getName());
					

					retailPriceView.setText("Telephone : "
							+ formatNumber(buyers.get(position).getTelephone()));

					TextView wholesalePriceView = (TextView) row
							.findViewById(R.id.location);
					wholesalePriceView.setText("Location : "
							+ buyers.get(position).getLocation());
				}
			} else {
				TextView marketView = (TextView) row.findViewById(R.id.name);
				marketView.setText("No buyers found for " + crop + " in " + district);
			}
			return row;
		}
	}
	
	protected Dialog onCreateDialog(int dialogId) {
		switch(dialogId) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(PotentialBuyersActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle("Downloading content...");
			progressDialog.setMessage("Content not found on phone. Please wait while it is downloaded.");
			return progressDialog;
		default:
			return null;
		}
	}
	
	public String formatNumber(String telephone) {
		Matcher numberMatcher = numberPattern.matcher(telephone);
		if (numberMatcher.find()) {
			telephone = "0"+telephone;
		}
		return telephone;
	}


	private class LoadBuyers extends AsyncTask<String, Void, List<Buyer>> {

		@Override
		protected void onPreExecute() {
			showDialog(PROGRESS_DIALOG);
		}
		@Override
		protected List<Buyer> doInBackground(String... urls) {
			for (String url: urls) {
				return(Repository.getBuyersByDistrictAndCrop(url, district, crop));
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(List<Buyer> buyersList) {
	    	 buyers = buyersList;
	         dismissDialog(PROGRESS_DIALOG);
	 		ListView listView = (ListView) findViewById(android.R.id.list);
			registerForContextMenu(listView);
			if(buyers.size()==0) {
				buyers.add(new Buyer("NONE", null, null));
			}
			setListAdapter(new BuyersAdapter());
	     }
	}
}
