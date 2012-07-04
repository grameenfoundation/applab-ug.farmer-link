package applab.client.farmerlink;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import applab.client.farmerlink.FindMarketsActivity.PricesAdapter;

public class ProjectedSalesActivity extends Activity {
	private TextView marketTextView;
	private TextView cropTextView;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projected_sales);
        marketTextView = (TextView)findViewById(R.id.market_name);
        marketTextView.setText(getIntent().getStringExtra("market"));
        //cropTextView = (TextView)findViewById(R.id.commodity_name);
        //cropTextView.setText(getIntent().getStringExtra("crop"));
    }
}
