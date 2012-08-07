package applab.client.farmerlink;

public class Farmer {
    private String name;
    private String id;
    private String phoneNumber;
    private double quantity;
    
    public Farmer(String id, String name, double quantity) {
        setId(id);
        setName(name);
        setQuantity(quantity);
    }
    
    public Farmer() {
    	
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    
    public double computeRevenue(double unitPrice) {
           return unitPrice * getQuantity();
    }
    
    public double computeTransactionFee(double unitPrice) {
        return Math.ceil(0.1 * computeRevenue(unitPrice));
    }
    
    @Override
    public String toString() {
        return "Farmer Name: " + getName() + "\nQuantity(kg): " + getQuantity();
    }
    
    @Override
    public boolean equals(Object object) {
    	if (object == null) {
    		return false;
    	}
    	
    	if (getClass() != object.getClass()) {
    		return false;
    	}
    	
    	if (getName().equalsIgnoreCase(((Farmer)object).getName())) {
    		return true;
    	}
    	return false;
    	
    }

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

    public String getDisplayName() {
        return getName() + " [" + getId() + "]";
    }
}
