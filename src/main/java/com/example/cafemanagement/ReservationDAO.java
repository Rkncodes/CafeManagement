package com.example.cafemanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.example.cafemanagement.JDBCConnection;

public class ReservationDAO {

    public List<Object[]> findAll() {

        List<Object[]> rows = new ArrayList<>();

        String sql =
                "SELECT r.reservation_id, " +
                "       u.name AS user_name, " +
                "       r.table_id, " +
                "       r.reservation_time " +
                "FROM reservations r " +
                "JOIN users u ON r.user_id = u.user_id " +
                "ORDER BY r.reservation_time DESC";

        try (
                Connection con = JDBCConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("reservation_id"),
                        rs.getString("user_name"),
                        rs.getInt("table_id"),
                        rs.getTimestamp("reservation_time")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    public void insertReservation(int userId, int tableId) {

        String sql =
                "INSERT INTO reservations (user_id, table_id, reservation_time) " +
                "VALUES (?, ?, NOW())";

        try (
                Connection con = JDBCConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, userId);
            ps.setInt(2, tableId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
