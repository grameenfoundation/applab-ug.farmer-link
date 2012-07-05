package applab.client.farmerlink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class OptionsActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();
		
		switch (view.getId()) {
		case R.id.radio_selling:
			if (checked) {
				Intent intent = new Intent(this, FindFarmersActivity.class);
				intent.putExtra("selectedOption", "selling");
				startActivity(intent);
			}
			break;
		case R.id.radio_buying:
			if (checked) {
				Intent intent = new Intent(this, FindFarmersActivity.class);
				intent.putExtra("selectedOption", "buying");
				startActivity(intent);
			}
			break;
		
		}
	}
}