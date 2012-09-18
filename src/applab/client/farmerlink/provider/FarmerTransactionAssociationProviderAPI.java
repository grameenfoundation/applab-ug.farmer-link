package applab.client.farmerlink.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class FarmerTransactionAssociationProviderAPI {
	public static final String AUTHORITY = "applab.client.farmerlink.provider.farmerlink.farmertransactionassociations";
	public static final String SYNCHED = "synched";
	public static final String UNSYNCHED = "unsynched";
	
	public static final class FarmerTransactionAssociationColumns implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/farmertransactionassociations");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.farmerlink.farmertransactionassociation";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.farmerlink.farmertransactionassociation";
        
        public static final String FARMER_QUOTA = "quota";
        public static final String TRANSACTION_ID = "transactionId";
        public static final String FARMER_ID = "farmerId";
        public static final String FARMER_NAME = "farmerName";
        public static final String FARMER_REVENUE = "farmerRevenue";
        public static final String TRANSACTION_FEE_QUOTA = "transactionFee";
        public static final String TRANSPORT_FEE_QUOTA = "transportFee";
        public static final String STATUS = "status";
	}
}
