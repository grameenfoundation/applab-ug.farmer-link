package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DistrictsProviderAPI {
	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.districts";
	
	
	private DistrictsProviderAPI() {
		
	}
	
	public static final class DistrictsColumns implements BaseColumns {
		private DistrictsColumns() {
			
		}
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/districts");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.district";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.district";
        
        public static final String DISTRICT_NAME = "districtName";
	}
}
