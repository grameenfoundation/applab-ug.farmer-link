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
import android.widget.ListView;
import android.widget.TextView;

public class FindSuppliersActivity extends ListActivity {
	
	String district;
	String crop;
	List<Suppliers> suppliers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.find_suppliers);
		
	    district = MarketSaleObject.getMarketObject().getDistrictName();
        crop = MarketSaleObject.getMarketObject().getCropName();
        String displayTitle = this.getString(R.string.app_name) + " - " + crop;
        setTitle(displayTitle);
        
        suppliers = Repository.getSuppliersByDistrictAndCrop(crop, district);
        setListAdapter(new SuppliersAdapter());
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
     

	class SuppliersAdapter extends ArrayAdapter<Suppliers> {
		
		SuppliersAdapter() {
			super(FindSuppliersActivity.this, R.layout.suppliers_list, R.id.name, suppliers);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			
			View row = inflater.inflate(R.layout.suppliers_list, parent, false);
			TextView supplierView = (TextView) row.findViewById(R.id.name);
			supplierView.setText("Name : " +suppliers.get(position).getSupplierName());
			
			TextView telephoneView = (TextView) row.findViewById(R.id.telephone);
			telephoneView.setText("Telephone : " + suppliers.get(position).getSupplierContact());
			
			TextView locationView = (TextView) row.findViewById(R.id.location);
			locationView.setText("Location : " + suppliers.get(position).getSupplierLocation());
			
			return row;
		}
	}
	
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Suppliers supplier = suppliers.get(position);
        String url = "tel:"+supplier.getSupplierContact();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        startActivity(intent);
    }
}
