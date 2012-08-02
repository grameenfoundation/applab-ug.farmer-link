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
import applab.client.farmerlink.provider.CropsProviderAPI.CropsColumns;

public class CropsProvider extends ContentProvider {

	private static final String t = "CropsProvider";

    private static final String DATABASE_NAME = "crops.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CROPS_TABLE_NAME = "crops";

    private static final int CROPS = 1;
    private static final int CROP_ID = 2;
    
    private static HashMap<String, String> sInstancesProjectionMap;
    private static final UriMatcher sUriMatcher;
    
    private static class DatabaseHelper extends MarketLinkSQLiteOpenHelper {

		public DatabaseHelper(String databaseName) {
			super("/sdcard/marketlink/databases", databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + CROPS_TABLE_NAME + " (" + CropsColumns._ID
                    + " integer primary key, " + CropsColumns.CROP_NAME + " text not null);");
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
        if (sUriMatcher.match(uri) != CROPS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(CROPS_TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri instanceUri = ContentUris.withAppendedId(CropsColumns.CONTENT_URI, rowId);
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
        qb.setTables(CROPS_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case CROPS:
                qb.setProjectionMap(sInstancesProjectionMap);
                break;

            case CROP_ID:
                qb.setProjectionMap(sInstancesProjectionMap);
                qb.appendWhere(CropsColumns._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // Get the database and run the query
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
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
        sUriMatcher.addURI(CropsProviderAPI.AUTHORITY, "crops", CROPS);
        sUriMatcher.addURI(CropsProviderAPI.AUTHORITY, "crops/#", CROP_ID);

        sInstancesProjectionMap = new HashMap<String, String>();
        sInstancesProjectionMap.put(CropsColumns._ID, CropsColumns._ID);
        sInstancesProjectionMap.put(CropsColumns.CROP_NAME, CropsColumns.CROP_NAME);
        
    }

}
