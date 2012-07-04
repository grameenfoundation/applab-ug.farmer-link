package applab.client.farmerlink.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.util.Log;

public class MarketPricesParser {

    private FarmersAndMarketPricesContentHandler contentHandler;
    private JSONParser jsonParser;

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
    
    private void saveFarmers() {
        
    }

    /**
     * Farmers and Market Prices content Handler for SAX-like parsing of returned JSON
     * These were batched into a single call for performance purposes
     */
    class FarmersAndMarketPricesContentHandler implements ContentHandler {

        private boolean end = false;

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
        public boolean primitive(Object arg0) throws ParseException, IOException {
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
            return false;
        }

        @Override
        public boolean startObjectEntry(String arg0) throws ParseException, IOException {
            return false;
        }

    }

}
