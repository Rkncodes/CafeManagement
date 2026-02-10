package com.example.cafemanagement;

import java.sql.*;
import java.util.*;

public class OrderStatusLogDAO {

    public List<Object[]> findAll() {
        List<Object[]> list = new ArrayList<>();

        String sql =
            "SELECT order_id, old_status, new_status, changed_at FROM order_status_logs";

        try (Connection con = JDBCConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getTimestamp(4)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
