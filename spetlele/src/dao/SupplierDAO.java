package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    public void addSupplier(String name, String contactPerson, String phone, String email, String address) throws SQLException {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, contactPerson); ps.setString(3, phone); ps.setString(4, email); ps.setString(5, address); ps.executeUpdate();
        }
    }

    public void updateSupplier(int id, String name, String contactPerson, String phone, String email, String address) throws SQLException {
        String sql = "UPDATE suppliers SET name=?, contact_person=?, phone=?, email=?, address=? WHERE supplier_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, contactPerson); ps.setString(3, phone); ps.setString(4, email); ps.setString(5, address); ps.setInt(6, id); ps.executeUpdate();
        }
    }

    public void deleteSupplier(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM suppliers WHERE supplier_id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    public List<Object[]> findAll() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT supplier_id, name, contact_person, phone, email, address FROM suppliers ORDER BY name";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(new Object[]{rs.getInt("supplier_id"), rs.getString("name"), rs.getString("contact_person"), rs.getString("phone"), rs.getString("email"), rs.getString("address")});
        }
        return rows;
    }
}
