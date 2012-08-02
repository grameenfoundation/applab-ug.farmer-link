package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class CropsProviderAPI {

	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.crops";
	
	private CropsProviderAPI() {
		
	}
	
	public static final class CropsColumns implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/crops");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.crop";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.crop";
        
        public static final String CROP_NAME = "cropName";
	}
}
