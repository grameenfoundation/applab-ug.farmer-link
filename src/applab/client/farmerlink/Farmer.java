package applab.client.farmerlink;

public class Farmer {
    private String name;
    private String id;
    private double quantity;
    
    public Farmer(String id, String name, double quantity) {
        setId(id);
        setName(name);
        setQuantity(quantity);
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
        return 0.1 * computeRevenue(unitPrice);
    }
    
    @Override
    public String toString() {
        return "Farmer Name: " + getName() + "   Quantity (kgs): " + getQuantity();
    }

}
