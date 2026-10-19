
package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SupplierDAO {

    // ADD SUPPLIER
    public void addSupplier(
            String name,
            String contact,
            String email,
            String address) throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO suppliers "
                    + "(supplierName, email, contact, address) "
                    + "VALUES (?, ?, ?,?)";
            
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setString(1, name);
            ps.setString(2, contact);
             ps.setString(1, name);
            ps.setString(3, address);
            
            ps.executeUpdate();
            
            ps.close();
        }
    }

    // UPDATE SUPPLIER
    public void updateSupplier(
            int supplierId,
            String name,
            String email,
            String contact,
            String address) throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE suppliers "
                    + "SET supplierName = ?, "
                    + "contact = ?, "
                    + "email = ?, "
                    + "address = ? "
                    + "WHERE supplierID = ?";
            
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setString(3, email);
            ps.setString(4, address);
            ps.setInt(5, supplierId);
            
            ps.executeUpdate();
            
            ps.close();
        }
    }

    // DELETE SUPPLIER
    public void deleteSupplier(int supplierId) throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM suppliers "
                    + "WHERE supplierID= ?";
            
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setInt(1, supplierId);
            
            ps.executeUpdate();
            
            ps.close();
        }
    }
}

