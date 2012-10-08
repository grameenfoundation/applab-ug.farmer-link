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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FindSuppliersActivity extends ListActivity {
	
	String district;
	String crop;
	ArrayList<Farmer> suppliers =  new ArrayList<Farmer>();;
    List<Farmer> allSuppliers;
    ProgressDialog progressDialog;
    static final int PROGRESS_DIALOG = 0;
    private static final Pattern numberPattern = Pattern.compile("^7\\d{8}$");
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.find_suppliers);
	    MarketSaleObject.getMarketObject().setTransactionType(MarketSaleObject.BUY);
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
			allSuppliers = Repository.getFarmersFromDbBuying(district, crop);
	          if((allSuppliers == null) || (allSuppliers.size() == 0)) {
					allSuppliers.add(new Farmer("NONE", null, 0.0));
				}
			
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
    		 MarketSaleObject.getMarketObject().setFarmers(suppliers);
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
			TextView amountView = (TextView) row.findViewById(R.id.amount);
			TextView isSelectedView = (TextView) row.findViewById(R.id.selected);
			
			if (suppliers.size() > 0) {
				if( suppliers.get(position).getName().equalsIgnoreCase("NONE")) {
					telephoneView.setText("No farmers found to provide " + crop + " in "+ "district");
				} else {
					supplierView.setText("Name : " +properCase(suppliers.get(position).getName()));
					telephoneView.setText("Telephone : " + suppliers.get(position).getPhoneNumber());
					isSelectedView.setText("Selected : " + suppliers.get(position).isSelected());					
					
					if (crop.equalsIgnoreCase(suppliers.get(position).getCropOne())) {
					    amountView.setText(String.valueOf(suppliers.get(position).getAmountCropOne()) + " Kgs");
					}
					else if (crop.equalsIgnoreCase(suppliers.get(position).getCropTwo())) {
                        amountView.setText(String.valueOf(suppliers.get(position).getAmountCropTwo()) + " Kgs");
                    }
					else if (crop.equalsIgnoreCase(suppliers.get(position).getCropThree())) {
                        amountView.setText(String.valueOf(suppliers.get(position).getAmountCropThree()) + " Kgs");
                    }
				}
			} else {
				telephoneView.setText("No farmers found to provide " + crop + " in "+ "district");
			}
			
			return row;
		}
	}
	
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Farmer supplier = suppliers.get(position);
        String url = "tel:"+  formatNumber(supplier.getPhoneNumber());
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        startActivity(intent);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select option");
        String edit = "Call supplier";
        String delete = "Select supplier";
        menu.add(0, v.getId(), 0, edit);
        menu.add(0, v.getId(), 0, delete);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Farmer supplier = suppliers.get((int) info.id);
        Log.d("supplier", supplier.getName());
        if ((supplier !=null) && !(supplier.getName().equalsIgnoreCase("NONE"))) {
        if(item.getTitle() =="Call supplier") {
            callSupplier(info.id);
        } else if (item.getTitle() == "Select supplier") {
            selectSupplier(info.id);
        } else {
            return false;
        }
        } else {
            return false;
        }
        return true;
    }
    
    private void callSupplier(long id) {
        Farmer supplier = suppliers.get((int) id);
        String url = "tel:" + formatNumber(supplier.getPhoneNumber());
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        startActivity(intent);
        
    }    
    
    private void selectSupplier(long id) {
        Farmer supplier = suppliers.get((int) id);
        supplier.setSelected(true);
        
        // reset the list adapter so the selection change is reflected
        setListAdapter(new SuppliersAdapter());
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
	
	public String formatNumber(String telephone) {
        Matcher numberMatcher = numberPattern.matcher(telephone);
        if (numberMatcher.find()) {
            telephone = "0"+telephone;
        }
        return telephone;
    }
	
    
    private class DownloadFarmers extends AsyncTask<String, Void, List<Farmer>> {

		@Override
		protected void onPreExecute() {
			showDialog(PROGRESS_DIALOG);
		}
		@Override
		protected List<Farmer> doInBackground(String... urls) {
			for (String url: urls) {
				return(Repository.getFarmersByDistrictAndCropBuying(url, district, crop));
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
