package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class sale {
    private int saleId;
    private Timestamp saleDate;
    private int cashierId;
    private BigDecimal totalAmount;
    private List<saleItem> items;

    public sale() {
        this.saleDate = new Timestamp(System.currentTimeMillis());
        this.totalAmount = BigDecimal.ZERO;
        this.items = new ArrayList<>();
    }

    public sale(int saleId, Timestamp saleDate, int cashierId, BigDecimal totalAmount) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.cashierId = cashierId;
        this.totalAmount = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        this.items = new ArrayList<>();
    }

    public sale(int saleId, Timestamp saleDate, int cashierId, BigDecimal totalAmount, List<saleItem> items) {
        this(saleId, saleDate, cashierId, totalAmount);
        setItems(items);
    }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
    public Timestamp getSaleDate() { return saleDate; }
    public void setSaleDate(Timestamp saleDate) { this.saleDate = saleDate; }
    public int getCashierId() { return cashierId; }
    public void setCashierId(int cashierId) { this.cashierId = cashierId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount == null ? BigDecimal.ZERO : totalAmount; }
    public List<saleItem> getItems() { return new ArrayList<>(items); }
    public void setItems(List<saleItem> items) { this.items = items == null ? new ArrayList<>() : new ArrayList<>(items); }
}

