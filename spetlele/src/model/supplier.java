
package model;

public class supplier {
    private int supplierID;
    private String supplierName;
    private String contact;
    private String address;
    
    public supplier(){}
    
    public supplier(int supplierID,String supplierName, String contact,String address) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.contact = contact;
        this.address = address;
    }
    
    public int getSupplierId() {
        return supplierID;
    }
    
    public void setSupplierId(int supplierId) {
        this.supplierID = supplierId;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public String getContact() {
        return contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
}
