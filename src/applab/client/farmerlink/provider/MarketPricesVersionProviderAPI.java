package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MarketPricesVersionProviderAPI {
	
	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.marketpricesVersion";
	
	private MarketPricesVersionProviderAPI() {
		
	}
	
	public static final class MarketPricesVersionColumns implements BaseColumns {
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/marketpricesVersions");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.marketpricesVersion";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.marketpricesVersion";
        
        public static final String VERSION = "version";
        public static final String CROP_ID = "cropId";
        public static final String DISTRICT_ID = "districtId";
	}
}
