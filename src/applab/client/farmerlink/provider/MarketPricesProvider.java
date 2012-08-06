package applab.client.farmerlink.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import applab.client.farmerlink.database.MarketLinkSQLiteOpenHelper;
import applab.client.farmerlink.provider.FarmerProviderAPI.FarmerColumns;
import applab.client.farmerlink.provider.MarketPricesProviderAPI.MarketPricesColumns;

public class MarketPricesProvider extends ContentProvider {

	private static final String t = "MarketPricesProvider";

    private static final String DATABASE_NAME = "marketprices.db";
    private static final int DATABASE_VERSION = 1;
    private static final String MARKETPRICES_TABLE_NAME = "marketprices";

    private static final int MARKETPRICES = 1;
    private static final int MARKETPRICE_ID = 2;
    
    private static HashMap<String, String> sInstancesProjectionMap;
    private static final UriMatcher sUriMatcher;
    
    private static class DatabaseHelper extends MarketLinkSQLiteOpenHelper {

		public DatabaseHelper(String databaseName) {
			super("/sdcard/marketlink/databases", databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("DB CREATION", "creating Market Prices database");
			db.execSQL("CREATE TABLE " + MARKETPRICES_TABLE_NAME + " (" 
		               + MarketPricesColumns._ID + " integer primary key, " 
		               + MarketPricesColumns.WHOLESALE_PRICE + " text not null, "
		               + MarketPricesColumns.RETAIL_PRICE + " text, " 
		               + MarketPricesColumns.DISTRICT_ID + " text not null, " 
		               + MarketPricesColumns.CROP_ID + " text not null, " 
		               + MarketPricesColumns.MARKET_NAME + " text not null );"); 
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(t, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS districts");
            onCreate(db);
		}
    	
    }
    public DatabaseHelper mDbHelper;
    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri
        if (sUriMatcher.match(uri) != MARKETPRICES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(MARKETPRICES_TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri instanceUri = ContentUris.withAppendedId(MarketPricesColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(instanceUri, null);
            return instanceUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		mDbHelper = new DatabaseHelper(DATABASE_NAME);
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MARKETPRICES_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case MARKETPRICES:
                qb.setProjectionMap(sInstancesProjectionMap);
                break;

            case MARKETPRICE_ID:
                qb.setProjectionMap(sInstancesProjectionMap);
                qb.appendWhere(MarketPricesColumns._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // Get the database and run the query
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // Tell the cursor what uri to watch, so it knows when its source data changes
        String queryString = qb.buildQuery(null, selection, selectionArgs, null, null, sortOrder, null);
        Log.i("QUERYSTRING", queryString);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MarketPricesProviderAPI.AUTHORITY, "marketprices", MARKETPRICES);
        sUriMatcher.addURI(MarketPricesProviderAPI.AUTHORITY, "marketprices/#", MARKETPRICE_ID);

        sInstancesProjectionMap = new HashMap<String, String>();
        sInstancesProjectionMap.put(MarketPricesColumns._ID, MarketPricesColumns._ID);
        sInstancesProjectionMap.put(MarketPricesColumns.WHOLESALE_PRICE, MarketPricesColumns.WHOLESALE_PRICE);
        sInstancesProjectionMap.put(MarketPricesColumns.RETAIL_PRICE, MarketPricesColumns.RETAIL_PRICE);
        sInstancesProjectionMap.put(MarketPricesColumns.MARKET_NAME, MarketPricesColumns.MARKET_NAME);
        sInstancesProjectionMap.put(MarketPricesColumns.DISTRICT_ID, MarketPricesColumns.DISTRICT_ID);
        sInstancesProjectionMap.put(MarketPricesColumns.CROP_ID, MarketPricesColumns.CROP_ID);
    }
}
