package applab.client.farmerlink.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import applab.client.farmerlink.Farmer;
import applab.client.farmerlink.MarketLinkApplication;
import applab.client.farmerlink.MarketPrices;
import applab.client.farmerlink.MarketSaleObject;
import applab.client.farmerlink.provider.BuyerProviderAPI;
import applab.client.farmerlink.provider.CropsProviderAPI;
import applab.client.farmerlink.provider.DistrictsProviderAPI;
import applab.client.farmerlink.provider.CropsProviderAPI.CropsColumns;
import applab.client.farmerlink.provider.DistrictsProviderAPI.DistrictsColumns;
import applab.client.farmerlink.provider.FarmerProviderAPI;
import applab.client.farmerlink.provider.MarketPricesProviderAPI;

public class MarketPricesParser {

    private FarmersAndMarketPricesContentHandler contentHandler;
    private JSONParser jsonParser;
    private String districtId;
    private String cropId;
    private static ArrayList<Farmer> farmers = new ArrayList<Farmer>();
    public static ArrayList<Farmer> getFarmers() {
		return farmers;
	}

    public MarketPricesParser(String district, String crop) {
    	String districtSelection = DistrictsColumns.DISTRICT_NAME + "=?";
		String[] districtSelectionArgs = {district};
		Cursor districtCursor = MarketLinkApplication.getInstance().getContentResolver().
				query(DistrictsProviderAPI.DistrictsColumns.CONTENT_URI, null, districtSelection, districtSelectionArgs, null);
		
		districtCursor.moveToFirst();
		while (districtCursor.isAfterLast() == false) 
		{
		    districtId = districtCursor.getString(districtCursor.getColumnIndex(DistrictsProviderAPI.DistrictsColumns._ID));
		    districtCursor.moveToNext();
		}
		
		districtCursor.close();
		
		String cropSelection = CropsColumns.CROP_NAME + "=?";
		String[] cropSelectionArgs = {crop};
		Cursor cropsCursor = MarketLinkApplication.getInstance().getContentResolver().
				query(CropsProviderAPI.CropsColumns.CONTENT_URI, null, cropSelection, cropSelectionArgs, null);
		
		cropsCursor.moveToFirst();
		while (cropsCursor.isAfterLast() == false) 
		{
		    cropId = cropsCursor.getString(cropsCursor.getColumnIndex(CropsProviderAPI.CropsColumns._ID));
		    cropsCursor.moveToNext();
		}
		cropsCursor.close();
    }
    
	public static List<MarketPrices> getMarketPrices() {
		for (MarketPrices marketPrice : marketPrices) {
			Log.d("MKT_PX", marketPrice.getMarketName() + marketPrice.getRetailPrice() + "-" + marketPrice.getWholesalePrice());
		}
		return marketPrices;
	}

	private static List<MarketPrices> marketPrices = new ArrayList<MarketPrices>();

    /** for debugging purposes in adb logcat */
    private static final String LOG_TAG = "MarketPricesParser";

    public boolean parse(InputStream marketPricesStream) throws IOException, ParseException {

        try {
        	marketPrices = new ArrayList<MarketPrices>();
        	farmers = new ArrayList<Farmer>();
            contentHandler = new FarmersAndMarketPricesContentHandler();
            jsonParser = new JSONParser();

            while (!this.contentHandler.isEnd()) {
                jsonParser.parse(new InputStreamReader(marketPricesStream), contentHandler, true);
            }
            
            return true;
        }
        
        catch (Exception ex) {
            //Log.e(LOG_TAG, ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public String getDistrictId() {
		return districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	public String getCropId() {
		return cropId;
	}

	public void setCropId(String cropId) {
		this.cropId = cropId;
	}

	// Save market prices to database
    private void saveMarketPrices(String districtName) {

    }
    
    private void saveFarmer() {
        
    }

    /**
     * Farmers and Market Prices content Handler for SAX-like parsing of returned JSON
     * These were batched into a single call for performance purposes
     */
    class FarmersAndMarketPricesContentHandler implements ContentHandler {

        private boolean end = false;
        private String key;
        private Farmer farmer = null;
        private MarketPrices marketPrice = null;
        

        public boolean isEnd() {
            return end;
        }

        @Override
        public boolean endArray() throws ParseException, IOException {
        	Log.d(LOG_TAG, "inside endArray");
            return false;
        }

        @Override
        public void endJSON() throws ParseException, IOException {
        	Log.d("ENDJSON", "inside endJSON");
        	Log.d("MARKETPRICES", String.valueOf(marketPrices.size()));
        	Log.d("FARMERS", String.valueOf(farmers.size()));
            end = true;
        }

        @Override
        public boolean endObject() throws ParseException, IOException {
        	Log.d(LOG_TAG, "inside endObject");
        	//marketPrice = null;
        	//farmer = null;
            return false;
        }

        @Override
        public boolean endObjectEntry() throws ParseException, IOException {
            return false;
        }

        @Override
        public boolean primitive(Object value) throws ParseException, IOException {
        	if (key != null) {
        		if (key.equalsIgnoreCase("Name")              || 
        		    key.equalsIgnoreCase("MobileNumber")      || 
        		    key.equalsIgnoreCase("Id")                ||
        		    key.equalsIgnoreCase("CropGrown3")        ||
        		    key.equalsIgnoreCase("CropGrown2")        ||
        		    key.equalsIgnoreCase("CropGrown1")        ||
        		    key.equalsIgnoreCase("AmountCropGrown3")  ||
        		    key.equalsIgnoreCase("AmountCropGrown2")  ||
        		    key.equalsIgnoreCase("AmountCropGrown1")) {
        		    
        			if (key.equalsIgnoreCase("Name")) {
        				farmer = new Farmer();
        				farmer.setName(value.toString());
        			}
        			else if (key.equalsIgnoreCase("MobileNumber") && null != value) {
        				farmer.setPhoneNumber(value.toString());
        			}
        			else if (key.equalsIgnoreCase("Id")) {
        				farmer.setId(value.toString());
        			}
        			else if (key.equalsIgnoreCase("CropGrown3")) {
                        farmer.setCropOne(value == null ? "" : value.toString().toLowerCase());
                    }
        			else if (key.equalsIgnoreCase("CropGrown2")) {
                        farmer.setCropTwo(value == null ? "" : value.toString().toLowerCase());
                    }
        			else if (key.equalsIgnoreCase("CropGrown1")) {
                        farmer.setCropThree(value == null ? "" : value.toString().toLowerCase());
                    }
        			else if (key.equalsIgnoreCase("AmountCropGrown3")) {
                        farmer.setAmountCropThree(value == null || value.toString().equalsIgnoreCase("null") ? 0 : Double.valueOf(value.toString()));
                    }
        			else if (key.equalsIgnoreCase("AmountCropGrown2")) {
                        farmer.setAmountCropTwo(value == null || value.toString().equalsIgnoreCase("null") ? 0 : Double.valueOf(value.toString()));
                    }
        			else if (key.equalsIgnoreCase("AmountCropGrown1")) {
                        farmer.setAmountCropOne(value == null || value.toString().equalsIgnoreCase("null") ? 0 : Double.valueOf(value.toString()));                    
        				farmers.add(farmer);
        				ContentValues values = new ContentValues();
        				values.put(FarmerProviderAPI.FarmerColumns.FARMER_NAME, farmer.getName());
        				values.put(FarmerProviderAPI.FarmerColumns.FARMER_MOBILE, farmer.getPhoneNumber());
        				values.put(FarmerProviderAPI.FarmerColumns.FARMER_ID, farmer.getId());
        				values.put(FarmerProviderAPI.FarmerColumns.DISTRICT_ID, districtId);
        				values.put(FarmerProviderAPI.FarmerColumns.CROP_ID, cropId);
        				values.put(FarmerProviderAPI.FarmerColumns.CROP_GROWN_ONE, farmer.getCropOne());
        				values.put(FarmerProviderAPI.FarmerColumns.CROP_GROWN_TWO, farmer.getCropTwo());
        				values.put(FarmerProviderAPI.FarmerColumns.CROP_GROWN_THREE, farmer.getCropThree());
        				values.put(FarmerProviderAPI.FarmerColumns.AMOUNT_CROP_GROWN_ONE, farmer.getAmountCropOne());
        				values.put(FarmerProviderAPI.FarmerColumns.AMOUNT_CROP_GROWN_TWO, farmer.getAmountCropTwo());
        				values.put(FarmerProviderAPI.FarmerColumns.AMOUNT_CROP_GROWN_THREE, farmer.getAmountCropThree());
        				MarketLinkApplication.getInstance().getContentResolver().insert(FarmerProviderAPI.FarmerColumns.CONTENT_URI, values);
        			}        			
        		}
        		else if (key.equalsIgnoreCase("WholesalePrice") || key.equalsIgnoreCase("RetailPrice") || key.equalsIgnoreCase("MarketName")) {
        			if (key.equalsIgnoreCase("WholesalePrice")) {
        				marketPrice = new MarketPrices();
        				if (null != value) {
        					marketPrice.setWholesalePrice(value.toString());
        				}
        			}
        			else if (key.equalsIgnoreCase("RetailPrice")) {
        				if (null != value) {
        					marketPrice.setRetailPrice(value.toString());
        				}
        			}
        			else if (key.equalsIgnoreCase("MarketName")) {
        				marketPrice.setMarketName(value.toString());
        				if (null != marketPrice.getWholesalePrice()) {
	        				ContentValues values = new ContentValues();
	        				values.put(MarketPricesProviderAPI.MarketPricesColumns.WHOLESALE_PRICE, marketPrice.getWholesalePrice());
	        				values.put(MarketPricesProviderAPI.MarketPricesColumns.RETAIL_PRICE, marketPrice.getRetailPrice());
	        				values.put(MarketPricesProviderAPI.MarketPricesColumns.MARKET_NAME, marketPrice.getMarketName());
	        				values.put(MarketPricesProviderAPI.MarketPricesColumns.DISTRICT_ID, districtId);
	        				values.put(MarketPricesProviderAPI.MarketPricesColumns.CROP_ID, cropId);
	        				MarketLinkApplication.getInstance().getContentResolver().insert(MarketPricesProviderAPI.MarketPricesColumns.CONTENT_URI, values);
        				}
        			}
        		}
        	}
            return false;
        }

        @Override
        public boolean startArray() throws ParseException, IOException {
        	Log.d(LOG_TAG, "inside startArray");
            return false;
        }

        @Override
        public void startJSON() throws ParseException, IOException {
        	Log.d(LOG_TAG, "inside startJSON");
            end = false;
        }

        @Override
        public boolean startObject() throws ParseException, IOException {
            return false;
        }

        @Override
        public boolean startObjectEntry(String key) throws ParseException, IOException {
        	this.key = key;
            return true;
        }

    }

}
