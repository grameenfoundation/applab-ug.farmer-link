package applab.client.farmerlink;

import java.util.ArrayList;

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
}
