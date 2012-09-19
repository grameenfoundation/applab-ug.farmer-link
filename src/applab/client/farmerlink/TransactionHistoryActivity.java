package applab.client.farmerlink;

import android.app.TabActivity;
import android.widget.TabHost;
import android.widget.TextView;
import android.os.Bundle;

public class TransactionHistoryActivity extends TabActivity {
	private static TextView mTVFF;
    private static TextView mTVDF;

    private static final String BUYS_TAB = "buys_tab";
    private static final String SALES_TAB = "sales_tab";
    private static final int FONT_SIZE = 21;
    
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	final TabHost tabHost = getTabHost();
    }
}
