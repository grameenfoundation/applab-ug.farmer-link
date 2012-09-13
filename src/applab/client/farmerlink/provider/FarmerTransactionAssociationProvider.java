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
import applab.client.farmerlink.provider.FarmerTransactionAssociationProviderAPI.FarmerTransactionAssociationColumns;
import applab.client.farmerlink.provider.TransactionProviderAPI.TransactionColumns;

public class FarmerTransactionAssociationProvider extends ContentProvider {

	private static final String t = "TransactionProvider";

    private static final String DATABASE_NAME = "farmertransactionassociation.db";
    private static final int DATABASE_VERSION = 1;
    private static final String FARMER_TRANSACTION_ASSOCIATION_TABLE_NAME = "farmertransactionassociation";

    private static final int FARMER_TRANSACTION_ASSOCIATIONS = 1;
    private static final int FARMER_TRANSACTION_ASSOCIATION_ID = 2;

    
    private static HashMap<String, String> sInstancesProjectionMap;
    private static final UriMatcher sUriMatcher;
    
    private static class DatabaseHelper extends MarketLinkSQLiteOpenHelper {

		public DatabaseHelper(String databaseName) {
			super("/sdcard/marketlink/databases", databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("DB CREATION", "creating farmer transaction association database");
			db.execSQL("CREATE TABLE " + FARMER_TRANSACTION_ASSOCIATION_TABLE_NAME + " (" 
		               + FarmerTransactionAssociationColumns._ID + " integer primary key, " 
		               + FarmerTransactionAssociationColumns.FARMER_QUOTA + " text not null, "
		               + FarmerTransactionAssociationColumns.FARMER_ID + " text, " 
		               + FarmerTransactionAssociationColumns.TRANSACTION_ID + " text not null );");
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
            case FARMER_TRANSACTION_ASSOCIATIONS:                
                count = db.delete(FARMER_TRANSACTION_ASSOCIATION_TABLE_NAME, where, whereArgs);
                break;

            case FARMER_TRANSACTION_ASSOCIATION_ID:
                count =
                    db.delete(FARMER_TRANSACTION_ASSOCIATION_TABLE_NAME,
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
	public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != FARMER_TRANSACTION_ASSOCIATIONS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(FARMER_TRANSACTION_ASSOCIATION_TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri instanceUri = ContentUris.withAppendedId(FarmerTransactionAssociationColumns.CONTENT_URI, rowId);
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
        qb.setTables(FARMER_TRANSACTION_ASSOCIATION_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case FARMER_TRANSACTION_ASSOCIATIONS:
                qb.setProjectionMap(sInstancesProjectionMap);
                break;

            case FARMER_TRANSACTION_ASSOCIATION_ID:
                qb.setProjectionMap(sInstancesProjectionMap);
                qb.appendWhere(FarmerTransactionAssociationColumns._ID + "=" + uri.getPathSegments().get(1));
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
            case FARMER_TRANSACTION_ASSOCIATIONS:
                count = db.update(FARMER_TRANSACTION_ASSOCIATION_TABLE_NAME, values, selection, selectionArgs);
                break;

            case FARMER_TRANSACTION_ASSOCIATION_ID:               
                count =
                    db.update(FARMER_TRANSACTION_ASSOCIATION_TABLE_NAME, values, FarmerTransactionAssociationColumns._ID + "=" + uri.getPathSegments().get(1)
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
		sUriMatcher.addURI(FarmerTransactionAssociationProviderAPI.AUTHORITY, "farmertransactionassociations", FARMER_TRANSACTION_ASSOCIATIONS);
		sUriMatcher.addURI(FarmerTransactionAssociationProviderAPI.AUTHORITY, "farmertransactionassociation/#", FARMER_TRANSACTION_ASSOCIATION_ID);
		
		sInstancesProjectionMap = new HashMap<String, String>();
		sInstancesProjectionMap.put(FarmerTransactionAssociationColumns._ID, FarmerTransactionAssociationColumns._ID);
		sInstancesProjectionMap.put(FarmerTransactionAssociationColumns.FARMER_QUOTA, FarmerTransactionAssociationColumns.FARMER_QUOTA);
		sInstancesProjectionMap.put(FarmerTransactionAssociationColumns.FARMER_ID, FarmerTransactionAssociationColumns.FARMER_ID);
		sInstancesProjectionMap.put(FarmerTransactionAssociationColumns.TRANSACTION_ID, FarmerTransactionAssociationColumns.TRANSACTION_ID);
	}
}
