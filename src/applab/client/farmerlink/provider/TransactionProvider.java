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
import applab.client.farmerlink.provider.TransactionProviderAPI.TransactionColumns;

public class TransactionProvider extends ContentProvider {

	private static final String t = "TransactionProvider";

    private static final String DATABASE_NAME = "markettransaction.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TRANSACTION_TABLE_NAME = "markettransaction";

    private static final int MARKET_TRANSACTIONS = 1;
    private static final int MARKET_TRANSACTION_ID = 2;

    
    private static HashMap<String, String> sInstancesProjectionMap;
    private static final UriMatcher sUriMatcher;
    
    private static class DatabaseHelper extends MarketLinkSQLiteOpenHelper {

		public DatabaseHelper(String databaseName) {
			super("/sdcard/marketlink/databases", databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("DB CREATION", "creating transaction database");
			db.execSQL("CREATE TABLE " + TRANSACTION_TABLE_NAME + " (" 
		               + TransactionColumns._ID + " integer primary key, " 
		               + TransactionColumns.BUYER_NAME + " text not null, "
		               + TransactionColumns.DISTRICT + " text not null, "
		               + TransactionColumns.CROP + " text not null, "
		               + TransactionColumns.TRANSACTION_DATE + " text not null, "
		               + TransactionColumns.TRANSACTION_TYPE + " text not null, "
		               + TransactionColumns.QUANTITY + " text not null, "
		               + TransactionColumns.TRANSACTION_FEE + " text not null, "
		               + TransactionColumns.TRANSPORT_FEE + " text not null, "
		               + TransactionColumns.UNITPRICE + " text not null, "
		               + TransactionColumns.STATUS + " text not null );");
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
            case MARKET_TRANSACTIONS:                
                count = db.delete(TRANSACTION_TABLE_NAME, where, whereArgs);
                break;

            case MARKET_TRANSACTION_ID:
                count =
                    db.delete(TRANSACTION_TABLE_NAME,
                    		TransactionColumns._ID + "=" + uri.getPathSegments().get(1)
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
	public Uri insert(Uri uri, ContentValues initialValues) {Log.i("TPINSERT UTI", uri.toString());
        // Validate the requested uri
        if (sUriMatcher.match(uri) != MARKET_TRANSACTIONS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(TRANSACTION_TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri instanceUri = ContentUris.withAppendedId(TransactionColumns.CONTENT_URI, rowId);
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
        qb.setTables(TRANSACTION_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case MARKET_TRANSACTIONS:
                qb.setProjectionMap(sInstancesProjectionMap);
                break;

            case MARKET_TRANSACTION_ID:
                qb.setProjectionMap(sInstancesProjectionMap);
                qb.appendWhere(TransactionColumns._ID + "=" + uri.getPathSegments().get(1));
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
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case MARKET_TRANSACTIONS:
                count = db.update(TRANSACTION_TABLE_NAME, values, selection, selectionArgs);
                break;

            case MARKET_TRANSACTION_ID:               
                count =
                    db.update(TRANSACTION_TABLE_NAME, values, TransactionColumns._ID + "=" + uri.getPathSegments().get(1)
                            + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(TransactionProviderAPI.AUTHORITY, "markettransactions", MARKET_TRANSACTIONS);
		sUriMatcher.addURI(TransactionProviderAPI.AUTHORITY, "markettransaction/#", MARKET_TRANSACTION_ID);
		
		sInstancesProjectionMap = new HashMap<String, String>();
		sInstancesProjectionMap.put(TransactionColumns._ID, TransactionColumns._ID);
		sInstancesProjectionMap.put(TransactionColumns.BUYER_NAME, TransactionColumns.BUYER_NAME);
		sInstancesProjectionMap.put(TransactionColumns.UNITPRICE, TransactionColumns.UNITPRICE);
		sInstancesProjectionMap.put(TransactionColumns.TRANSPORT_FEE, TransactionColumns.TRANSPORT_FEE);
		sInstancesProjectionMap.put(TransactionColumns.TRANSACTION_FEE, TransactionColumns.TRANSACTION_FEE);
		sInstancesProjectionMap.put(TransactionColumns.QUANTITY, TransactionColumns.QUANTITY);
		sInstancesProjectionMap.put(TransactionColumns.CROP, TransactionColumns.CROP);
		sInstancesProjectionMap.put(TransactionColumns.DISTRICT, TransactionColumns.DISTRICT);
		sInstancesProjectionMap.put(TransactionColumns.TRANSACTION_TYPE, TransactionColumns.TRANSACTION_TYPE);
		sInstancesProjectionMap.put(TransactionColumns.TRANSACTION_DATE, TransactionColumns.TRANSACTION_DATE);
		sInstancesProjectionMap.put(TransactionColumns.STATUS, TransactionColumns.STATUS);
	}
}
