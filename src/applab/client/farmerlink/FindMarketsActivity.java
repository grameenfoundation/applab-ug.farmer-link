package applab.client.farmerlink;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import applab.client.farmerlink.tasks.DownloadFarmersAndMarketPrices;
import applab.client.farmerlink.utilities.PricesFormatter;

public class FindMarketsActivity extends ListActivity {

	List<MarketPrices> marketPrices;
	private String commodityName = "Commodity Price for ";
	String crop;
	String district;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		district = MarketSaleObject.getMarketObject().getDistrictName();
		crop = MarketSaleObject.getMarketObject().getCropName();

		String displayTitle = this.getString(R.string.app_name) + " - " + crop;
		setTitle(displayTitle);

		commodityName += crop;

		marketPrices = Repository.getMarketPricesFromDb(district, crop);
		if (marketPrices.size() == 0) {
			marketPrices.add(new MarketPrices("NONE", null, "NO MARKET PRICES"));
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_markets);
		setListAdapter(new PricesAdapter());

		TextView commodityTextView = (TextView) findViewById(R.id.commodity_name);
		commodityTextView.setText(commodityName);

		Button findBuyersButton = (Button) findViewById(R.id.next_find_buyer);
		findBuyersButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(),
						PotentialBuyersActivity.class);
				startActivity(intent);

			}
		});

		Button backButton = (Button) findViewById(R.id.back_add_farmers);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						AddFarmersActivity.class);
				startActivity(intent);
			}
		});
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		MarketPrices market = marketPrices.get(position);
		if (!market.getMarketName().equalsIgnoreCase("NONE")) {
			Intent intent = new Intent(getApplicationContext(),
					TransportEstimatorActivity.class);
			MarketSaleObject.getMarketObject().setMarketPrices(market);
			startActivity(intent);
		}
		else {
			Intent finish = new Intent(this, FinishSellActivity.class);
			startActivity(finish);
		}
	}

	class PricesAdapter extends ArrayAdapter<MarketPrices> {

		PricesAdapter() {
			
			super(FindMarketsActivity.this, R.layout.market_prices_list,
					R.id.market_name, marketPrices);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.market_prices_list, parent,
					false);
			if (marketPrices.size() > 0) {
				TextView marketView = (TextView) row.findViewById(R.id.market_name);
				if (marketPrices.get(position).getMarketName().equalsIgnoreCase("NONE")) {
					TextView wholesalePriceView = (TextView) row
							.findViewById(R.id.wholesale_price);
					wholesalePriceView.setText(marketPrices.get(position).getWholesalePrice());
				}
				else {
					marketView.setText("Market Name : "
							+ marketPrices.get(position).getMarketName());
		
					TextView wholesalePriceView = (TextView) row
							.findViewById(R.id.wholesale_price);
					wholesalePriceView.setText("Wholesale Price : "
							+ PricesFormatter.formatPrice(marketPrices.get(position).getWholesalePriceValue()));
				}
			}
			else {
				TextView marketView = (TextView) row.findViewById(R.id.market_name);
				marketView.setText("NO MARKET PRICES FOUND");
			}
			return row;
		}
	}

}
