package dao;

import database.DBConnection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.sale;
import model.saleItem;

/** Database operations for recording and reading sales. */
public class salesDAO {
    /** Saves a sale and its line items atomically, reducing stock in the same transaction. */
    public sale createSale(int cashierId, List<saleItem> items) throws SQLException {
        if (cashierId <= 0) throw new SQLException("A valid cashier account is required.");
        if (items == null || items.isEmpty()) throw new SQLException("A sale must contain at least one item.");

        BigDecimal total = BigDecimal.ZERO;
        for (saleItem item : items) {
            if (item == null || item.getMedicineId() <= 0 || item.getQuantitySold() <= 0)
                throw new SQLException("Sale items need a medicine and a positive quantity.");
            if (item.getPriceAtSale() == null || item.getPriceAtSale().signum() < 0)
                throw new SQLException("Sale item prices cannot be negative.");
            BigDecimal lineTotal = item.getPriceAtSale().multiply(BigDecimal.valueOf(item.getQuantitySold())).setScale(2, RoundingMode.HALF_UP);
            item.setSubtotal(lineTotal);
            total = total.add(lineTotal);
        }

        try (Connection con = DBConnection.getConnection()) {
            boolean originalAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                int saleId = insertSale(con, cashierId, total);
                for (saleItem item : items) {
                    reduceStock(con, item.getMedicineId(), item.getQuantitySold());
                    insertItem(con, saleId, item);
                    item.setSaleId(saleId);
                }
                con.commit();
                Timestamp now = new Timestamp(System.currentTimeMillis());
                return new sale(saleId, now, cashierId, total, items);
            } catch (SQLException ex) {
                try { con.rollback(); } catch (SQLException rollbackError) { ex.addSuppressed(rollbackError); }
                throw ex;
            } finally {
                try { con.setAutoCommit(originalAutoCommit); } catch (SQLException ignored) { }
            }
        }
    }

    private int insertSale(Connection con, int cashierId, BigDecimal total) throws SQLException {
        String sql = "INSERT INTO sales (cashier_id, total_amount) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cashierId); ps.setBigDecimal(2, total); ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("The sale record could not be created.");
                return keys.getInt(1);
            }
        }
    }

    private void reduceStock(Connection con, int medicineId, int quantity) throws SQLException {
        String lockSql = "SELECT quantity_in_stock FROM medicines WHERE medicine_id=? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(lockSql)) {
            ps.setInt(1, medicineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Medicine " + medicineId + " no longer exists.");
                if (rs.getInt("quantity_in_stock") < quantity) throw new SQLException("Insufficient stock for medicine " + medicineId + ". Refresh the cart and try again.");
            }
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE medicines SET quantity_in_stock=quantity_in_stock-? WHERE medicine_id=?")) {
            ps.setInt(1, quantity); ps.setInt(2, medicineId); ps.executeUpdate();
        }
    }

    private void insertItem(Connection con, int saleId, saleItem item) throws SQLException {
        String sql = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, saleId); ps.setInt(2, item.getMedicineId()); ps.setInt(3, item.getQuantitySold());
            ps.setBigDecimal(4, item.getPriceAtSale()); ps.setBigDecimal(5, item.getSubtotal()); ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) item.setSaleItemId(keys.getInt(1)); }
        }
    }

    public sale findById(int saleId) throws SQLException {
        String sql = "SELECT sale_id, sale_date, cashier_id, total_amount FROM sales WHERE sale_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            sale found;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                found = new sale(rs.getInt("sale_id"), rs.getTimestamp("sale_date"), rs.getInt("cashier_id"), rs.getBigDecimal("total_amount"));
            }
            found.setItems(findSaleItems(con, saleId));
            return found;
        }
    }

    public List<saleItem> findSaleItems(int saleId) throws SQLException {
        try (Connection con = DBConnection.getConnection()) { return findSaleItems(con, saleId); }
    }

    private List<saleItem> findSaleItems(Connection con, int saleId) throws SQLException {
        List<saleItem> items = new ArrayList<>();
        String sql = "SELECT si.sale_item_id, si.sale_id, si.medicine_id, m.name, si.quantity_sold, si.price_at_sale, si.subtotal "
                + "FROM sale_items si JOIN medicines m ON m.medicine_id=si.medicine_id WHERE si.sale_id=? ORDER BY si.sale_item_id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(new saleItem(rs.getInt("sale_item_id"), rs.getInt("sale_id"),
                        rs.getInt("medicine_id"), rs.getString("name"), rs.getInt("quantity_sold"),
                        rs.getBigDecimal("price_at_sale"), rs.getBigDecimal("subtotal")));
            }
        }
        return items;
    }
}
