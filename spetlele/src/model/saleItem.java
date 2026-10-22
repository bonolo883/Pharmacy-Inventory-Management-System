package model;

import java.math.BigDecimal;


public class saleItem {
    private int saleItemId;
    private int saleId;
    private int medicineId;
    private String medicineName;
    private int quantitySold;
    private BigDecimal priceAtSale;
    private BigDecimal subtotal;

    public saleItem() {
        this.priceAtSale = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public saleItem(int medicineId, String medicineName, int quantitySold, BigDecimal priceAtSale) {
        this(0, 0, medicineId, medicineName, quantitySold, priceAtSale,
                (priceAtSale == null ? BigDecimal.ZERO : priceAtSale).multiply(BigDecimal.valueOf(quantitySold)));
    }

    public saleItem(int saleItemId, int saleId, int medicineId, String medicineName,
            int quantitySold, BigDecimal priceAtSale, BigDecimal subtotal) {
        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantitySold = quantitySold;
        this.priceAtSale = priceAtSale == null ? BigDecimal.ZERO : priceAtSale;
        this.subtotal = subtotal == null ? BigDecimal.ZERO : subtotal;
    }

    public int getSaleItemId() { return saleItemId; }
    public void setSaleItemId(int saleItemId) { this.saleItemId = saleItemId; }
    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }
    public BigDecimal getPriceAtSale() { return priceAtSale; }
    public void setPriceAtSale(BigDecimal priceAtSale) { this.priceAtSale = priceAtSale == null ? BigDecimal.ZERO : priceAtSale; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal == null ? BigDecimal.ZERO : subtotal; }
}
