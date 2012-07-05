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
    
    public String getMarketName() {
        return marketName;
    }
    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }
    public String getRetailPrice() {
        return retailPrice;
    }
    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
    }
    public String getWholesalePrice() {
        return wholesalePrice;
    }
    public void setWholesalePrice(String wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

}
