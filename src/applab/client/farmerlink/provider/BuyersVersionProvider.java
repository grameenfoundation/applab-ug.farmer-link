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
import android.text.TextUtils;
import android.util.Log;
import applab.client.farmerlink.database.MarketLinkSQLiteOpenHelper;
import applab.client.farmerlink.provider.BuyersVersionProviderAPI.BuyersVersionColumns;
import applab.client.farmerlink.provider.FarmerVersionProviderAPI.FarmerVersionsColumns;

public class BuyersVersionProvider extends ContentProvider {

	private static final String t = "BuyersVersionProvider";

    private static final String DATABASE_NAME = "buyersVersion.db";
    private static final int DATABASE_VERSION = 1;
    private static final String BUYERSVERSION_TABLE_NAME = "buyersVersion";

    private static final int BUYERSVERSIONS = 1;
    private static final int BUYERSVERSION_ID = 2;
    
    private static HashMap<String, String> sInstancesProjectionMap;
    private static final UriMatcher sUriMatcher;
    
    private static class DatabaseHelper extends MarketLinkSQLiteOpenHelper {

		public DatabaseHelper(String databaseName) {
			super("/sdcard/marketlink/databases", databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("DB CREATION", "creating buyer versions database");
			db.execSQL("CREATE TABLE " + BUYERSVERSION_TABLE_NAME + " (" 
		               + BuyersVersionColumns._ID + " integer primary key, " 
		               + BuyersVersionColumns.VERSION + " text not null, "
		               + BuyersVersionColumns.CROP_ID + " text not null, "
		               + BuyersVersionColumns.DISTRICT_ID + " text not null );");
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
            case BUYERSVERSIONS:                
                count = db.delete(BUYERSVERSION_TABLE_NAME, where, whereArgs);
                break;

            case BUYERSVERSION_ID:
                String instanceId = uri.getPathSegments().get(1);
                
                count =
                    db.delete(BUYERSVERSION_TABLE_NAME,
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
		if (sUriMatcher.match(uri) != BUYERSVERSIONS) {
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
        long rowId = db.insert(BUYERSVERSION_TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri instanceUri = ContentUris.withAppendedId(BuyersVersionColumns.CONTENT_URI, rowId);
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
        qb.setTables(BUYERSVERSION_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case BUYERSVERSIONS:
                qb.setProjectionMap(sInstancesProjectionMap);
                break;

            case BUYERSVERSION_ID:
                qb.setProjectionMap(sInstancesProjectionMap);
                qb.appendWhere(BuyersVersionColumns._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
            	throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // Get the database and run the query
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        String queryString = qb.buildQuery(null, selection, selectionArgs, null, null, sortOrder, null);
        Log.i("QUERYSTRING", queryString);
        // Tell the cursor what uri to watch, so it knows when its source data changes
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
        sUriMatcher.addURI(BuyerProviderAPI.AUTHORITY, "buyersVersions", BUYERSVERSIONS);
        sUriMatcher.addURI(BuyerProviderAPI.AUTHORITY, "buyerVersion/#", BUYERSVERSION_ID);
        

        sInstancesProjectionMap = new HashMap<String, String>();
        sInstancesProjectionMap.put(BuyersVersionColumns._ID, BuyersVersionColumns._ID);
        sInstancesProjectionMap.put(BuyersVersionColumns.VERSION, BuyersVersionColumns.VERSION);
        sInstancesProjectionMap.put(BuyersVersionColumns.DISTRICT_ID, BuyersVersionColumns.DISTRICT_ID);
        sInstancesProjectionMap.put(BuyersVersionColumns.CROP_ID, BuyersVersionColumns.CROP_ID);
    }
}
