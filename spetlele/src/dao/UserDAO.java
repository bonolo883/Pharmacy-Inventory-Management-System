package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private int lastUserId = -1;

    public String login(String username, String password) {
        lastUserId = -1;
        String sql = "SELECT user_id, role FROM users WHERE username=? AND password=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { lastUserId = rs.getInt("user_id"); return rs.getString("role"); }
            }
        } catch (SQLException e) { throw new IllegalStateException("Could not connect to the PIMS database: " + e.getMessage(), e); }
        return null;
    }

    public int getLastUserId() { return lastUserId; }

    public void addUser(String username, String password, String role, String fullName) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, password); ps.setString(3, role); ps.setString(4, fullName); ps.executeUpdate();
        }
    }

    public void updateUser(int userId, String username, String password, String role, String fullName) throws SQLException {
        String sql = password == null || password.isEmpty()
                ? "UPDATE users SET username=?, role=?, full_name=? WHERE user_id=?"
                : "UPDATE users SET username=?, password=?, role=?, full_name=? WHERE user_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1; ps.setString(i++, username);
            if (password != null && !password.isEmpty()) ps.setString(i++, password);
            ps.setString(i++, role); ps.setString(i++, fullName); ps.setInt(i, userId); ps.executeUpdate();
        }
    }

    public void deleteUser(int userId) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE user_id=? AND role='Cashier'")) {
            ps.setInt(1, userId); ps.executeUpdate();
        }
    }

    public List<Object[]> findAll() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT user_id, username, role, full_name FROM users ORDER BY user_id"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(new Object[]{rs.getInt("user_id"), rs.getString("username"), rs.getString("role"), rs.getString("full_name")});
        }
        return rows;
    }
}
