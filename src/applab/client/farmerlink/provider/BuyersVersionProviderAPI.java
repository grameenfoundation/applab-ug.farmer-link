package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class BuyersVersionProviderAPI {

	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.buyersVersion";
	
	private BuyersVersionProviderAPI() {
		
	}
	
	public static final class BuyersVersionColumns implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/buyersVersions");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.buyersVersion";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.buyersVersion";
        
        public static final String VERSION = "version";
        public static final String CROP_ID = "cropId";
        public static final String DISTRICT_ID = "districtId";
	}
}
