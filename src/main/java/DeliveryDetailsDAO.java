package com.example.cafemanagement;

import java.sql.*;

public class DeliveryDetailsDAO {

    public void saveDelivery(int orderId, String address, String phone)
            throws Exception {

        String sql = """
            INSERT INTO delivery_details(order_id, address, phone)
            VALUES (?, ?, ?)
        """;

        Connection con = JDBCConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, orderId);
        ps.setString(2, address);
        ps.setString(3, phone);

        ps.executeUpdate();
    }
}
