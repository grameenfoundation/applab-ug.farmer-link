package applab.client.farmerlink;

public class Suppliers {
	private String supplierName;
	private String supplierContact;
	private String supplierLocation;
	
	public Suppliers(String supplierName, String supplierContact, String supplierLocation) {
		this.setSupplierName(supplierName);
		this.setSupplierContact(supplierContact);
		this.setSupplierLocation(supplierLocation);
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierContact() {
		return supplierContact;
	}

	public void setSupplierContact(String supplierContact) {
		this.supplierContact = supplierContact;
	}

	public String getSupplierLocation() {
		return supplierLocation;
	}

	public void setSupplierLocation(String supplierLocation) {
		this.supplierLocation = supplierLocation;
	}

}
