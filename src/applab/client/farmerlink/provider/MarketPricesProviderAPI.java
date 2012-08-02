package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MarketPricesProviderAPI {
	
	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.marketprices";
	
	private MarketPricesProviderAPI() {
		
	}
	
	public static final class MarketPricesColumns implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/marketprices");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.marketprice";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.marketprice";
        
        public static final String WHOLESALE_PRICE = "wholesalePrice";
        public static final String RETAIL_PRICE = "retailPrice";
        public static final String MARKET_NAME = "marketName";
        public static final String DISTRICT_ID = "districtId";
        public static final String CROP_ID = "cropId";
	}

}
