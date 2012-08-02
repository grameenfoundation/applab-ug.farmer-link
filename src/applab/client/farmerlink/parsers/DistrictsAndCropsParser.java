package applab.client.farmerlink.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.ContentValues;
import android.util.Log;
import applab.client.farmerlink.MarketLinkApplication;
import applab.client.farmerlink.provider.DistrictsProviderAPI;

public class DistrictsAndCropsParser {

    private DistrictsAndCropsContentHandler contentHandler;
    private JSONParser jsonParser;
    private static List<String> districts = new ArrayList<String>();
    public static List<String> getDistricts() {
    	Collections.sort(districts);
    	if (districts.contains("Select District")) {
    		districts.remove("Select District");
    	}
    	districts.add(0, "Select District");
		return districts;
	}

	public static List<String> getCrops() {
		Collections.sort(crops);
		if (crops.contains("Select Crop")) {
			crops.remove("Select Crop");
		}
		crops.add(0, "Select Crop");
		
		return crops;
	}

	private static List<String> crops = new ArrayList<String>();

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
            Log.e(LOG_TAG, "ExceptionMessage "+ex.getMessage());
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
        	Log.d(LOG_TAG, "inside endArray");
            return true;
        }

        @Override
        public void endJSON() throws ParseException, IOException {
        	
        	districts.add(0, "Select District");
        	
        	for (String district : districts) {
        		Log.i("DISTRICT", district);
        		
        	}
            end = true;
        }

        @Override
        public boolean endObject() throws ParseException, IOException {
        	Log.d(LOG_TAG, "inside endObject");
            return true;
        }

        @Override
        public boolean endObjectEntry() throws ParseException, IOException {
        	Log.d(LOG_TAG, "inside endObjectEntry");
            return true;
        }

        @Override
        public boolean primitive(Object value) throws ParseException, IOException {
        	if (key != null) {
        		if (key.equalsIgnoreCase("districts")) {
        			if (!districts.contains((String)value)) {
        				districts.add((String)value);
        				ContentValues values = new ContentValues();
        				values.put(DistrictsProviderAPI.DistrictsColumns.DISTRICT_NAME, (String)value);
                		MarketLinkApplication.getInstance().getContentResolver().insert(DistrictsProviderAPI.DistrictsColumns.CONTENT_URI, values);
        			}
        		}
        		else if (key.equalsIgnoreCase("crops")) {
        			if (!crops.contains((String)value)) {
        				crops.add((String)value);
        			}
        		}
        	}
            return true;
        }

        @Override
        public boolean startArray() throws ParseException, IOException {
        	Log.d(LOG_TAG, "inside startArray");
            return true;
        }

        @Override
        public void startJSON() throws ParseException, IOException {
        	Log.d(LOG_TAG, "inside startJSON");
        	/*if (!districts.contains("Select District")) {
        		districts.add("Select District");
        	}
        	if (!crops.contains("Select Crop")) {
        		crops.add("Select Crop");
        	}
        	Log.d("EXIT", "Leaving ...");*/
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
