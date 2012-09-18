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
import applab.client.farmerlink.provider.BuyerProviderAPI.BuyersColumns;

public class BuyerProvider extends ContentProvider {

	private static final String t = "BuyerProvider";

    private static final String DATABASE_NAME = "buyers.db";
    private static final int DATABASE_VERSION = 1;
    private static final String BUYERS_TABLE_NAME = "buyers";

    private static final int BUYERS = 1;
    private static final int BUYER_ID = 2;
    private static final int DISTRICT_ID = 3;
    private static final int CROP_ID = 4;
    
    private static HashMap<String, String> sInstancesProjectionMap;
    private static final UriMatcher sUriMatcher;
    
    private static class DatabaseHelper extends MarketLinkSQLiteOpenHelper {

		public DatabaseHelper(String databaseName) {
			super("/sdcard/marketlink/databases", databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("DB CREATION", "creating buyers database");
			db.execSQL("CREATE TABLE " + BUYERS_TABLE_NAME + " (" 
		               + BuyersColumns._ID + " integer primary key, " 
		               + BuyersColumns.BUYER_NAME + " text not null, "
		               + BuyersColumns.BUYER_TELEPHONE + " text, " 
		               + BuyersColumns.DISTRICT_ID + " text not null, "
		               + BuyersColumns.CROP_ID + " text not null, "
		               + BuyersColumns.BUYER_LOCATION + " text not null );");
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
            case BUYERS:
                count = db.delete(BUYERS_TABLE_NAME, where, whereArgs);
                break;

            case BUYER_ID:
                String instanceId = uri.getPathSegments().get(1);
                
                count =
                    db.delete(BUYERS_TABLE_NAME,
                    		BuyersColumns._ID + "=" + instanceId
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
        // Validate the requested uri
        if (sUriMatcher.match(uri) != BUYERS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Log.i("INSERTBUYER", "inserting buyer");
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(BUYERS_TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri instanceUri = ContentUris.withAppendedId(BuyersColumns.CONTENT_URI, rowId);
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
        qb.setTables(BUYERS_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case BUYERS:
                qb.setProjectionMap(sInstancesProjectionMap);
                break;

            case BUYER_ID:
                qb.setProjectionMap(sInstancesProjectionMap);
                qb.appendWhere(BuyersColumns._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
            	//qb.setProjectionMap(sInstancesProjectionMap);
            	//qb.appendWhere(BuyersColumns.DISTRICT_ID + "=" + selectionArgs[0] + "and " + BuyersColumns.CROP_ID + "=" + selectionArgs[1]);
            	throw new IllegalArgumentException("Unknown URI " + uri);
            	//break;
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
        sUriMatcher.addURI(BuyerProviderAPI.AUTHORITY, "buyers", BUYERS);
        sUriMatcher.addURI(BuyerProviderAPI.AUTHORITY, "buyer/#", BUYER_ID);
        sUriMatcher.addURI(BuyerProviderAPI.AUTHORITY, "buyer/crop", CROP_ID);
        

        sInstancesProjectionMap = new HashMap<String, String>();
        sInstancesProjectionMap.put(BuyersColumns._ID, BuyersColumns._ID);
        sInstancesProjectionMap.put(BuyersColumns.BUYER_NAME, BuyersColumns.BUYER_NAME);
        sInstancesProjectionMap.put(BuyersColumns.BUYER_TELEPHONE, BuyersColumns.BUYER_TELEPHONE);
        sInstancesProjectionMap.put(BuyersColumns.BUYER_LOCATION, BuyersColumns.BUYER_LOCATION);
        sInstancesProjectionMap.put(BuyersColumns.DISTRICT_ID, BuyersColumns.DISTRICT_ID);
        sInstancesProjectionMap.put(BuyersColumns.CROP_ID, BuyersColumns.CROP_ID);
    }
}
