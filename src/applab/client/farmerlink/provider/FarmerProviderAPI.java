package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class FarmerProviderAPI {
	
	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.farmers";
	
	private FarmerProviderAPI() {
		
	}
	
	public static final class FarmerColumns implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/farmers");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.farmer";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.farmer";
        
        public static final String FARMER_NAME = "farmerName";
        public static final String FARMER_MOBILE = "mobileNumber";
        public static final String FARMER_ID = "farmerId";
        public static final String DISTRICT_ID = "districtId";
        public static final String CROP_ID = "cropId";
	}
}
