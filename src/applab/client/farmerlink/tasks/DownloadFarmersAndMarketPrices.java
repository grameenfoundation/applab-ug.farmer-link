package applab.client.farmerlink.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.simple.parser.ParseException;

import android.content.ContentValues;
import android.util.Log;
import applab.client.farmerlink.GlobalConstants;
import applab.client.farmerlink.MarketLinkApplication;
import applab.client.farmerlink.MarketPrices;
import applab.client.farmerlink.parsers.MarketPricesParser;
import applab.client.farmerlink.provider.FarmerVersionProvider;
import applab.client.farmerlink.provider.FarmerVersionProviderAPI;
import applab.client.farmerlink.provider.MarketPricesVersionProviderAPI;
import applab.client.farmerlink.utilities.HttpHelpers;
import applab.client.farmerlink.utilities.XmlEntityBuilder;

public class DownloadFarmersAndMarketPrices {
	private String url;
	public DownloadFarmersAndMarketPrices(String url) {
		this.url = url;Log.i("URL", url);
	}

	public List<MarketPrices> downloadFarmersAndMarketPrices(String district, String crop) {
		InputStream farmersAndMarketPricesStream = null;
		int networkTimeout = 5 * 60 * 1000;
		
		try {
			farmersAndMarketPricesStream = HttpHelpers.postJsonRequestAndGetStream(url,
					(StringEntity)getFarmersAndMarketPricesRequestEntity(district, crop), networkTimeout);
			
			if (farmersAndMarketPricesStream != null) {
				Log.i("SUCCESS", "Farmerlink server returned something");
			}
			
			String filePath = "sdcard/farmersAndMarketPrices.tmp";
			
			Boolean downloadSuccessful = HttpHelpers.writeStreamToTempFile(farmersAndMarketPricesStream, filePath);
			
			farmersAndMarketPricesStream.close();
            File file = new File(filePath);
            FileInputStream inputStream = new FileInputStream(file);

            if (downloadSuccessful && inputStream != null) {
            	MarketPricesParser marketPricesParser = new MarketPricesParser(district, crop);
            	ContentValues marketPricesVersionContentValues = new ContentValues();
    			marketPricesVersionContentValues.put(MarketPricesVersionProviderAPI.MarketPricesVersionColumns.DISTRICT_ID, marketPricesParser.getDistrictId());
    			marketPricesVersionContentValues.put(MarketPricesVersionProviderAPI.MarketPricesVersionColumns.CROP_ID, marketPricesParser.getCropId());
    			marketPricesVersionContentValues.put(MarketPricesVersionProviderAPI.MarketPricesVersionColumns.VERSION, new Date().toString());
    			
    			ContentValues farmerVersionsContentValues = new ContentValues();
    			farmerVersionsContentValues.put(FarmerVersionProviderAPI.FarmerVersionsColumns.DISTRICT_ID, marketPricesParser.getDistrictId());
    			farmerVersionsContentValues.put(FarmerVersionProviderAPI.FarmerVersionsColumns.CROP_ID, marketPricesParser.getCropId());
    			farmerVersionsContentValues.put(FarmerVersionProviderAPI.FarmerVersionsColumns.VERSION, new Date().toString());
    			
            	try {
            		Log.d("PARSING", "parsing begins ...");
					marketPricesParser.parse(inputStream);
					MarketLinkApplication.getInstance().getContentResolver().delete(MarketPricesVersionProviderAPI.MarketPricesVersionColumns.CONTENT_URI, null, null);
					MarketLinkApplication.getInstance().getContentResolver().insert(MarketPricesVersionProviderAPI.MarketPricesVersionColumns.CONTENT_URI, marketPricesVersionContentValues);
					MarketLinkApplication.getInstance().getContentResolver().delete(FarmerVersionProviderAPI.FarmerVersionsColumns.CONTENT_URI, null, null);
					MarketLinkApplication.getInstance().getContentResolver().insert(FarmerVersionProviderAPI.FarmerVersionsColumns.CONTENT_URI, farmerVersionsContentValues);
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
            }
            else {
                //sendInternalMessage(GlobalConstants.KEYWORD_DOWNLOAD_FAILURE);
            }

            if (inputStream != null) {
                inputStream.close();
                file.delete();
            }
		}
		catch(IOException e) {
			Log.e("IOException", e.getLocalizedMessage());
		}
		
		return MarketPricesParser.getMarketPrices();
	}
	
	static AbstractHttpEntity getFarmersAndMarketPricesRequestEntity(String district, String crop) throws UnsupportedEncodingException {

        XmlEntityBuilder xmlRequest = new XmlEntityBuilder();
        xmlRequest. writeStartElement("FarmerLinkRequest", GlobalConstants.XMLNAMESPACE);
        xmlRequest.writeStartElement("district");
        xmlRequest.writeText(district);
        xmlRequest.writeEndElement();

        xmlRequest.writeStartElement("crop");
        xmlRequest.writeText(crop);
        xmlRequest.writeEndElement();
        
        xmlRequest.writeEndElement();
        return xmlRequest.getEntity();
    }
	
}
