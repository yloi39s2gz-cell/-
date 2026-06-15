package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDao {

    // 获取单条档案卡
    public Map<String, Object> getProfile(int userId) {
        String sql = "SELECT * FROM student_profile WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("userId", rs.getInt("user_id"));
                    map.put("name", rs.getString("name"));
                    map.put("majorId", rs.getObject("major_id"));
                    map.put("className", rs.getString("class_name"));
                    map.put("enrollYear", rs.getObject("enroll_year"));
                    map.put("gradYear", rs.getObject("grad_year"));
                    map.put("company", rs.getString("company"));
                    map.put("city", rs.getString("city"));
                    map.put("phone", rs.getString("phone"));
                    map.put("email", rs.getString("email"));
                    return map;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 学生修改资料
    public boolean updateProfile(Map<String, Object> p) {
        String sql = "UPDATE student_profile SET name=?, major_id=?, class_name=?, enroll_year=?, grad_year=?, company=?, city=?, phone=?, email=? WHERE user_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, (String) p.get("name"));
            ps.setObject(2, p.get("majorId"));
            ps.setString(3, (String) p.get("className"));
            ps.setObject(4, p.get("enrollYear"));
            ps.setObject(5, p.get("gradYear"));
            ps.setString(6, (String) p.get("company"));
            ps.setString(7, (String) p.get("city"));
            ps.setString(8, (String) p.get("phone"));
            ps.setString(9, (String) p.get("email"));
            ps.setInt(10, (Integer) p.get("userId"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 查询其他校友（必须为通过审核 status = 1 状态）
    public List<Map<String, Object>> searchDirectory(String query) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT sp.*, m.major_name FROM student_profile sp " +
                     "JOIN user u ON sp.user_id = u.id " +
                     "LEFT JOIN major m ON sp.major_id = m.id " +
                     "WHERE u.status = 1 AND (sp.name LIKE ? OR sp.city LIKE ? OR sp.company LIKE ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + (query == null ? "" : query) + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", rs.getString("name"));
                    map.put("majorName", rs.getString("major_name"));
                    map.put("className", rs.getString("class_name"));
                    map.put("enrollYear", rs.getObject("enroll_year"));
                    map.put("gradYear", rs.getObject("grad_year"));
                    map.put("company", rs.getString("company"));
                    map.put("city", rs.getString("city"));
                    map.put("phone", rs.getString("phone"));
                    map.put("email", rs.getString("email"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 获取专业字段
    public List<Map<String, Object>> getMajors() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, major_name FROM major";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("majorName", rs.getString("major_name"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 维护：增设新专业
    public boolean addMajor(String name, String desc) {
        String sql = "INSERT INTO major (major_name, description) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, desc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 维护：废除专业
    public boolean deleteMajor(int id) {
        String sql = "DELETE FROM major WHERE id = ?";
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