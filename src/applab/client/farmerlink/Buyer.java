package applab.client.farmerlink;

public class Buyer {
    private String name;
    private String telephone;
    private String location;
    
    public Buyer(String name, String telephone, String location) {
        this.name = name;
        this.telephone = telephone;
        this.location = location;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}
