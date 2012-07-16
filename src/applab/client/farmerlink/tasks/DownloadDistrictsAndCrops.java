package applab.client.farmerlink.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.simple.parser.ParseException;

import android.util.Log;
import applab.client.farmerlink.GlobalConstants;
import applab.client.farmerlink.parsers.DistrictsAndCropsParser;
import applab.client.farmerlink.utilities.HttpHelpers;
import applab.client.farmerlink.utilities.XmlEntityBuilder;

public class DownloadDistrictsAndCrops {

	private String url;
	public DownloadDistrictsAndCrops(String url) {
		this.url = url;
	}

	public List<String> download() {
		InputStream districtsAndCropsStream;
		int networkTimeout = 5 * 60 * 1000;

		try {
			districtsAndCropsStream = HttpHelpers.postJsonRequestAndGetStream(url,
					(StringEntity)getDistrictsAndCropsRequestEntity(), networkTimeout);
			
			if (districtsAndCropsStream != null) {
				Log.d("SUCCESS", "Farmerlink server returned something");
			}
			
            String filePath = "sdcard/districtsAndCrops.tmp";
			Boolean downloadSuccessful = HttpHelpers.writeStreamToTempFile(districtsAndCropsStream, filePath);
			districtsAndCropsStream.close();
            File file = new File(filePath);
			//String filePath2 = "sdcard/testJson.tmp";
            //File file = new File(filePath2);
            FileInputStream inputStream = new FileInputStream(file);

            if (downloadSuccessful && inputStream != null) {
            	DistrictsAndCropsParser districtsAndCropsParser = new DistrictsAndCropsParser();
            	try {
            		Log.d("PARSING", "parsing follows...");
            		districtsAndCropsParser.parse(inputStream);
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
		return DistrictsAndCropsParser.getDistricts();
	}
	
	static AbstractHttpEntity getDistrictsAndCropsRequestEntity() throws UnsupportedEncodingException {

        XmlEntityBuilder xmlRequest = new XmlEntityBuilder();
        xmlRequest. writeStartElement("FarmerLinkRequest", GlobalConstants.XMLNAMESPACE);
        xmlRequest.writeEndElement();
        return xmlRequest.getEntity();
    }
}
