package applab.client.farmerlink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FinishSellActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_sell);
        if (MarketSaleObject.getMarketObject() != null) {
        	MarketSaleObject.getMarketObject().setCropName(null);
        	MarketSaleObject.getMarketObject().setDistrictName(null);
        	MarketSaleObject.getMarketObject().setFarmers(null);
        	MarketSaleObject.getMarketObject().setMarketPrices(null);
        	MarketSaleObject.getMarketObject().setBuyer(null);
        }
        
        Button nextButton = (Button)findViewById(R.id.finish);
        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO: Add committing information and logging transaction
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
