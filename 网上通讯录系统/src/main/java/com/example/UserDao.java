package com.example;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao {

    // 学生注册（事务处理，user表插入后获取自增ID再写入student_profile表）
    public boolean registerStudent(String username, String password, String name) {
        String insertUser = "INSERT INTO user (username, password, role, status) VALUES (?, ?, 'STUDENT', 0)";
        String insertProfile = "INSERT INTO student_profile (user_id, name) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                ps1.setString(1, username);
                ps1.setString(2, password);
                ps1.executeUpdate();
                try (ResultSet rs = ps1.getGeneratedKeys()) {
                    if (rs.next()) {
                        int userId = rs.getInt(1);
                        try (PreparedStatement ps2 = conn.prepareStatement(insertProfile)) {
                            ps2.setInt(1, userId);
                            ps2.setString(2, name);
                            ps2.executeUpdate();
                        }
                    }
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 登录查询
    public Map<String, Object> login(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", rs.getInt("id"));
                    user.put("username", rs.getString("username"));
                    user.put("role", rs.getString("role"));
                    user.put("status", rs.getInt("status"));
                    user.put("loginCount", rs.getInt("login_count"));
                    user.put("lastLoginTime", rs.getTimestamp("last_login_time"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 更新登录次数及最近访问时间
    public void updateLoginStats(int userId, int newCount) {
        String sql = "UPDATE user SET last_login_time = ?, login_count = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, newCount);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 管理员：获取所有学生用户
    public List<Map<String, Object>> getAllStudents() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, username, status, login_count, last_login_time, create_time FROM user WHERE role = 'STUDENT'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("username", rs.getString("username"));
                map.put("status", rs.getInt("status"));
                map.put("loginCount", rs.getInt("login_count"));
                map.put("lastLoginTime", rs.getTimestamp("last_login_time"));
                map.put("createTime", rs.getTimestamp("create_time"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 管理员：审核或禁用
    public boolean updateStatus(int id, int status) {
        String sql = "UPDATE user SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 管理员：清退未审批账户（status=0）
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM user WHERE id = ? AND status = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}