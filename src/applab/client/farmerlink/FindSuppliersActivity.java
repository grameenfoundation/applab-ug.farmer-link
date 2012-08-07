package applab.client.farmerlink;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FindSuppliersActivity extends ListActivity {
	
	String district;
	String crop;
	List<Farmer> suppliers =  new ArrayList<Farmer>();;
    List<Farmer> allSuppliers;
    ProgressDialog progressDialog;
    static final int PROGRESS_DIALOG = 0;
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.find_suppliers);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
	    district = MarketSaleObject.getMarketObject().getDistrictName();
        crop = MarketSaleObject.getMarketObject().getCropName();
        String displayTitle = this.getString(R.string.app_name) + " - " + crop;
        setTitle(displayTitle);
        
        String url = getString(R.string.server) + "/"
				+ "FarmerLink"
				+ getString(R.string.farmers_market_prices);
        
		if (Repository.farmersInDb(district, crop)) {
			Log.d("ADD SUPPLIERS:", "Farmers in localdb");
			allSuppliers = Repository.getFarmersFromDb(district, crop);
	          if((allSuppliers == null) || (allSuppliers.size() == 0)) {
					allSuppliers.add(new Farmer("NONE", null, 0.0));
				}
			
		       // allSuppliers = Repository.getFarmersByDistrictAndCrop(url, district, crop);
		        for (Farmer farmer : allSuppliers) {
		        	if (null != farmer.getPhoneNumber() && farmer.getPhoneNumber().trim().length() > 0) {
		        		
		        		suppliers.add(farmer);
		        		Log.i("farmer no.", farmer.getPhoneNumber());
		        	}
		        }

		        setListAdapter(new SuppliersAdapter());
		} else {
			Log.d("ADD SUPPLIERS:", "Going to download the farmers");
			//downloadFarmers(url, district, crop);
			new DownloadFarmers().execute(url);
		}

	}
	
    
    public void buttonClicked (View view) {
    	switch (view.getId()) {
    	case R.id.next_add_farmers:
    		Intent nextIntent = new Intent(getApplicationContext(), AddFarmersActivity.class);
    		startActivity(nextIntent);
    		break;
    	case R.id.back_find_farmers:
    		Intent backIntent = new Intent(getApplicationContext(), FindFarmersActivity.class);
    		backIntent.putExtra("selectedOption", "buying");
    		startActivity(backIntent);
    		break;
    	}
    }
     

	class SuppliersAdapter extends ArrayAdapter<Farmer> {
		
		SuppliersAdapter() {
			super(FindSuppliersActivity.this, R.layout.suppliers_list, R.id.name, suppliers);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			
			View row = inflater.inflate(R.layout.suppliers_list, parent, false);
			TextView telephoneView = (TextView) row.findViewById(R.id.telephone);
			TextView supplierView = (TextView) row.findViewById(R.id.name);
			
			if (suppliers.size() > 0) {
				if( suppliers.get(position).getName().equalsIgnoreCase("NONE")) {
					telephoneView.setText("No farmers found to provide " + crop + " in "+ "district");
				} else {
					supplierView.setText("Name : " +properCase(suppliers.get(position).getName()));
					telephoneView.setText("Telephone : " + suppliers.get(position).getPhoneNumber());
				}
			} else {
				telephoneView.setText("No farmers found to provide " + crop + " in "+ "district");
			}
			
			return row;
		}
	}
	
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Farmer supplier = suppliers.get(position);
        String url = "tel:"+supplier.getPhoneNumber();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        startActivity(intent);
    }
    
	public static String properCase(String name) {
		java.io.StringReader in = new java.io.StringReader(name.toLowerCase());  
		boolean precededBySpace = true;  
		StringBuffer properCase = new StringBuffer();    
		try {
		while(true) {        
		        int i = in.read();  
		          if (i == -1)  break;        
		            char c = (char)i;  
		            if (c == ' ' || c == '"' || c == '(' || c == '.' || c == '/' || c == '\\' || c == ',') {  
		              properCase.append(c);  
		              precededBySpace = true;  
		           } else {  
		              if (precededBySpace) {   
		             properCase.append(Character.toUpperCase(c));  
		           } else {   
		                 properCase.append(c);   
		           }  
		           precededBySpace = false;  
		        }  
		        }  
		       
  
		} catch (Exception e) {
			
		}
	    return properCase.toString();   
	}

	protected Dialog onCreateDialog(int dialogId) {
		switch(dialogId) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(FindSuppliersActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle("Downloading content...");
			progressDialog.setMessage("Content not found on phone. Please wait while it is downloaded.");
			return progressDialog;
		default:
			return null;
		}
	}
	
    
    private class DownloadFarmers extends AsyncTask<String, Void, List<Farmer>> {

		@Override
		protected void onPreExecute() {
			showDialog(PROGRESS_DIALOG);
		}
		@Override
		protected List<Farmer> doInBackground(String... urls) {
			for (String url: urls) {
				return(Repository.getFarmersByDistrictAndCrop(url, district, crop));
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(List<Farmer> farmers) {
	    	 allSuppliers = farmers;
	         dismissDialog(PROGRESS_DIALOG);
	         if((allSuppliers == null) || (allSuppliers.size() == 0)) {
					allSuppliers.add(new Farmer("NONE", null, 0.0));
				}
			
	         // allSuppliers = Repository.getFarmersByDistrictAndCrop(url, district, crop);
	          for (Farmer farmer : allSuppliers) {
	          	if (null != farmer.getPhoneNumber() && farmer.getPhoneNumber().trim().length() > 0) {
	          		
	          		suppliers.add(farmer);
	          		Log.i("farmer no.", farmer.getPhoneNumber());
	          	}
	          }
	          setListAdapter(new SuppliersAdapter());
	     }
	}
}
