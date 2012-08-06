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
import applab.client.farmerlink.Buyer;
import applab.client.farmerlink.Farmer;
import applab.client.farmerlink.MarketLinkApplication;
import applab.client.farmerlink.MarketPrices;
import applab.client.farmerlink.parsers.MarketPricesParser.FarmersAndMarketPricesContentHandler;
import applab.client.farmerlink.provider.BuyerProviderAPI;
import applab.client.farmerlink.provider.CropsProviderAPI;
import applab.client.farmerlink.provider.CropsProviderAPI.CropsColumns;
import applab.client.farmerlink.provider.DistrictsProviderAPI;
import applab.client.farmerlink.provider.DistrictsProviderAPI.DistrictsColumns;

public class BuyersParser {
	private JSONParser jsonParser;
	private BuyersContentHandler contentHandler;
	private static List<Buyer> buyers = new ArrayList<Buyer>();
	private String districtId;
	public String getDistrictId() {
		return districtId;
	}
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	private String cropId;
	
	public String getCropId() {
		return cropId;
	}
	public void setCropId(String cropId) {
		this.cropId = cropId;
	}
	public BuyersParser(String district, String crop) {
		Log.i("DISTRICT", district);
		Log.i("CROP", crop);
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
	public static List<Buyer> getBuyers() {
		Log.i("BUYERS COUNT", String.valueOf(buyers.size()));
		return buyers;
	}
	
	/** for debugging purposes in adb logcat */
    private static final String LOG_TAG = "BuyersParser";

    public boolean parse(InputStream buyersStream) throws IOException, ParseException {

        try {
            contentHandler = new BuyersContentHandler();
            jsonParser = new JSONParser();

            while (!this.contentHandler.isEnd()) {
                jsonParser.parse(new InputStreamReader(buyersStream), contentHandler, true);
            }
            
            return true;
        }
        
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    class BuyersContentHandler implements ContentHandler {

        private boolean end = false;
        private String key;
        private Buyer buyer = null;        

        public boolean isEnd() {
            return end;
        }

        @Override
        public boolean endArray() throws ParseException, IOException {
            return false;
        }

        @Override
        public void endJSON() throws ParseException, IOException {
            end = true;
        }

        @Override
        public boolean endObject() throws ParseException, IOException {
            return false;
        }

        @Override
        public boolean endObjectEntry() throws ParseException, IOException {
            return false;
        }

        @Override
        public boolean primitive(Object value) throws ParseException, IOException {
        	if (key != null) {
        		if (buyer != null) {
        			if (key.equalsIgnoreCase("Name") && null != value) {
        				buyer.setName((String)value);
        			}
        			else if (key.equalsIgnoreCase("Location") && null != value) {
        				buyer.setLocation((String)value);
        			}
        			else if (key.equalsIgnoreCase("Contact") && null != value) {
        				buyer.setTelephone((String)value);
        				buyers.add(buyer);
        				ContentValues values = new ContentValues();
        				values.put(BuyerProviderAPI.BuyersColumns.BUYER_NAME, buyer.getName());
        				values.put(BuyerProviderAPI.BuyersColumns.BUYER_LOCATION, buyer.getLocation());
        				values.put(BuyerProviderAPI.BuyersColumns.BUYER_TELEPHONE, buyer.getTelephone());
        				values.put(BuyerProviderAPI.BuyersColumns.DISTRICT_ID, districtId);
        				values.put(BuyerProviderAPI.BuyersColumns.CROP_ID, cropId);
        				MarketLinkApplication.getInstance().getContentResolver().insert(BuyerProviderAPI.BuyersColumns.CONTENT_URI, values);
        				buyer = new Buyer();
        			}
        		}
        	}
            return false;
        }

        @Override
        public boolean startArray() throws ParseException, IOException {
        	Log.d("INSIDE ARRAY", "");
        	buyer = new Buyer();
            return false;
        }

        @Override
        public void startJSON() throws ParseException, IOException {
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
