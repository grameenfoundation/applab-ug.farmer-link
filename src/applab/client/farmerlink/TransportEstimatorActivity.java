package applab.client.farmerlink;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import applab.client.farmerlink.FindMarketsActivity.PricesAdapter;

public class TransportEstimatorActivity extends Activity{
	 private Button nextButton;
	 private TextView marketTextView;
	 private TextView cropTextView;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.transport_estimator);
	        marketTextView = (TextView) findViewById(R.id.market_name);
	        marketTextView.setText(getIntent().getStringExtra("market"));
	        cropTextView = (TextView)findViewById(R.id.crop_name);
	        //cropTextView.setText(getIntent().getStringExtra("crop"));
	        nextButton = (Button) findViewById(R.id.next_transport_estimate);
	        
	        Intent i = getIntent();
	        final String marketName = i.getStringExtra("market");
	        //final String crop = i.getStringExtra("crop");
	        //Log.d("CROP", "crop is "+crop);
	        nextButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getApplicationContext(), ProjectedSalesActivity.class);
					intent.putExtra("market", marketName);
					//intent.putExtra("crop", crop);
					startActivity(intent);
				}
	        	
	        });
	    }
}
