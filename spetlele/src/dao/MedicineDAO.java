package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MedicineDAO {
    public void addMedicine(String name, String category, String company, String type,
        double price, int quantity, int reorderLevel, Date expiryDate, Integer supplierId) throws SQLException {
        String sql = "INSERT INTO medicines (name, category, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, name, category, company, type, price, quantity, reorderLevel, expiryDate, supplierId);
            ps.executeUpdate();
        }
    }

    public void updateMedicine(int id, String name, String category, String company, String type,
            double price, int quantity, int reorderLevel, Date expiryDate, Integer supplierId) throws SQLException {
        String sql = "UPDATE medicines SET name=?, category=?, company=?, medicine_type=?, price=?, quantity_in_stock=?, reorder_level=?, expiry_date=?, supplier_id=? WHERE medicine_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, name, category, company, type, price, quantity, reorderLevel, expiryDate, supplierId);
            ps.setInt(10, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, String name, String category, String company, String type,
            double price, int quantity, int reorderLevel, Date expiryDate, Integer supplierId) throws SQLException {
        ps.setString(1, name); ps.setString(2, category); ps.setString(3, company); ps.setString(4, type);
        ps.setDouble(5, price); ps.setInt(6, quantity); ps.setInt(7, reorderLevel);
        if (expiryDate == null) ps.setNull(8, java.sql.Types.DATE); else ps.setDate(8, expiryDate);
        if (supplierId == null) ps.setNull(9, java.sql.Types.INTEGER); else ps.setInt(9, supplierId);
    }

    public void deleteMedicine(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM medicines WHERE medicine_id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
}
