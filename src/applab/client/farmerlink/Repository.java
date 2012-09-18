package applab.client.farmerlink;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import applab.client.farmerlink.parsers.BuyersParser;
import applab.client.farmerlink.parsers.DistrictsAndCropsParser;
import applab.client.farmerlink.parsers.MarketPricesParser;
import applab.client.farmerlink.provider.BuyerProviderAPI;
import applab.client.farmerlink.provider.BuyersVersionProviderAPI;
import applab.client.farmerlink.provider.CropsProviderAPI;
import applab.client.farmerlink.provider.DistrictsProviderAPI;
import applab.client.farmerlink.provider.FarmerProviderAPI;
import applab.client.farmerlink.provider.FarmerVersionProviderAPI;
import applab.client.farmerlink.provider.MarketPricesProviderAPI;
import applab.client.farmerlink.provider.MarketPricesVersionProviderAPI;
import applab.client.farmerlink.tasks.DownloadBuyers;
import applab.client.farmerlink.tasks.DownloadDistrictsAndCrops;
import applab.client.farmerlink.tasks.DownloadFarmersAndMarketPrices;

public class Repository {
    
	public static List<Farmer> getFarmersByDistrictAndCrop(String url, String district, String crop) {

		Cursor farmersVersionCursor = MarketLinkApplication.getInstance().getContentResolver().query(FarmerVersionProviderAPI.FarmerVersionsColumns.CONTENT_URI,
				null, null, null, null);
		List<Farmer> farmers = new ArrayList<Farmer>();
		if (farmersVersionCursor.getCount() == 0) {
			DownloadFarmersAndMarketPrices downloadFarmersAndMarketPrices = new DownloadFarmersAndMarketPrices(url);
        	downloadFarmersAndMarketPrices.downloadFarmersAndMarketPrices(district, crop);
        	farmers = getFarmersFromDb(district, crop);
		}
		else {
			 farmersVersionCursor.moveToFirst();Log.i("farmerVersion DB", "DB EXISTS!!");
			 if (farmersVersionCursor.getCount() > 0) {
				String farmersVersion = farmersVersionCursor.getString(farmersVersionCursor.getColumnIndex(FarmerVersionProviderAPI.FarmerVersionsColumns.VERSION));
				DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				Calendar versionDate = Calendar.getInstance();
				Calendar todayDate = Calendar.getInstance();
				try {
					Date date = df.parse(farmersVersion);
					
					versionDate.setTime(date);
					versionDate.add(Calendar.DATE, 7);
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
			
		    	farmers = getFarmersFromDb(district, crop);
		        if (farmers == null || farmers.size() == 0 || ((todayDate.getTimeInMillis() - versionDate.getTimeInMillis())/(24 * 60 * 60 * 1000)) >= 7) {
		        	if (((todayDate.getTimeInMillis() - versionDate.getTimeInMillis())/(24 * 60 * 60 * 1000)) >= 7) {
		        		String where = FarmerProviderAPI.FarmerColumns.DISTRICT_ID + " =? " + "and " + FarmerProviderAPI.FarmerColumns.CROP_ID + " =? ";
		        		MarketPricesParser marketPricesParser = new MarketPricesParser(district, crop);
		        		String [] selectionArgs = {marketPricesParser.getDistrictId(), marketPricesParser.getCropId()};
		        		MarketLinkApplication.getInstance().getContentResolver().delete(FarmerProviderAPI.FarmerColumns.CONTENT_URI,
		        				where, selectionArgs);
		        		MarketLinkApplication.getInstance().getContentResolver().delete(MarketPricesProviderAPI.MarketPricesColumns.CONTENT_URI,
		        				where, selectionArgs);
		        		
		        		MarketLinkApplication.getInstance().getContentResolver().delete(FarmerVersionProviderAPI.FarmerVersionsColumns.CONTENT_URI, null, null);
		        		MarketLinkApplication.getInstance().getContentResolver().delete(MarketPricesVersionProviderAPI.MarketPricesVersionColumns.CONTENT_URI, null, null);
		        		Log.i("FARMERS DOWNLOAD", "Farmer cache empty, downloading ...");
			        	DownloadFarmersAndMarketPrices downloadFarmersAndMarketPrices = new DownloadFarmersAndMarketPrices(url);
			        	downloadFarmersAndMarketPrices.downloadFarmersAndMarketPrices(district, crop);
			        	farmers = getFarmersFromDb(district, crop); 
		        	}
		        }
			}
	        	   
		}
		farmersVersionCursor.close();
		return farmers;
    }
    
    public static List<Farmer> getFarmersFromDb(String district, String crop) {
    	
    	List<Farmer> farmers = new ArrayList<Farmer>();
    	MarketPricesParser marketPricesParser = new MarketPricesParser(district, crop);
    	
    	String selection = FarmerProviderAPI.FarmerColumns.DISTRICT_ID + "=? and " + FarmerProviderAPI.FarmerColumns.CROP_ID + "=?";
    	String[] selectionArgs = {marketPricesParser.getDistrictId(), marketPricesParser.getCropId()};
    	Cursor farmerCursor = MarketLinkApplication.getInstance().getContentResolver().query(FarmerProviderAPI.FarmerColumns.CONTENT_URI, null, selection, selectionArgs, null);
    	Log.i("FARMER DB COUNT", String.valueOf(farmerCursor.getCount()));
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
        	Log.i("MarketPrice DOWNLOAD", "MarketPrice cache empty, downloading ...");
        	DownloadFarmersAndMarketPrices downloadFarmersAndMarketPrices = new DownloadFarmersAndMarketPrices(url);
        	downloadFarmersAndMarketPrices.downloadFarmersAndMarketPrices(district, crop);
        	marketPrices = getMarketPricesFromDb(district, crop);
        }
        return marketPrices;
    }

    public static List<MarketPrices> getMarketPricesFromDb(String district, String crop) {
    	List<MarketPrices> marketPrices = new ArrayList<MarketPrices>();
    	MarketPricesParser marketPricesParser = new MarketPricesParser(district, crop);
    	
    	String selection = MarketPricesProviderAPI.MarketPricesColumns.DISTRICT_ID + "=? and " + MarketPricesProviderAPI.MarketPricesColumns.CROP_ID + "=?";
    	String[] selectionArgs = {marketPricesParser.getDistrictId(), marketPricesParser.getCropId()};
    	Cursor marketPricesCursor = MarketLinkApplication.getInstance().getContentResolver().query(MarketPricesProviderAPI.MarketPricesColumns.CONTENT_URI, null, selection, selectionArgs, null);
    	Log.i("MKT PRICES DB COUNT", String.valueOf(marketPricesCursor.getCount()));
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
		Cursor buyersVersionCursor = MarketLinkApplication.getInstance().getContentResolver().query(BuyersVersionProviderAPI.BuyersVersionColumns.CONTENT_URI,
				null, null, null, null);
		List<Buyer> buyers = new ArrayList<Buyer>();
		
		if (buyersVersionCursor.getCount() == 0) {
			DownloadBuyers downloadBuyers = new DownloadBuyers(url);
			downloadBuyers.downloadBuyers(district, crop);
			buyers = getBuyersFromDb(district, crop);
		}
		else {
			buyersVersionCursor.moveToFirst();
			if (buyersVersionCursor.getCount() > 0) { 
				String buyersVersion = buyersVersionCursor.getString(buyersVersionCursor.getColumnIndex(BuyersVersionProviderAPI.BuyersVersionColumns.VERSION));
				buyersVersionCursor.close();
				DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				Calendar versionDate = Calendar.getInstance();
				Calendar todayDate = Calendar.getInstance();
				try {
					Date date = df.parse(buyersVersion);
					
					versionDate.setTime(date);
					versionDate.add(Calendar.DATE, 7);
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
				buyers = getBuyersFromDb(district, crop);
				if (buyers == null || buyers.size() == 0 || ((todayDate.getTimeInMillis() - versionDate.getTimeInMillis())/(24 * 60 * 60 * 1000)) >= 7) {
					
					if (((todayDate.getTimeInMillis() - versionDate.getTimeInMillis())/(24 * 60 * 60 * 1000)) >= 7) {
						String where = BuyerProviderAPI.BuyersColumns.DISTRICT_ID + " =? " + "and " + BuyerProviderAPI.BuyersColumns.CROP_ID + " =? ";
		        		BuyersParser buyersParser = new BuyersParser(district, crop);
		        		String [] selectionArgs = {buyersParser.getDistrictId(), buyersParser.getCropId()};
		        		MarketLinkApplication.getInstance().getContentResolver().delete(BuyerProviderAPI.BuyersColumns.CONTENT_URI,
		        				where, selectionArgs);
		        		
		        		MarketLinkApplication.getInstance().getContentResolver().delete(BuyersVersionProviderAPI.BuyersVersionColumns.CONTENT_URI, null, null);
		        		Log.i("Buyers DOWNLOAD", "Buyers cache empty, downloading ...");
		    			DownloadBuyers downloadBuyers = new DownloadBuyers(url);
		    			downloadBuyers.downloadBuyers(district, crop);
		    			buyers = getBuyersFromDb(district, crop);
					}	
				}
			}
			
		}
		return buyers;
	}
	
	private static List<Buyer> getBuyersFromDb(String district, String crop) {
		List<Buyer> buyers =  new ArrayList<Buyer>();
		
    	BuyersParser buyersParser = new BuyersParser(district, crop);
    	
    	String selection = BuyerProviderAPI.BuyersColumns.DISTRICT_ID + "=? and " + BuyerProviderAPI.BuyersColumns.CROP_ID + "=?";
    	String[] selectionArgs = {buyersParser.getDistrictId(), buyersParser.getCropId()};
    	Cursor buyerCursor = MarketLinkApplication.getInstance().getContentResolver().query(BuyerProviderAPI.BuyersColumns.CONTENT_URI, null, selection, selectionArgs, null);
    	Log.i("BUYERDBCOUNT", String.valueOf(buyerCursor.getCount()));
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
	
	public static List<String> getDistrictsFromDb() {
		
		Cursor cursor = MarketLinkApplication.getInstance().getContentResolver().query(DistrictsProviderAPI.DistrictsColumns.CONTENT_URI, null, null, null, null);
		Log.i("DBDISTRICTSCOUNT", String.valueOf(cursor.getCount()));
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
		List<String> districts = getDistrictsFromDb();
		if (districts == null || districts.size() == 0) {
			Log.i("Districts DOWNLOAD", "Districts cache empty, downloading ...");
			DownloadDistrictsAndCrops downloadDistrictsAndCrops = new DownloadDistrictsAndCrops(url);
			downloadDistrictsAndCrops.download();
			districts = getDistrictsFromDb();
		}
		return districts;
	}

	public static List<String> getCrops(String url) {
		List<String> crops = getCropsFromDb();
		if (crops == null || crops.size() == 0) {
			Log.i("Crops DOWNLOAD", "Crops cache empty, downloading ...");
			DownloadDistrictsAndCrops downloadDistrictsAndCrops = new DownloadDistrictsAndCrops(url);
			downloadDistrictsAndCrops.download();
			DistrictsAndCropsParser.getCrops();
			crops = getCropsFromDb();
		}
		return crops;
	}
	
	public static List<String> getCropsFromDb() {
		Cursor cursor = MarketLinkApplication.getInstance().getContentResolver().query(CropsProviderAPI.CropsColumns.CONTENT_URI, null, null, null, null);
		List<String> crops = new ArrayList<String>();
		Log.i("CROPS DB COUNT", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) 
		{
		    crops.add(cursor.getString(1));
		    cursor.moveToNext();
		}
		cursor.close();
		return crops;
	}

	public static boolean farmersInDb(String district, String crop) {
    	MarketPricesParser marketPricesParser = new MarketPricesParser(district, crop);
    	
    	String selection = FarmerProviderAPI.FarmerColumns.DISTRICT_ID + "=? and " + FarmerProviderAPI.FarmerColumns.CROP_ID + "=?";
    	String[] selectionArgs = {marketPricesParser.getDistrictId(), marketPricesParser.getCropId()};
    	Cursor farmerCursor = MarketLinkApplication.getInstance().getContentResolver().query(FarmerProviderAPI.FarmerColumns.CONTENT_URI, null, selection, selectionArgs, null);
    	if(farmerCursor.getCount() > 0) {
    		farmerCursor.close();
    		return true;
    	} else {
    		farmerCursor.close();
    		return false;
    	}
	}
	
	public static boolean buyersInDb(String district, String crop) {
    	BuyersParser buyersParser = new BuyersParser(district, crop);
    	
    	String selection = BuyerProviderAPI.BuyersColumns.DISTRICT_ID + "=? and " + BuyerProviderAPI.BuyersColumns.CROP_ID + "=?";
    	String[] selectionArgs = {buyersParser.getDistrictId(), buyersParser.getCropId()};
    	Cursor buyerCursor = MarketLinkApplication.getInstance().getContentResolver().query(BuyerProviderAPI.BuyersColumns.CONTENT_URI, null, selection, selectionArgs, null);
    	if (buyerCursor.getCount() > 0) {
    		buyerCursor.close();
    		return true;
    	} else {
    		buyerCursor.close();
    		return false;
    	}
	}

	public static boolean districtsinDb() {
		Cursor cursor = MarketLinkApplication.getInstance().getContentResolver().query(DistrictsProviderAPI.DistrictsColumns.CONTENT_URI, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}
}
