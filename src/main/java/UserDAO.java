package com.example.cafemanagement;

import java.sql.*;

public class UserDAO {

    public int saveUser(String name, String phone, String email) throws Exception {
        String sql = "INSERT INTO users(name, phone, email) VALUES (?, ?, ?)";
        Connection con = JDBCConnection.getConnection();
        PreparedStatement ps =
            con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, name);
        ps.setString(2, phone);
        ps.setString(3, email);

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) return rs.getInt(1);
        return 0;
    }
}
