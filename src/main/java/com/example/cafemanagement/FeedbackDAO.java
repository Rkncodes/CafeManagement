package com.example.cafemanagement;

import java.sql.*;
import java.util.*;

public class FeedbackDAO {

    public List<Object[]> findAll() {
        List<Object[]> rows = new ArrayList<>();

        String sql =
            "SELECT u.name, f.order_id, f.rating, f.comment " +
            "FROM feedback f " +
            "JOIN users u ON f.user_id = u.user_id " +
            "ORDER BY f.feedback_id DESC";

        try (
            Connection con = JDBCConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getString(1), // user name
                    rs.getInt(2),    // order id
                    rs.getInt(3),    // rating
                    rs.getString(4)  // comment
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }
}
