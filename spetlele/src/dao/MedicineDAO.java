
package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class MedicineDAO {

    public void addMedicine(
            String name,
            String category,
            double price,
            int quantity) {

        try {

            try (Connection con = DBConnection.getConnection()) {
                String sql = "INSERT INTO medicines "
                        + "(medicineName, category, unitPrice, quantityInStock) "
                        + "VALUES (?, ?, ?, ?)";
                
                PreparedStatement ps = con.prepareStatement(sql);
                
                ps.setString(1, name);
                ps.setString(2, category);
                ps.setDouble(3, price);
                ps.setInt(4, quantity);
                
                ps.executeUpdate();
                
                System.out.println("Medicine added successfully!");
                
                ps.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
