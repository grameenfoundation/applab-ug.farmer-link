package applab.client.farmerlink.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import applab.client.farmerlink.database.MarketLinkSQLiteOpenHelper;
import applab.client.farmerlink.provider.FarmerVersionProviderAPI.FarmerVersionsColumns;
import applab.client.farmerlink.provider.MarketPricesVersionProviderAPI.MarketPricesVersionColumns;

public class MarketPricesVersionProvider extends ContentProvider {

	private static final String t = "MarketPricesVersionProvider";

    private static final String DATABASE_NAME = "marketPricesVersion.db";
    private static final int DATABASE_VERSION = 1;
    private static final String MARKETPRICESVERSION_TABLE_NAME = "marketPricesVersion";

    private static final int MARKETPRICESVERSIONS = 1;
    private static final int MARKETPRICESVERSION_ID = 2;
    
    private static HashMap<String, String> sInstancesProjectionMap;
    private static final UriMatcher sUriMatcher;
    
    private static class DatabaseHelper extends MarketLinkSQLiteOpenHelper {

		public DatabaseHelper(String databaseName) {
			super("/sdcard/marketlink/databases", databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("DB CREATION", "creating market prices versions database");
			db.execSQL("CREATE TABLE " + MARKETPRICESVERSION_TABLE_NAME + " (" 
		               + MarketPricesVersionColumns._ID + " integer primary key, " 
		               + MarketPricesVersionColumns.VERSION + " text not null, "
		               + MarketPricesVersionColumns.CROP_ID + " text not null, "
		               + MarketPricesVersionColumns.DISTRICT_ID + " text not null );");
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
	public int delete(Uri uri, String where, String[] whereArgs) {
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        
        switch (sUriMatcher.match(uri)) {
            case MARKETPRICESVERSIONS:                
                count = db.delete(MARKETPRICESVERSION_TABLE_NAME, where, whereArgs);
                break;

            case MARKETPRICESVERSION_ID:
                String instanceId = uri.getPathSegments().get(1);
                
                count =
                    db.delete(MARKETPRICESVERSION_TABLE_NAME,
                    		FarmerVersionsColumns._ID + "=" + instanceId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
                        whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		
		if (sUriMatcher.match(uri) != MARKETPRICESVERSIONS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		}
		else {
			values = new ContentValues();
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(MARKETPRICESVERSION_TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri instanceUri = ContentUris.withAppendedId(MarketPricesVersionColumns.CONTENT_URI, rowId);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MarketPricesVersionProviderAPI.AUTHORITY, "marketpricesVersions", MARKETPRICESVERSIONS);
        sUriMatcher.addURI(MarketPricesVersionProviderAPI.AUTHORITY, "marketpricesVersion/#", MARKETPRICESVERSION_ID);

        sInstancesProjectionMap = new HashMap<String, String>();
        sInstancesProjectionMap.put(MarketPricesVersionColumns._ID, MarketPricesVersionColumns._ID);
        sInstancesProjectionMap.put(MarketPricesVersionColumns.VERSION, MarketPricesVersionColumns.VERSION);
        sInstancesProjectionMap.put(MarketPricesVersionColumns.DISTRICT_ID, MarketPricesVersionColumns.DISTRICT_ID);
        sInstancesProjectionMap.put(MarketPricesVersionColumns.CROP_ID, MarketPricesVersionColumns.CROP_ID);
    }
}
