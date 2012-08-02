package applab.client.farmerlink;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import applab.client.farmerlink.parsers.BuyersParser;
import applab.client.farmerlink.parsers.DistrictsAndCropsParser;
import applab.client.farmerlink.parsers.MarketPricesParser;
import applab.client.farmerlink.provider.BuyerProvider;
import applab.client.farmerlink.provider.BuyerProviderAPI;
import applab.client.farmerlink.provider.CropsProviderAPI;
import applab.client.farmerlink.provider.DistrictsProviderAPI;
import applab.client.farmerlink.provider.FarmerProviderAPI;
import applab.client.farmerlink.provider.MarketPricesProviderAPI;
import applab.client.farmerlink.tasks.DownloadBuyers;
import applab.client.farmerlink.tasks.DownloadDistrictsAndCrops;
import applab.client.farmerlink.tasks.DownloadFarmersAndMarketPrices;

public class Repository {
    
	private static List<String> districts;
	private static List<String> crops;
	private static List<MarketPrices> marketPrices;
	private static List<Farmer> farmers;
	private static List<Buyer> buyers;
    
    public static List<Farmer> getFarmersByDistrictAndCrop(String url, String district, String crop) {
    	List<Farmer> farmers = getFarmersFromDb(district, crop);
        if (farmers == null || farmers.size() == 0) {
        	Log.i("FARMERS DOWNLOAD", "Farmer cache empty, downloading ...");
        	DownloadFarmersAndMarketPrices downloadFarmersAndMarketPrices = new DownloadFarmersAndMarketPrices(url);
        	downloadFarmersAndMarketPrices.downloadFarmersAndMarketPrices(district, crop);
        	farmers = MarketPricesParser.getFarmers();
        }
        return farmers;
    }
    
    private static List<Farmer> getFarmersFromDb(String district, String crop) {
    	
    	List<Farmer> farmers = new ArrayList<Farmer>();
    	MarketPricesParser marketPricesParser = new MarketPricesParser(district, crop);
    	
    	String selection = DistrictsProviderAPI.DistrictsColumns._ID + "=? and " + CropsProviderAPI.CropsColumns._ID + "=?";
    	String[] selectionArgs = {marketPricesParser.getDistrictId(), marketPricesParser.getCropId()};
    	Cursor farmerCursor = MarketLinkApplication.getInstance().getContentResolver().query(FarmerProviderAPI.FarmerColumns.CONTENT_URI, null, selection, selectionArgs, null);
    	farmerCursor.moveToFirst();
		while (farmerCursor.isAfterLast() == false) 
		{
			Farmer farmer = new Farmer();
		    farmer.setId(farmerCursor.getString(farmerCursor.getColumnIndex(FarmerProviderAPI.FarmerColumns.FARMER_ID)));
		    farmer.setName(farmerCursor.getString(farmerCursor.getColumnIndex(FarmerProviderAPI.FarmerColumns.FARMER_NAME)));
		    farmer.setPhoneNumber(farmerCursor.getString(farmerCursor.getColumnIndex(FarmerProviderAPI.FarmerColumns.FARMER_MOBILE)));
		    farmers.add(farmer);
		    farmerCursor.moveToNext();
		}
		farmerCursor.close();
    	return farmers;
    }

    public static List<MarketPrices> getMarketPricesByDistrictAndCrop(String url, String crop, String district) {
        List<MarketPrices> marketPrices = getMarketPricesFromDb(district, crop);
        if (marketPrices == null || marketPrices.size() == 0) {
        	DownloadFarmersAndMarketPrices downloadFarmersAndMarketPrices = new DownloadFarmersAndMarketPrices(url);
        	downloadFarmersAndMarketPrices.downloadFarmersAndMarketPrices(district, crop);
        	marketPrices = getMarketPricesFromDb(district, crop);
        }
        return marketPrices;
    }

    private static List<MarketPrices> getMarketPricesFromDb(String district, String crop) {
    	List<MarketPrices> marketPrices = new ArrayList<MarketPrices>();
    	MarketPricesParser marketPricesParser = new MarketPricesParser(district, crop);
    	
    	String selection = DistrictsProviderAPI.DistrictsColumns._ID + "=? and " + CropsProviderAPI.CropsColumns._ID + "=?";
    	String[] selectionArgs = {marketPricesParser.getDistrictId(), marketPricesParser.getCropId()};
    	Cursor marketPricesCursor = MarketLinkApplication.getInstance().getContentResolver().query(MarketPricesProviderAPI.MarketPricesColumns.CONTENT_URI, null, selection, selectionArgs, null);
    	marketPricesCursor.moveToFirst();
		while (marketPricesCursor.isAfterLast() == false) 
		{
			MarketPrices marketPrice = new MarketPrices();
			marketPrice.setWholesalePrice(marketPricesCursor.getString(marketPricesCursor.getColumnIndex(MarketPricesProviderAPI.MarketPricesColumns.WHOLESALE_PRICE)));
			marketPrice.setRetailPrice(marketPricesCursor.getString(marketPricesCursor.getColumnIndex(MarketPricesProviderAPI.MarketPricesColumns.RETAIL_PRICE)));
			marketPrice.setMarketName(marketPricesCursor.getString(marketPricesCursor.getColumnIndex(MarketPricesProviderAPI.MarketPricesColumns.MARKET_NAME)));
		    marketPrices.add(marketPrice);
		    marketPricesCursor.moveToNext();
		}
		marketPricesCursor.close();
    	return marketPrices;
    	
    }
    
	public static List<Suppliers> getSuppliersByDistrictAndCrop(String crop,
			String district) {
		ArrayList<Suppliers> suppliers = new ArrayList<Suppliers>();
		suppliers.add(new Suppliers("James Onyango", "0772712255", "Namokora"));
		suppliers.add(new Suppliers("James Pombe", "0777777777", "Bukalango"));
		suppliers.add(new Suppliers("Billy Blanks", "0712190999", "Chinatown"));
		
		return suppliers;
	}

	public static List<Buyer> getBuyersByDistrictAndCrop(String url, String district, String crop) {
		
		List<Buyer> buyers = getBuyersFromDb(district, crop);
		if (buyers == null || buyers.size() == 0) {
			Log.i("BUYERS DOWNLOAD", "Buyers cache empty, downloading ...");
			DownloadBuyers downloadBuyers = new DownloadBuyers(url);
			downloadBuyers.downloadBuyers(district, crop);
			buyers = getBuyersFromDb(district, crop);
		}
		return buyers;
	}
	
	private static List<Buyer> getBuyersFromDb(String district, String crop) {
		List<Buyer> buyers =  new ArrayList<Buyer>();
		
    	BuyersParser buyersParser = new BuyersParser(district, crop);
    	
    	String selection = DistrictsProviderAPI.DistrictsColumns._ID + "=? and " + CropsProviderAPI.CropsColumns._ID + "=?";
    	String[] selectionArgs = {buyersParser.getDistrictId(), buyersParser.getCropId()};
    	Cursor buyerCursor = MarketLinkApplication.getInstance().getContentResolver().query(BuyerProviderAPI.BuyersColumns.CONTENT_URI, null, selection, selectionArgs, null);
    	buyerCursor.moveToFirst();
		while (buyerCursor.isAfterLast() == false) 
		{
			Buyer buyer = new Buyer();
			buyer.setLocation(buyerCursor.getString(buyerCursor.getColumnIndex(BuyerProviderAPI.BuyersColumns.BUYER_LOCATION)));
			buyer.setName(buyerCursor.getString(buyerCursor.getColumnIndex(BuyerProviderAPI.BuyersColumns.BUYER_NAME)));
			buyer.setTelephone(buyerCursor.getString(buyerCursor.getColumnIndex(BuyerProviderAPI.BuyersColumns.BUYER_TELEPHONE)));
		    buyers.add(buyer);
		    buyerCursor.moveToNext();
		}
		buyerCursor.close();
    	return buyers;
	} 
	/**
	 * Check if the database exist
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private static boolean checkDataBase(String database) {
	    SQLiteDatabase checkDB = null;
	    try {
	        checkDB = SQLiteDatabase.openDatabase(database, null,
	                SQLiteDatabase.OPEN_READONLY);
	        checkDB.close();
	    } catch (SQLiteException e) {
	        // database doesn't exist yet.
	    }
	    return checkDB != null ? true : false;
	}
	
	private static List<String> getDistrictsFromDb() {
		
		Cursor cursor = MarketLinkApplication.getInstance().getContentResolver().query(DistrictsProviderAPI.DistrictsColumns.CONTENT_URI, null, null, null, null);
		//cursor.moveToPosition(-1);
		List<String> districts = new ArrayList<String>();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) 
		{
		    districts.add(cursor.getString(1));
		    cursor.moveToNext();
		}
		cursor.close();
		return districts;
	}
	public static List<String> getDistricts(String url) {
		//if (checkDataBase("/sdcard/marketlink/databases/districts.db")) {
			List<String> districts = getDistrictsFromDb();
		//}
		if (districts == null || districts.size() == 0) {
			DownloadDistrictsAndCrops downloadDistrictsAndCrops = new DownloadDistrictsAndCrops(url);
			downloadDistrictsAndCrops.download();
			districts = getDistrictsFromDb();
		}
		return districts;
	}

	public static List<String> getCrops(String url) {
		//if (checkDataBase("/sdcard/marketlink/databases/crops.db")) {
			List<String> crops = getCropsFromDb();
		//}
		if (crops == null || crops.size() == 0) {
			DownloadDistrictsAndCrops downloadDistrictsAndCrops = new DownloadDistrictsAndCrops(url);
			downloadDistrictsAndCrops.download();
			DistrictsAndCropsParser.getCrops();
			crops = getCropsFromDb();
		}
		return crops;
	}
	
	private static List<String> getCropsFromDb() {
		Cursor cursor = MarketLinkApplication.getInstance().getContentResolver().query(CropsProviderAPI.CropsColumns.CONTENT_URI, null, null, null, null);
		//cursor.moveToPosition(-1);
		List<String> crops = new ArrayList<String>();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) 
		{
		    crops.add(cursor.getString(1));
		    cursor.moveToNext();
		}
		cursor.close();
		return crops;
	}
}
