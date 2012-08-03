package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class BuyerProviderAPI {
	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.buyers";
	
	private BuyerProviderAPI() {
		
	}
	
	public static final class BuyersColumns implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/buyers");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.buyer";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.buyer";
        
        public static final String BUYER_NAME = "name";
        public static final String BUYER_TELEPHONE = "telephone";
        public static final String BUYER_LOCATION = "location";
        public static final String DISTRICT_ID = "districtId";
        public static final String CROP_ID = "cropId";
	}
}
