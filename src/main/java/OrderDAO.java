package com.example.cafemanagement;

import java.sql.*;

public class OrderDAO {

    public int saveOrder(int userId, String orderType, String orderStatus, double total)
            throws Exception {

        String sql = """
            INSERT INTO orders
            (user_id, order_type, order_status, total_amount)
            VALUES (?, ?, ?, ?)
        """;

        Connection con = JDBCConnection.getConnection();
        PreparedStatement ps =
            con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, userId);
        ps.setString(2, orderType);
        ps.setString(3, orderStatus);
        ps.setDouble(4, total);

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) return rs.getInt(1);
        return 0;
    }
}
