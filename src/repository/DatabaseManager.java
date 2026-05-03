package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // This creates a file named photo_data.db in your project folder
    private static final String URL = "jdbc:sqlite:photo_data.db";

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            // Create table to store file paths
            String sql = "CREATE TABLE IF NOT EXISTS photos (id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("DB Init Error: " + e.getMessage());
        }
    }

    public static void savePath(String path) {
        String sql = "INSERT INTO photos(path) VALUES(?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, path);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getSavedPaths() {
        List<String> paths = new ArrayList<>();
        String sql = "SELECT path FROM photos";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                paths.add(rs.getString("path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paths;
    }
}