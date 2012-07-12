package applab.client.farmerlink.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;

import android.util.Log;
import applab.client.farmerlink.GlobalConstants;
import applab.client.farmerlink.utilities.HttpHelpers;
import applab.client.farmerlink.utilities.XmlEntityBuilder;

public class FarmersAndMarketPricesDownload {
	private String district;
	private String crop;
	
	public FarmersAndMarketPricesDownload(String district, String crop) {
		this.district = district;
		this.crop = crop;
	}
	
	public List<String> downloadFarmersAndMarketPrices() {
		List<String> farmersAndMarketPrices = null;
		
		InputStream farmersAndMarketPricesStream = null;
		int networkTimeout = 5 * 60 * 1000;
		String url = "http://test.applab.org/FarmerLink/getFarmersAndMarketPrices";
		
		try {
			farmersAndMarketPricesStream = HttpHelpers.postJsonRequestAndGetStream(url,
					(StringEntity)getFarmersAndMarketPricesRequestEntity("Abim", "Cotton"), networkTimeout);
			
			if (farmersAndMarketPricesStream != null) {
				Log.i("SUCCESS", "Farmerlink server returned something");
			}
			
			String filePath = "districtsAndCrops.tmp";
			
			Boolean downloadSuccessful = HttpHelpers.writeStreamToTempFile(farmersAndMarketPricesStream, filePath);
			farmersAndMarketPricesStream.close();
            File file = new File(filePath);
            FileInputStream inputStream = new FileInputStream(file);

            if (downloadSuccessful && inputStream != null) {
                //sendInternalMessage(GlobalConstants.KEYWORD_DOWNLOAD_SUCCESS);
                //parseKeywords(inputStream);
            	//DistrictsAndCropsParser dcp = new DistrictsAndCropsParser();
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
			Log.e("IOException", e.getLocalizedMessage() + " message " + e.getMessage());
		}
		
		return farmersAndMarketPrices;
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
