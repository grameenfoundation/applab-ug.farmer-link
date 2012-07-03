package applab.client.farmerlink;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

public class AddFarmersActivity extends ListActivity {
	ArrayList<String> listItems = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	private Button addFarmerButton;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_farmers);
        adapter = new ArrayAdapter<String>(this, R.layout.simple_list, R.id.sampletext, listItems);
        setListAdapter(adapter);
        listItems.add("TEST DATA");
        addFarmerButton = (Button) findViewById(R.id.addFarmerButton);
        addFarmerButton.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View arg0) {
				EditText farmerName = (EditText) findViewById(R.id.farmer);
				EditText quantity = (EditText) findViewById(R.id.quantity);
				listItems.add(farmerName.getText().toString() + ", " + quantity.getText().toString());
				adapter.notifyDataSetChanged();
			}
        });
    }
	
	/*class FarmerAdapter extends ArrayAdapter<String> {
		FarmerAdapter() {
			super(AddFarmersActivity.this, R.layout.simple_list, R.id.sampletext, listItems);
		}
	}*/
}
