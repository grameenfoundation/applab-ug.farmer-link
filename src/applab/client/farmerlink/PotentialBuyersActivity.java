package applab.client.farmerlink;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PotentialBuyersActivity extends ListActivity {
    
    List<Buyer> buyers;
    private TextView cropTextView;
    String crop;
    String district;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.potential_buyers);
        
        cropTextView = (TextView)findViewById(R.id.crop);
	    district = MarketSaleObject.getMarketObject().getDistrictName();
        crop = MarketSaleObject.getMarketObject().getCropName();
        
        cropTextView.setText("Crop : " + crop);
        
        buyers = Repository.getBuyersByDistrictAndCrop(crop, district);

        setListAdapter(new BuyersAdapter());
        
        Button nextButton = (Button) findViewById(R.id.next_transport_estimate_buyer);
        nextButton.setOnClickListener(new Button.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TransportEstimatorBuyerActivity.class);
                startActivity(intent);
            }
        });
        
        Button backButton = (Button) findViewById(R.id.back_find_markets);
        backButton.setOnClickListener(new Button.OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), FindMarketsActivity.class);
				startActivity(intent);
        }});
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Buyer buyer = buyers.get(position);
        String url = "tel:"+buyer.getTelephone();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        startActivity(intent);
        
    }
    
    class BuyersAdapter extends ArrayAdapter<Buyer> {
        
        BuyersAdapter() {
            super(PotentialBuyersActivity.this, R.layout.potential_buyers_list, R.id.name, buyers);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            
            View row = inflater.inflate(R.layout.potential_buyers_list, parent, false);
            TextView marketView = (TextView) row.findViewById(R.id.name);
            marketView.setText("Name : " + buyers.get(position).getName());
           
            TextView retailPriceView = (TextView) row.findViewById(R.id.telephone);
            retailPriceView.setText("Telephone : " + buyers.get(position).getTelephone());
            
            TextView wholesalePriceView = (TextView) row.findViewById(R.id.location);
            wholesalePriceView.setText("Location : " + buyers.get(position).getLocation());
            
            return row;
        }        
    }
}
