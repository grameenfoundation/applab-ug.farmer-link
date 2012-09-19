package applab.client.farmerlink;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class HomeActivity extends DashboardActivity {
    ProgressDialog progressDialog;
    static final int PROGRESS_DIALOG = 0;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_home);
	}
	
	/**
	 * onDestroy
	 * The final call you receive before your activity is destroyed. 
	 * This can happen either because the activity is finishing (someone called finish() on it, 
	 * or because the system is temporarily destroying this instance of the activity to save space. 
	 * You can distinguish between these two scenarios with the isFinishing() method.
	 *
	 */

	protected void onDestroy ()
	{
	   super.onDestroy ();
	}

	/**
	 * onPause
	 * Called when the system is about to start resuming a previous activity. 
	 * This is typically used to commit unsaved changes to persistent data, stop animations 
	 * and other things that may be consuming CPU, etc. 
	 * Implementations of this method must be very quick because the next activity will not be resumed 
	 * until this method returns.
	 * Followed by either onResume() if the activity returns back to the front, 
	 * or onStop() if it becomes invisible to the user.
	 *
	 */

	protected void onPause ()
	{
	   super.onPause ();
	}

	/**
	 * onRestart
	 * Called after your activity has been stopped, prior to it being started again.
	 * Always followed by onStart().
	 *
	 */

	protected void onRestart ()
	{
	   super.onRestart ();
	}

	/**
	 * onResume
	 * Called when the activity will start interacting with the user. 
	 * At this point your activity is at the top of the activity stack, with user input going to it.
	 * Always followed by onPause().
	 *
	 */

	protected void onResume ()
	{
	   super.onResume ();
       try {
       	MarketLinkApplication.createMarketLinkDirectories();
       	String url = getString(R.string.server) + "/" + "FarmerLink"
   				+ getString(R.string.districts_crops);
       	if(Repository.districtsinDb()) {
           	Repository.getDistrictsFromDb();
       	} else {
       		new DownloadDistricts().execute(url);
       	}
       	Repository.getDistricts(getString(R.string.server) + "/" + "FarmerLink"
   				+ getString(R.string.districts_crops));
       }
       catch (Exception ex) {
       	Log.e("EXCEPTION", ex.getMessage());
       }
       
       if (MarketSaleObject.getMarketObject() != null) {
       	MarketSaleObject.getMarketObject().setCropName(null);
       	MarketSaleObject.getMarketObject().setDistrictName(null);
       	MarketSaleObject.getMarketObject().setFarmers(null);
       	MarketSaleObject.getMarketObject().setMarketPrices(null);
       	MarketSaleObject.getMarketObject().setBuyer(null);
       }
	}

	/**
	 * onStart
	 * Called when the activity is becoming visible to the user.
	 * Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.
	 *
	 */

	protected void onStart ()
	{
	   super.onStart ();
	}

	/**
	 * onStop
	 * Called when the activity is no longer visible to the user
	 * because another activity has been resumed and is covering this one. 
	 * This may happen either because a new activity is being started, an existing one 
	 * is being brought in front of this one, or this one is being destroyed.
	 *
	 * Followed by either onRestart() if this activity is coming back to interact with the user, 
	 * or onDestroy() if this activity is going away.
	 */

	protected void onStop ()
	{
	   super.onStop ();
	}
	
    private class DownloadDistricts extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			showDialog(PROGRESS_DIALOG);
		}
		@Override
		protected Void doInBackground(String... urls) {
			for (String url: urls) {
				Repository.getDistricts(url);
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(Void ignoreReturnValue) {
	         //dismissDialog(PROGRESS_DIALOG);
	     }
	}
}
