package applab.client.farmerlink;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import applab.client.farmerlink.parsers.DistrictsAndCropsParser;
import applab.client.farmerlink.parsers.MarketPricesParser;
import applab.client.farmerlink.tasks.DownloadBuyers;
import applab.client.farmerlink.tasks.DownloadDistrictsAndCrops;
import applab.client.farmerlink.tasks.DownloadFarmersAndMarketPrices;

public class Repository {
    
	private static List<String> districts;
	private static List<String> crops;
	private static List<MarketPrices> marketPrices;
	private static List<Farmer> farmers;
	private static List<Buyer> buyers;
    
    public static List<Farmer> getFarmersByDistrictAndCrop(String url, String district, String crop) {
        if (farmers == null || farmers.size() == 0) {
        	Log.i("FARMERS DOWNLOAD", "Farmer cache empty, downloading ...");
        	DownloadFarmersAndMarketPrices downloadFarmersAndMarketPrices = new DownloadFarmersAndMarketPrices(url);
        	downloadFarmersAndMarketPrices.downloadFarmersAndMarketPrices(district, crop);
        	farmers = MarketPricesParser.getFarmers();
        }
        else {
        	Log.i("ACCESSING CACHE", "Accessing cache to get farmers");
        }
        return farmers;
    }

    public static List<MarketPrices> getMarketPricesByDistrictAndCrop(String url, String crop, String district) {
        
        if (marketPrices == null || marketPrices.size() == 0) {
        	Log.i("MARKET PRICES DOWNLOAD", "Market Prices cache empty, downloading ...");
        	DownloadFarmersAndMarketPrices downloadFarmersAndMarketPrices = new DownloadFarmersAndMarketPrices(url);
        	marketPrices = downloadFarmersAndMarketPrices.downloadFarmersAndMarketPrices(district, crop);
        }
        else {
        	Log.i("ACCESSING CACHE", "Accessing cache to get Market Prices");
        }
        return marketPrices;
    }

	public static List<Suppliers> getSuppliersByDistrictAndCrop(String crop,
			String district) {
		ArrayList<Suppliers> suppliers = new ArrayList<Suppliers>();
		suppliers.add(new Suppliers("James Onyango", "0772712255", "Namokora"));
		suppliers.add(new Suppliers("James Pombe", "0777777777", "Bukalango"));
		suppliers.add(new Suppliers("Billy Blanks", "0712190999", "Chinatown"));
		
		return suppliers;
	}

	public static List<Buyer> getBuyersByDistrictAndCrop(String url, String crop, String district) {
		if (buyers == null || buyers.size() == 0) {
			Log.i("BUYERS DOWNLOAD", "Buyers cache empty, downloading ...");
			DownloadBuyers downloadBuyers = new DownloadBuyers(url);
			buyers = downloadBuyers.downloadBuyers(district, crop);
		}
		else {
			Log.i("ACCESSING CACHE", "Accessing cache to get buyers");
		}
		return buyers;
	}
	
	public static List<String> getDistricts(String url) {
		if (districts == null) {
			Log.i("DISTRICTS DOWNLOAD", "No district in repository, downloading ...");
			DownloadDistrictsAndCrops downloadDistrictsAndCrops = new DownloadDistrictsAndCrops(url);
			districts = downloadDistrictsAndCrops.download();
		}
		else {
			Log.d("DISTRICTS CACHED", "Accessing cache to get districts");
		}
		return districts;
	}
	public static List<String> getCrops(String url) {
		if (crops == null) {
			Log.i("CROPS DOWNLOAD", "No crop in repository, downloading ...");
			DownloadDistrictsAndCrops downloadDistrictsAndCrops = new DownloadDistrictsAndCrops(url);
			downloadDistrictsAndCrops.download();
			crops = DistrictsAndCropsParser.getCrops();
		}
		else {
			Log.d("CROPS CACHED", "Accessing cache to get crops");
		}
		return crops;
	}
}
