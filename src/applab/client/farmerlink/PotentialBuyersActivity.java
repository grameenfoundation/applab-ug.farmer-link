package applab.client.farmerlink;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.potential_buyers);
        
        cropTextView = (TextView)findViewById(R.id.crop);
        cropTextView.setText("Crop : Sourgham");
        
        buyers = new ArrayList<Buyer>();
        
        buyers.add(new Buyer("Tom Cruise", "+5557777", "Hollywood"));
        buyers.add(new Buyer("Lionel Messi", "+6557777", "Barcelona"));
        buyers.add(new Buyer("Ibracadabra", "+7557777", "Milan"));
        buyers.add(new Buyer("Super Mario", "+8557777", "Manchester"));
        setListAdapter(new BuyersAdapter());
        
        Button findBuyersButton = (Button) findViewById(R.id.next_transport_estimate_buyer);
        findBuyersButton.setOnClickListener(new Button.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TransportEstimatorBuyerActivity.class);
                startActivity(intent);
                
            }
        });
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        
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
