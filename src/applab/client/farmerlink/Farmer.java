package applab.client.farmerlink;

public class Farmer {
    private String name;
    private String id;
    private String phoneNumber;
    private double quantity;
    private String cropOne;
    private String cropTwo;
    private String cropThree;
    private double amountCropOne;
    private double amountCropTwo;
    private double amountCropThree;
    private boolean isSelected;
    private double amountToBeSupplied;
    
    
    public Farmer(String id, String name, double quantity) {
        setId(id);
        setName(name);
        setQuantity(quantity);
        setSelected(false);
    }
    
    public Farmer() {
        setSelected(false);
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

    public String getCropOne() {
        return cropOne;
    }

    public void setCropOne(String cropOne) {
        this.cropOne = cropOne;
    }

    public String getCropTwo() {
        return cropTwo;
    }

    public void setCropTwo(String cropTwo) {
        this.cropTwo = cropTwo;
    }

    public String getCropThree() {
        return cropThree;
    }

    public void setCropThree(String cropThree) {
        this.cropThree = cropThree;
    }

    public double getAmountCropOne() {
        return amountCropOne;
    }

    public void setAmountCropOne(double amountCropOne) {
        this.amountCropOne = amountCropOne;
    }

    public double getAmountCropTwo() {
        return amountCropTwo;
    }

    public void setAmountCropTwo(double amountCropTwo) {
        this.amountCropTwo = amountCropTwo;
    }

    public double getAmountCropThree() {
        return amountCropThree;
    }

    public void setAmountCropThree(double amountCropThree) {
        this.amountCropThree = amountCropThree;
    }
    
    public void setAmountCropOneFromString(String amountCropOneString) {
        if (null != amountCropOneString) {
            this.amountCropOne = Double.valueOf(amountCropOneString);
        }        
    }
    
    public void setAmountCropTwoFromString(String amountCropTwoString) {
        if (null != amountCropTwoString) {
            this.amountCropTwo = Double.valueOf(amountCropTwoString);
        }
    }
    
    public void setAmountCropThreeFromString(String amountCropThreeString) {
        if (null != amountCropThreeString) {
            this.amountCropThree = Double.valueOf(amountCropThreeString);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public double getAmountToBeSupplied() {
        return amountToBeSupplied;
    }

    public void setAmountToBeSupplied(double amountToBeSupplied) {
        this.amountToBeSupplied = amountToBeSupplied;
    }
}
