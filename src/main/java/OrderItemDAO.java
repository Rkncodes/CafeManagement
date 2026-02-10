package com.example.cafemanagement;

import java.sql.*;

public class OrderItemDAO {

    public void saveItem(int orderId, int productId, int quantity)
            throws Exception {

        String sql = """
            INSERT INTO order_items(order_id, product_id, quantity)
            VALUES (?, ?, ?)
        """;

        Connection con = JDBCConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, orderId);
        ps.setInt(2, productId);
        ps.setInt(3, quantity);

        ps.executeUpdate();
    }
}
