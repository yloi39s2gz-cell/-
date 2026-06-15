package com.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserDao userDao = new UserDao();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String name = body.get("name");
        
        if (username == null || password == null || name == null) {
            return ResponseEntity.badRequest().body("关键字段（账号、密码、姓名）缺失");
        }
        
        boolean success = userDao.registerStudent(username, password, name);
        if (success) {
            // 使用传统 HashMap 代替 Map.of，完美兼容 Java 8
            Map<String, String> response = new HashMap<>();
            response.put("message", "您的注册已提交。请等待管理员审核过关后再进行登录。");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("注册失败，该学号/账号已被注册。");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        
        Map<String, Object> user = userDao.login(username, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("您的账号或密码错误。");
        }
        
        int status = (Integer) user.get("status");
        if (status == 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("您的账户暂处于待审核阶段，请稍后再试。");
        }
        if (status == 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("您的账户已被锁定禁用，请联系管理员核实。");
        }

        int newCount = (Integer) user.get("loginCount") + 1;
        userDao.updateLoginStats((Integer) user.get("id"), newCount);
        user.put("loginCount", newCount);

        return ResponseEntity.ok(user);
    }
}