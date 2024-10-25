package org.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class Main {


    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://192.168.2.202:5432/namordnik", "postgres", "root")) {

            if (conn != null) {
                System.out.println("Connected to the database!");
                //getAllProducts();
                savePhotoProducts();
            } else {
                System.out.println("Failed to make connection!");
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static byte[] convertFileToByteArray(String filePath) throws IOException {
        File file = new File(filePath);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            for (int len; (len = fileInputStream.read(buffer)) != -1; ) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }


    boolean insertFile(int id, String picture) throws SQLException, IOException {
        var updateSql = "UPDATE products "
                + "SET photo = ?"
                + "WHERE product_id = ?";
        try (Connection conn = connect()) {
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(updateSql);
                stmt.setBytes(1, convertFileToByteArray(picture));
                stmt.setInt(2, id);
                stmt.executeUpdate();
                return true;
            }
        }
        return false;
    }


    static Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.2.202:5432/namordnik", "postgres", "root");
        return connection;
    }

    public static void getAllProducts() throws SQLException, IOException {

        String selectSql = "SELECT product_id, photo_title FROM products";

        try (Connection conn = connect()) {
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(selectSql);
                System.out.println(stmt);
                // Execute the query
                ResultSet rs = stmt.executeQuery();
                // Process the ResultSet to retrieve all data
                while (rs.next()) {
                    int id = rs.getInt("product_id");
                    String photoTitle = rs.getString("photo_title");
                    System.out.println(id + ", " + photoTitle);
                    System.out.println(System.getProperty("user.dir") + "/images/" + photoTitle);
                }

            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void savePhotoProducts() throws SQLException, IOException {

        String selectSql = "SELECT product_id, photo_title FROM products";
        var updateSql = "UPDATE products "
                + "SET photo = ?"
                + "WHERE product_id = ?";
        try (Connection conn = connect()) {
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(selectSql);
                System.out.println(stmt);
                // Execute the query
                ResultSet rs = stmt.executeQuery();
                // Process the ResultSet to retrieve all data
                while (rs.next()) {
                    int id = rs.getInt("product_id");
                    String photoTitle = rs.getString("photo_title");
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    if (!photoTitle.isEmpty()) {
                        updateStmt.setBytes(1, convertFileToByteArray(System.getProperty("user.dir") + "/images/" + photoTitle));
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                    }
                    System.out.println(id + ", " + photoTitle);
                }

            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


}
