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
    private ArrayList<Farmer> farmers = new ArrayList<Farmer>();
    public ArrayList<Farmer> getFarmers() {
		return farmers;
	}

	public ArrayList<MarketPrices> getMarketPrices() {
		return marketPrices;
	}

	private ArrayList<MarketPrices> marketPrices = new ArrayList<MarketPrices>();

    /** for debugging purposes in adb logcat */
    private static final String LOG_TAG = "MarketPricesParser";

    public boolean parse(InputStream marketPricesStream) throws IOException, ParseException {

        try {
            contentHandler = new FarmersAndMarketPricesContentHandler();
            jsonParser = new JSONParser();

            while (!this.contentHandler.isEnd()) {
                jsonParser.parse(new InputStreamReader(marketPricesStream), contentHandler, true);
            }
            
            return true;
        }
        
        catch (Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
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
            return false;
        }

        @Override
        public void endJSON() throws ParseException, IOException {
            end = true;
        }

        @Override
        public boolean endObject() throws ParseException, IOException {
        	if (marketPrice != null) {
        		marketPrices.add(marketPrice);
        	}
        	else if (farmer != null) {
        		farmers.add(farmer);
        	}
        	marketPrice = null;
        	farmer = null;
            return false;
        }

        @Override
        public boolean endObjectEntry() throws ParseException, IOException {
            return false;
        }

        @Override
        public boolean primitive(Object value) throws ParseException, IOException {
        	if (key != null) {
        		if (farmer != null) {
        			if (key.equalsIgnoreCase("Name")) {
        				farmer.setName((String)value);
        			}
        			else if (key.equalsIgnoreCase("ID")) {
        				farmer.setId((String)value);
        			}
        			else if (key.equalsIgnoreCase("PhoneNumber")) {
        				farmer.setPhoneNumber((String)value);
        			}
        		}
        		else if (marketPrice != null) {
        			if (key.equalsIgnoreCase("marketName")) {
        				marketPrice.setMarketName((String)value);
        			}
        			else if (key.equalsIgnoreCase("retailPrice")) {
        				marketPrice.setRetailPrice((String)value);
        			}
        			else if (key.equalsIgnoreCase("PhoneNumber")) {
        				marketPrice.setWholesalePrice((String)value);
        			}
        		}
        	}
            return false;
        }

        @Override
        public boolean startArray() throws ParseException, IOException {
        	
            return false;
        }

        @Override
        public void startJSON() throws ParseException, IOException {
            end = false;
        }

        @Override
        public boolean startObject() throws ParseException, IOException {
        	if (key != null) {
        		if (key.equalsIgnoreCase("farmers")) {
        			farmer = new Farmer();
        		}
        		else if (key.equalsIgnoreCase("marketprices")) {
        			marketPrice = new MarketPrices();
        		}
        	}
            return false;
        }

        @Override
        public boolean startObjectEntry(String key) throws ParseException, IOException {
        	this.key = key;
            return true;
        }

    }

}
