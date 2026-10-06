
package model;


public class medicine {

    private int medicineID;
    private String medicineName;
    private String category;
    private double unitPrice;
    private int quantity;

    public medicine(){
    }

    public medicine( int medicineID, String medicineName, String category, double unitPrice, int quantity) {
        this.medicineID = medicineID;
        this.medicineName = medicineName;
        this.category = category;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public int getMedicineId() {
        return medicineID;
    }

    public void setMedicineId(int medicineID) {
       this.medicineID = medicineID;    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(
            String medicineName) {
        this.medicineName = medicineName;
    }

    public String getCategory (){
        return category;
    }
    
    public void setCategory(String category) {
       this.category = category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
         this.unitPrice = unitPrice;
     }

    public int getQuantity() {
      return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

