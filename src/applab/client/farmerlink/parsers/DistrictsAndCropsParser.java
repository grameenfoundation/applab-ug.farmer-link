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

public class DistrictsAndCropsParser {

    private DistrictsAndCropsContentHandler contentHandler;
    private JSONParser jsonParser;
    private static List<String> districts = new ArrayList<String>();
    public static List<String> getDistricts() {
		return districts;
	}

	public List<String> getCrops() {
		return crops;
	}

	private List<String> crops = new ArrayList<String>();

    /** for debugging purposes in adb logcat */
    private static final String LOG_TAG = "DistrictsAndCropsParser";

    public boolean parse(InputStream districtAndCropsStream) throws IOException, ParseException {

        try {
            contentHandler = new DistrictsAndCropsContentHandler();
            jsonParser = new JSONParser();

            while (!this.contentHandler.isEnd()) {
                jsonParser.parse(new InputStreamReader(districtAndCropsStream), contentHandler, true);
            }
            return true;
        }
        
        catch (Exception ex) {
            Log.d(LOG_TAG, "Message "+ex.getMessage());
            return false;
        }
    }

    // Save district to database
    private void saveDistrict(String districtName) {

    }

    // Save crop to database
    private void saveCrops(String cropName) {

    }

    /**
     * Districts and Crops content Handler for SAX-like parsing of returned JSON
     * 
     */
    class DistrictsAndCropsContentHandler implements ContentHandler {

        private boolean end = false;
        private String key;

        public boolean isEnd() {
            return end;
        }

        @Override
        public boolean endArray() throws ParseException, IOException {
            return true;
        }

        @Override
        public void endJSON() throws ParseException, IOException {
            end = true;
        }

        @Override
        public boolean endObject() throws ParseException, IOException {
            return true;
        }

        @Override
        public boolean endObjectEntry() throws ParseException, IOException {
            return true;
        }

        @Override
        public boolean primitive(Object value) throws ParseException, IOException {
        	if (key != null) {
        		Log.d("VALUE", "value="+(String)value);
        		if (key.equalsIgnoreCase("district")) {
        			districts.add((String)value);
        		}
        		else if (key.equalsIgnoreCase("crops")) {
        			crops.add((String)value);
        		}
        	}
            return true;
        }

        @Override
        public boolean startArray() throws ParseException, IOException {
            return true;
        }

        @Override
        public void startJSON() throws ParseException, IOException {
            end = false;
        }

        @Override
        public boolean startObject() throws ParseException, IOException {
            return true;
        }

        @Override
        public boolean startObjectEntry(String key) throws ParseException, IOException {
        	Log.d("KEY", "key="+key);
        	this.key = key;
            return true;
        }

    }

}
