package com.example.cafemanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public Product insert(Product p) {
        String sql = """
            INSERT INTO products (name, price, category, image_path)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = JDBCConnection.getConnection();
             PreparedStatement ps =
                 con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setString(3, p.getCategory());
            ps.setString(4, p.getImagePath());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(String.valueOf(rs.getInt(1)));
                }
            }
            return p;

        } catch (SQLIntegrityConstraintViolationException e) {
            // duplicate (name + category)
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id, name, price, category, image_path FROM products";

        try (Connection con = JDBCConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("category"),
                    rs.getString("image_path")
                );
                list.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean update(Product p) {
        String sql = """
            UPDATE products
            SET name = ?, price = ?, category = ?, image_path = ?
            WHERE id = ?
        """;

        try (Connection con = JDBCConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setString(3, p.getCategory());
            ps.setString(4, p.getImagePath());
            ps.setInt(5, Integer.parseInt(p.getId()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteById(String id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection con = JDBCConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(id));
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
