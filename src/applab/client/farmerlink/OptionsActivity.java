package applab.client.farmerlink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OptionsActivity extends Activity {
    /** Called when the activity is first created. */
	
	private Button nextButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
        
        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new OnClickListener() {
        	

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), AddFarmersActivity.class);
				startActivity(i);
			}
        });
    }
}