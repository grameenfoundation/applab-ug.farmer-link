package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class TransactionProviderAPI {
	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.transactions";
	public static final String SALE = "sale";
	public static final String BUY = "buy";
	
	public static final class TransactionColumns implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/transactions");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.transaction";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.transaction";
        
        public static final String BUYER_NAME = "name";
        public static final String BUYER_TELEPHONE = "telephone";
        public static final String DISTRICT = "district";
        public static final String CROP = "crop";
        public static final String CKW = "ckw";
        public static final String TRANSACTION_TYPE = "transactionType";
        public static final String TRANSACTION_DATE = "transactionDate";
	}
}
