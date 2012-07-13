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
import applab.client.farmerlink.Buyer;
import applab.client.farmerlink.Farmer;
import applab.client.farmerlink.MarketPrices;
import applab.client.farmerlink.parsers.MarketPricesParser.FarmersAndMarketPricesContentHandler;

public class BuyersParser {
	private JSONParser jsonParser;
	private BuyersContentHandler contentHandler;
	private static List<Buyer> buyers = new ArrayList<Buyer>();
	
	public static List<Buyer> getBuyers() {
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
        	if (buyer != null) {
        		buyers.add(buyer);
        	}
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
        			if (key.equalsIgnoreCase("Name")) {
        				buyer.setName((String)value);
        			}
        			else if (key.equalsIgnoreCase("Location")) {
        				buyer.setLocation((String)value);
        			}
        			else if (key.equalsIgnoreCase("PhoneNumber")) {
        				buyer.setTelephone((String)value);
        				buyers.add(buyer);
        				buyer = new Buyer();
        			}
        		}
        	}
            return false;
        }

        @Override
        public boolean startArray() throws ParseException, IOException {
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
