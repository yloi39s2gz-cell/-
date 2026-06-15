package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserDao userDao = new UserDao();
    private final StudentDao studentDao = new StudentDao();

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userDao.getAllStudents());
    }

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable int id) {
        userDao.updateStatus(id, 1);
        Map<String, String> response = new HashMap<>();
        response.put("message", "账号审核通过，已成功启用。");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable int id) {
        userDao.updateStatus(id, 2);
        Map<String, String> response = new HashMap<>();
        response.put("message", "已对该账号实施禁用。");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        boolean success = userDao.deleteUser(id);
        if (success) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "未审批申请清退完毕。");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("清退操作无效：只能清退待审核的账号申请。");
    }

    @PostMapping("/majors")
    public ResponseEntity<?> addMajor(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String desc = body.get("desc");
        boolean success = studentDao.addMajor(name, desc);
        if (success) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "新专业建立完成。");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("专业新增失败。");
    }

    @DeleteMapping("/majors/{id}")
    public ResponseEntity<?> deleteMajor(@PathVariable int id) {
        boolean success = studentDao.deleteMajor(id);
        if (success) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "专业已被废除删除。");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("该专业无法删除，请检查外键约束。");
    }
}