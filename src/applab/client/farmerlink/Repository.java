package applab.client.farmerlink;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    
    public static ArrayList<String> getFarmersByDistrictAndCrop(String district, String crop) {
        ArrayList<String> farmers = new ArrayList<String>();
        
        // Add some dummy farmers to use in the autocomplete
        farmers.add("james Onyango");
        farmers.add("james Pombe");
        farmers.add("phillip Banya");
        farmers.add("andrew Makanya");
        farmers.add("billy Blanks");
        
        return farmers;
    }

    public static List<MarketPrices> getMarketPricesByDistrictAndCrop(String crop, String district) {
        
        ArrayList<MarketPrices> marketPrices = new ArrayList<MarketPrices>();
        marketPrices.add(new MarketPrices("First Market", "6000", "4000"));
        marketPrices.add(new MarketPrices("Second Market", "6000", "4000"));
        marketPrices.add(new MarketPrices("Third Market", "6000", "2000"));
        marketPrices.add(new MarketPrices("Fourth Market", "5000", "4000"));
        marketPrices.add(new MarketPrices("Fifth Market", "7000", "4000"));
        marketPrices.add(new MarketPrices("First Market", "6000", "4000"));
        marketPrices.add(new MarketPrices("Second Market", "6000", "4000"));
        marketPrices.add(new MarketPrices("Third Market", "6000", "2000"));
        marketPrices.add(new MarketPrices("Fourth Market", "5000", "4000"));
        marketPrices.add(new MarketPrices("Fifth Market", "7000", "4000"));
        
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

	public static List<Buyer> getBuyersByDistrictAndCrop(String crop,
			String district) {
		ArrayList<Buyer> buyers = new ArrayList<Buyer>();
        buyers.add(new Buyer("Tom Cruise", "+5557777", "Hollywood"));
        buyers.add(new Buyer("Lionel Messi", "+6557777", "Barcelona"));
        buyers.add(new Buyer("Ibracadabra", "+7557777", "Milan"));
        buyers.add(new Buyer("Super Mario", "+8557777", "Manchester"));
		return buyers;
	}
}
