package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentDao studentDao = new StudentDao();

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable int userId) {
        Map<String, Object> profile = studentDao.getProfile(userId);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> body) {
        boolean success = studentDao.updateProfile(body);
        if (success) {
            // 使用传统 HashMap 兼容 Java 8
            Map<String, String> response = new HashMap<>();
            response.put("message", "个人资料更新成功。");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("保存资料失败，请重试。");
    }

    @GetMapping("/directory")
    public ResponseEntity<?> searchDirectory(@RequestParam(required = false) String query) {
        List<Map<String, Object>> list = studentDao.searchDirectory(query);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/majors")
    public ResponseEntity<?> getMajors() {
        return ResponseEntity.ok(studentDao.getMajors());
    }
}