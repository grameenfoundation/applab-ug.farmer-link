package applab.client.farmerlink;

import java.util.ArrayList;

public class MarketSaleObject {
    
    private static MarketSaleObject marketSaleObject = null; 
    
    private double quantity;
    private String cropName;
    private String districtName;
    private double transportCost;
    private double transactionFee;
    private ArrayList<Farmer> farmers;
    private MarketPrices marketPrices;
    private String selectedOption;
	private Buyer buyer;
    private String transactionType;
    public static final String BUY = "buy";
    public static final String MARKETSALE = "marketSale";
    public static final String BUYERSALE = "buyerSale";
    
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

    public double getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(double transportCost) {
        this.transportCost = transportCost;
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
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

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String marketSale) {
		this.transactionType = marketSale;
	}

    public double getInitalTotalQuantity() {
        return quantity;
    }

    public void setInitialQuantity(double quantity) {
        this.quantity = quantity;
    }

}
