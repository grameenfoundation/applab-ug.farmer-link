package applab.client.farmerlink;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ProjectedSalesActivity extends ListActivity {
    private TextView marketTextView;
    private TextView cropTextView;
    private TextView quantityTextView;
    private TextView priceTextView;
    private TextView totalValueTextView;
    private TextView transactFeeTextView;
    private TextView transportTextView;
    private Button nextButton;
    private Button backButton;
    
    private ArrayList<Farmer> farmers;
    private TextView marketView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.projected_sales);
        setTextViews();
        
        farmers = MarketSaleObject.getMarketObject().getFarmers();
        setListAdapter(new FarmerAdapter());

        nextButton = (Button)findViewById(R.id.next_finish);
        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO: Add commiting information and logging transaction
                Intent intent = new Intent(getApplicationContext(), FinishSellActivity.class);
                startActivity(intent);
            }
        });
        
        backButton = (Button) findViewById(R.id.back_estimate_transport);
        backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), TransportEstimatorBuyerActivity.class);
				startActivity(intent);
			}
        	
        });
    }

    /**
     * Sets text values
     */
    private void setTextViews() {
        
        marketView = (TextView)findViewById(R.id.market);
        marketView.setText(getIntent().getStringExtra("source"));
        
        marketTextView = (TextView)findViewById(R.id.market_name);
        marketTextView.setText(MarketSaleObject.getMarketObject().getMarketPrices().getMarketName());

        cropTextView = (TextView)findViewById(R.id.crop_name);
        cropTextView.setText(MarketSaleObject.getMarketObject().getCropName());

        quantityTextView = (TextView)findViewById(R.id.quantity_value);
        quantityTextView.setText(String.valueOf(MarketSaleObject.getMarketObject().getTotalQuantity()));

        priceTextView = (TextView)findViewById(R.id.kg_price_value);
        priceTextView.setText(String.valueOf(MarketSaleObject.getMarketObject().getMarketPrices().getRetailPrice()));

        totalValueTextView = (TextView)findViewById(R.id.total_value);
        totalValueTextView.setText(String.valueOf(MarketSaleObject.getMarketObject().getTotalValue()));

        transportTextView = (TextView)findViewById(R.id.transport_value);
        transportTextView.setText(String.valueOf(MarketSaleObject.getMarketObject().getTransportCost()));

        transactFeeTextView = (TextView)findViewById(R.id.transaction_fee_value);
        transactFeeTextView.setText(String.valueOf(MarketSaleObject.getMarketObject().getTotalTransactionFee()));
    }

    class FarmerAdapter extends ArrayAdapter<Farmer> {

        FarmerAdapter() {
            
            super(ProjectedSalesActivity.this, R.layout.projected_sales_list, R.id.farmer_name_text, farmers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            double revenue = farmers.get(position).computeRevenue(MarketSaleObject.getMarketObject().getMarketPrices().getRetailPriceValue());
            double transactionFee = farmers.get(position).computeTransactionFee(MarketSaleObject.getMarketObject().getMarketPrices().getRetailPriceValue());
            
            View row = inflater.inflate(R.layout.projected_sales_list, parent, false);
            TextView marketView = (TextView)row.findViewById(R.id.farmer_name_text);
            marketView.setText("Farmer Name : " + farmers.get(position).getName());

            TextView farmerIdView = (TextView)row.findViewById(R.id.farmer_id_text);
            farmerIdView.setText("Farmer ID : " + farmers.get(position).getId());
            
            TextView quantityView = (TextView)row.findViewById(R.id.quantity_text);
            quantityView.setText("Quantity : " + farmers.get(position).getQuantity());
          
            TextView revenueView = (TextView)row.findViewById(R.id.revenue_text);
            revenueView.setText("Revenue : " + revenue);
            
            TextView transportView = (TextView)row.findViewById(R.id.transport_text);
            transportView.setText("Transport Cost : " + MarketSaleObject.getMarketObject().getTransportCost());
            
            TextView transactionFeeView = (TextView)row.findViewById(R.id.transaction_fee_text);
            transactionFeeView.setText("Transaction Fee : " + transactionFee);           

            return row;
        }
    }
}
