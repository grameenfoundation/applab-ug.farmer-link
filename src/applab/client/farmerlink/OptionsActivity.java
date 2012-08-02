package applab.client.farmerlink;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import applab.client.farmerlink.MarketLinkApplication;

public class OptionsActivity extends ListActivity {
	List<String> options;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
        
        try {
        	MarketLinkApplication.createMarketLinkDirectories();
        	Repository.getDistricts(getString(R.string.server) + "/" + "FarmerLink"
    				+ getString(R.string.districts_crops));
        }
        catch (Exception ex) {
        	Log.e("EXCEPTION", ex.getMessage());
        }
        
        if (MarketSaleObject.getMarketObject() != null) {
        	MarketSaleObject.getMarketObject().setCropName(null);
        	MarketSaleObject.getMarketObject().setDistrictName(null);
        	MarketSaleObject.getMarketObject().setFarmers(null);
        }
        options = new ArrayList<String>();
        options.add("Selling");
        options.add("Buying");      

        setListAdapter(new OptionsAdapter());
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	String selectedOption = options.get(position).toString();
    	Log.d("OPTION", selectedOption);
    	Intent intent = new Intent(this, FindFarmersActivity.class);
    	if (selectedOption.equalsIgnoreCase("selling")) {
    		intent.putExtra("selectedOption", "selling");
    	} else if (selectedOption.equalsIgnoreCase("buying")) {
    		intent.putExtra("selectedOption", "buying");
    	}
		startActivity(intent);
    }

    class OptionsAdapter extends ArrayAdapter<String> {
    	OptionsAdapter() {
    		super(OptionsActivity.this, R.layout.options_list, R.id.select_option, options);
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		LayoutInflater inflater = getLayoutInflater();
    		
    		View row = inflater.inflate(R.layout.options_list, parent, false);
    		
    		ImageView iconView = (ImageView)row.findViewById(R.id.view_icon);
    		iconView.setImageResource(R.drawable.selling);
    		if (position == 1) {
    			iconView.setImageResource(R.drawable.buying);
    		}
    		
    		TextView optionView = (TextView) row.findViewById(R.id.select_option);
			optionView.setText(options.get(position).toString());
			return row;
    	}
    }
}