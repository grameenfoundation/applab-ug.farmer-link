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

public class FarmerVersionProvider extends ContentProvider {

	private static final String t = "FarmerVersionProvider";

    private static final String DATABASE_NAME = "farmerVersion.db";
    private static final int DATABASE_VERSION = 1;
    private static final String FARMERVERSION_TABLE_NAME = "farmerVersion";

    private static final int FARMERVERSIONS = 1;
    private static final int FARMERVERSION_ID = 2;
    
    private static HashMap<String, String> sInstancesProjectionMap;
    private static final UriMatcher sUriMatcher;
    
    private static class DatabaseHelper extends MarketLinkSQLiteOpenHelper {

		public DatabaseHelper(String databaseName) {
			super("/sdcard/marketlink/databases", databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("DB CREATION", "creating buyer versions database");
			db.execSQL("CREATE TABLE " + FARMERVERSION_TABLE_NAME + " (" 
		               + FarmerVersionsColumns._ID + " integer primary key, " 
		               + FarmerVersionsColumns.VERSION + " text not null, "
		               + FarmerVersionsColumns.CROP_ID + " text not null, "
		               + FarmerVersionsColumns.DISTRICT_ID + " text not null );");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(t, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS districts");
            onCreate(db);
		}
    	
    }
    private DatabaseHelper mDbHelper;
    
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        
        switch (sUriMatcher.match(uri)) {
            case FARMERVERSIONS:                
                count = db.delete(FARMERVERSION_TABLE_NAME, where, whereArgs);
                break;

            case FARMERVERSION_ID:
                String instanceId = uri.getPathSegments().get(1);
                
                count =
                    db.delete(FARMERVERSION_TABLE_NAME,
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
		if (sUriMatcher.match(uri) != FARMERVERSIONS) {
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
        long rowId = db.insert(FARMERVERSION_TABLE_NAME, null, values);

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
        qb.setTables(FARMERVERSION_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case FARMERVERSIONS:
                qb.setProjectionMap(sInstancesProjectionMap);
                break;

            case FARMERVERSION_ID:
                qb.setProjectionMap(sInstancesProjectionMap);
                qb.appendWhere(FarmerVersionsColumns._ID + "=" + uri.getPathSegments().get(1));
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
        sUriMatcher.addURI(FarmerVersionProviderAPI.AUTHORITY, "farmersVersions", FARMERVERSIONS);
        sUriMatcher.addURI(FarmerVersionProviderAPI.AUTHORITY, "farmerVersion/#", FARMERVERSION_ID);
        

        sInstancesProjectionMap = new HashMap<String, String>();
        sInstancesProjectionMap.put(FarmerVersionsColumns._ID, FarmerVersionsColumns._ID);
        sInstancesProjectionMap.put(FarmerVersionsColumns.VERSION, FarmerVersionsColumns.VERSION);
        sInstancesProjectionMap.put(FarmerVersionsColumns.DISTRICT_ID, FarmerVersionsColumns.DISTRICT_ID);
        sInstancesProjectionMap.put(FarmerVersionsColumns.CROP_ID, FarmerVersionsColumns.CROP_ID);
    }
}
