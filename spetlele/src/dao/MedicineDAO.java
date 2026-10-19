
package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class MedicineDAO {

    public void addMedicine(
            String name, String category, double price, int quantity) {

        try {

            try (Connection con = DBConnection.getConnection()) {
                String sql = "INSERT INTO medicines "
                        + "(medicineName, category, company, medicineType, unitPrice, quantityInStock) "
                        + "VALUES (?, ?, ?, ?,? ,?)";
                
                PreparedStatement ps = con.prepareStatement(sql);
                
                ps.setString(1, name);
                ps.setString(2, category);
                String company = null;
                ps.setString(2, company);
                String medicineType = null;
                ps.setString(2, medicineType);
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
    public void updateMedicine(

            int id, String name, String category, double price, int qty) {

        try {

            Connection con =
                    DBConnection.getConnection();

            String sql =
                    "UPDATE medicines " +
                    "SET medicineName=?, " +
                    "category=?, " +
                    "company=?, " +
                    "medincineType=?, " +
                    "unitPrice=?, " +
                    "quantityInStock=? " +
                    "WHERE medicineId=?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1,name);
            ps.setString(2,category);
            ps.setDouble(3,price);
            ps.setInt(4,qty);
            ps.setInt(5,id);

            ps.executeUpdate();

        } catch(Exception e) {

            e.printStackTrace();
        }
    }
    
    public void deleteMedicine(int id) {
            try {
                Connection con =
                        DBConnection.getConnection();

                String sql =
                        "DELETE FROM medicines " +
                        "WHERE medicineID=?";

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setInt(1,id);

                ps.executeUpdate();

            } catch(Exception e) {

                e.printStackTrace();
            }
        }
    
        
}
