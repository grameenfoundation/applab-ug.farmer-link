package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class TransactionProviderAPI {
	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.markettransactions";
	public static final String SYNCHED = "synched";
	public static final String UNSYNCHED = "unsynched";
	private TransactionProviderAPI() {
		
	}
	
	public static final class TransactionColumns implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/markettransactions");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.markettransaction";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.markettransaction";
        
        public static final String BUYER_NAME = "name";
        public static final String DISTRICT = "district";
        public static final String CROP = "crop";
        public static final String TRANSACTION_TYPE = "transactionType";
        public static final String TRANSACTION_DATE = "transactionDate";
        public static final String STATUS = "status";
        public static final String QUANTITY = "quantity";
        public static final String TRANSPORT_FEE = "transportFee";
        public static final String TRANSACTION_FEE = "transactionFee";
        public static final String UNITPRICE = "unitPrice";
	}
}
