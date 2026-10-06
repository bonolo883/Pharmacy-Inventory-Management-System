package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    // LOGIN
    public String login(String username, String password) {

        String role = null;

        try {

            try (Connection con = DBConnection.getConnection()) {
                String sql = "SELECT role FROM users "
                        + "WHERE username = ? AND password = ?";
                
                PreparedStatement ps = con.prepareStatement(sql);
                
                ps.setString(1, username);
                ps.setString(2, password);
                
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    role = rs.getString("role");
                }
                
                rs.close();
                ps.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return role;
    }

    // UPDATE USER
    public void updateUser(
            int userId,
            String username,
            String password,
            String role) throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE users "
                    + "SET username = ?, "
                    + "password = ?, "
                    + "role = ? "
                    + "WHERE user_id = ?";
            
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setInt(4, userId);
            
            ps.executeUpdate();
            
            ps.close();
        }
    }

    // DELETE USER
    public void deleteUser(int userId) throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM users "
                    + "WHERE user_id = ?";
            
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setInt(1, userId);
            
            ps.executeUpdate();
            
            ps.close();
        }
    }
}

