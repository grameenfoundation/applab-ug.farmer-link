package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class FarmerVersionProviderAPI {

	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.farmersVersion";
	
	private FarmerVersionProviderAPI() {
		
	}
	
	public static final class FarmerVersionsColumns implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/farmerVersions");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.farmerVersion";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.farmerVersion";
        
        public static final String VERSION = "version";
        public static final String CROP_ID = "cropId";
        public static final String DISTRICT_ID = "districtId";
	}
}
