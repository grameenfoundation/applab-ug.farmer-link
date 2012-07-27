package applab.client.farmerlink;

public class MarketPrices {
    
    private String marketName;
    private String retailPrice;
    private String wholesalePrice;
    
    public MarketPrices(String marketName, String retailPrice, String wholesalePrice) {
        this.marketName = marketName;
        this.retailPrice = retailPrice;
        this.wholesalePrice = wholesalePrice;
    }
    
    public MarketPrices() {
    	
	}

	public String getMarketName() {
        return marketName;
    }
    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }
    public String getRetailPrice() {
        return retailPrice + " Shs";
    }
    
    /**
     * Used to compute total costs
     * @return
     */
    public double getRetailPriceValue() {
    	if (retailPrice != null) {
    		return Math.ceil(Double.parseDouble(retailPrice));
    	}
    	else {
    		return 0;
    	}
    }
    
    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
    }
    public String getWholesalePrice() {
        return wholesalePrice + " Shs";
    }
    public double getWholesalePriceValue() {
    	if (wholesalePrice != null) {
    		return Math.ceil(Double.parseDouble(wholesalePrice));
    	}
    	else {
    		return 0;
    	}
    }
    public void setWholesalePrice(String wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

}
