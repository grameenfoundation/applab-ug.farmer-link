package applab.client.farmerlink;

import java.util.ArrayList;

public class MarketSaleObject {
    
    private static MarketSaleObject marketSaleObject = null;  
    private String cropName;
    private String districtName;
    private double transportCost;
    private ArrayList<Farmer> farmers;
    private MarketPrices marketPrices;
    private String selectedOption;
	private Buyer buyer;
    
    private MarketSaleObject() {
        
    }
    
    public static MarketSaleObject getMarketObject() {
        if (null == marketSaleObject) {
            marketSaleObject = new MarketSaleObject();
        }
        return marketSaleObject;
    }
    
    public ArrayList<Farmer> getFarmers() {
        return farmers;
    }

    public void setFarmers(ArrayList<Farmer> farmers) {
        this.farmers = farmers;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public MarketPrices getMarketPrices() {
        return marketPrices;
    }

    public void setMarketPrices(MarketPrices marketPrices) {
        this.marketPrices = marketPrices;
    }
    
    public double getTotalQuantity() {
        double totalQuantity = 0;
        for (Farmer farmer : farmers) {
            totalQuantity += farmer.getQuantity();
        }
        return Math.ceil(totalQuantity);
    }
    
    public double getTotalValue() {
        return getTotalQuantity() * marketPrices.getWholesalePriceValue();
    }
    
    public double getTotalTransactionFee() {
        return Math.ceil(0.1 * getTotalValue());
    }

    public double getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(double transportCost) {
        this.transportCost = transportCost;
    }

    public String getSelectedOption() {
    	return selectedOption;
    }
    
	public void setSelectedOption(String selectedOption) {
		this.selectedOption = selectedOption;
		
	}

	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}
	
	public Buyer getBuyer() {
		return buyer;
	}

}
