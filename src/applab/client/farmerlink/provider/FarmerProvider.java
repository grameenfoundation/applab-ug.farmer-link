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

public class FarmerProvider extends ContentProvider {

	private static final String t = "FarmerProvider";

    private static final String DATABASE_NAME = "farmers.db";
    private static final int DATABASE_VERSION = 1;
    private static final String FARMERS_TABLE_NAME = "farmers";

    private static final int FARMERS = 1;
    private static final int FARMER_ID = 2;
    
    private static HashMap<String, String> sInstancesProjectionMap;
    private static final UriMatcher sUriMatcher;
    
    private static class DatabaseHelper extends MarketLinkSQLiteOpenHelper {

		public DatabaseHelper(String databaseName) {
			super("/sdcard/marketlink/databases", databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("DB CREATION", "creating Farmers database");
			db.execSQL("CREATE TABLE " + FARMERS_TABLE_NAME + " (" 
		               + FarmerColumns._ID + " integer primary key, " 
		               + FarmerColumns.FARMER_NAME + " text not null, "
		               + FarmerColumns.FARMER_MOBILE + " text, " 
		               + FarmerColumns.DISTRICT_ID + " text not null, " 
		               + FarmerColumns.CROP_ID + " text not null, " 
		               + FarmerColumns.FARMER_ID + " text not null );"); 
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
	public int delete(Uri arg0, String arg1, String[] arg2) {
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
        if (sUriMatcher.match(uri) != FARMERS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(FARMERS_TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri instanceUri = ContentUris.withAppendedId(FarmerColumns.CONTENT_URI, rowId);
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
        qb.setTables(FARMERS_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case FARMERS:
                qb.setProjectionMap(sInstancesProjectionMap);
                break;

            case FARMER_ID:
                qb.setProjectionMap(sInstancesProjectionMap);
                qb.appendWhere(FarmerColumns._ID + "=" + uri.getPathSegments().get(1));
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
		return 0;
	}

	static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(FarmerProviderAPI.AUTHORITY, "farmers", FARMERS);
        sUriMatcher.addURI(FarmerProviderAPI.AUTHORITY, "farmer/#", FARMER_ID);

        sInstancesProjectionMap = new HashMap<String, String>();
        sInstancesProjectionMap.put(FarmerColumns._ID, FarmerColumns._ID);
        sInstancesProjectionMap.put(FarmerColumns.FARMER_NAME, FarmerColumns.FARMER_NAME);
        sInstancesProjectionMap.put(FarmerColumns.FARMER_MOBILE, FarmerColumns.FARMER_MOBILE);
        sInstancesProjectionMap.put(FarmerColumns.FARMER_ID, FarmerColumns.FARMER_ID);
        sInstancesProjectionMap.put(FarmerColumns.DISTRICT_ID, FarmerColumns.DISTRICT_ID);
        sInstancesProjectionMap.put(FarmerColumns.CROP_ID, FarmerColumns.CROP_ID);
    }
}
