package applab.client.farmerlink;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import applab.client.farmerlink.tasks.DownloadBuyers;

public class PotentialBuyersActivity extends ListActivity {

	List<Buyer> buyers;
	private TextView cropTextView;
	String crop;
	String district;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.potential_buyers);

		cropTextView = (TextView) findViewById(R.id.crop);
		district = MarketSaleObject.getMarketObject().getDistrictName();
		crop = MarketSaleObject.getMarketObject().getCropName();
		String displayTitle = this.getString(R.string.app_name) + " - " + crop;
		setTitle(displayTitle);

		cropTextView.setText("Crop : " + crop);

		/*DownloadBuyers downloadBuyers = new DownloadBuyers(
				getString(R.string.server) + "/" + "FarmerLink"
						+ getString(R.string.buyers));
		buyers = downloadBuyers.downloadBuyers(district, crop);*/
		buyers = Repository.getBuyersByDistrictAndCrop(getString(R.string.server) + "/" + "FarmerLink"
				+ getString(R.string.buyers), district, crop);

		ListView listView = (ListView) findViewById(android.R.id.list);
		registerForContextMenu(listView);
		
		setListAdapter(new BuyersAdapter());

		Button nextButton = (Button) findViewById(R.id.next_transport_estimate_buyer);
		nextButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(),
						TransportEstimatorBuyerActivity.class);
				startActivity(intent);
			}
		});

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
		String url = "tel:" + buyer.getTelephone();
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
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if(item.getTitle() =="Call buyer") {
			callBuyer(info.id);
		} else if (item.getTitle() == "Select buyer") {
			selectBuyer(info.id);
		} else {
			return false;
		}
		return true;
	}

	
	private void callBuyer(long id) {
		Buyer buyer = buyers.get((int) id);
		String url = "tel:" + buyer.getTelephone();
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
		startActivity(intent);
		
	}
	
	private void selectBuyer(long id) {
		Buyer buyer = buyers.get((int) id);
		MarketSaleObject.getMarketObject().setBuyer(buyer);
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
			TextView marketView = (TextView) row.findViewById(R.id.name);
			marketView.setText("Name : " + buyers.get(position).getName());

			TextView retailPriceView = (TextView) row
					.findViewById(R.id.telephone);
			retailPriceView.setText("Telephone : "
					+ buyers.get(position).getTelephone());

			TextView wholesalePriceView = (TextView) row
					.findViewById(R.id.location);
			wholesalePriceView.setText("Location : "
					+ buyers.get(position).getLocation());

			return row;
		}
	}
}
