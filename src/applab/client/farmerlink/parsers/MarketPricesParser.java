package applab.client.farmerlink.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.util.Log;
import applab.client.farmerlink.Farmer;
import applab.client.farmerlink.MarketPrices;
import applab.client.farmerlink.MarketSaleObject;

public class MarketPricesParser {

    private FarmersAndMarketPricesContentHandler contentHandler;
    private JSONParser jsonParser;
    private static ArrayList<Farmer> farmers = new ArrayList<Farmer>();
    public static ArrayList<Farmer> getFarmers() {
		return farmers;
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
            Log.e(LOG_TAG, ex.getMessage());
            ex.printStackTrace();
            return false;
        }
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
        		//Log.d("KEY", key);
        		Log.d("VALUE", "VALUE="+value.toString());
        		if (key.equalsIgnoreCase("Name") || (key.equalsIgnoreCase("MobileNumber")) || (key.equalsIgnoreCase("Id"))) {
        			if (key.equalsIgnoreCase("Name")) {
        				farmer = new Farmer();
        				farmer.setName(value.toString());
        			}
        			else if (key.equalsIgnoreCase("MobileNumber")) {
        				farmer.setPhoneNumber(value.toString());
        			}
        			else if (key.equalsIgnoreCase("Id")) {
        				farmer.setId(value.toString());
        				farmers.add(farmer);
        			}
        			
        		}
        		else if (key.equalsIgnoreCase("WholesalePrice") || key.equalsIgnoreCase("RetailPrice") || key.equalsIgnoreCase("MarketName")) {
        			if (key.equalsIgnoreCase("WholesalePrice")) {
        				marketPrice = new MarketPrices();
        				marketPrice.setWholesalePrice(value.toString());
        			}
        			else if (key.equalsIgnoreCase("RetailPrice")) {
        				marketPrice.setRetailPrice(value.toString());
        			}
        			else if (key.equalsIgnoreCase("MarketName")) {
        				Log.d("CREATING MKT", "we are in here so market is added");
        				marketPrice.setMarketName((String)value);
        				marketPrices.add(marketPrice);
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
