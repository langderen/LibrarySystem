package com.example.librarysystem_back.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.example.librarysystem_back.entity.User;
import com.example.librarysystem_back.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            // 第1步，使用id登录
            StpUtil.login(loginUser.getId());
            // 第2步，获取 Token  相关参数
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            // 第3步，返回给前端
            return SaResult.data(tokenInfo);
            //return SaResult.ok("登录成功");
        } else {
            result.put("success", false);
            result.put("msg", "用户名或密码错误");
        }
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userService.register(user);
        result.put("success", success);
        result.put("msg", success ? "注册成功" : "用户名已存在");
        result.put("code", 200);
        return result;
    }


    @RequestMapping("/tokenInfo")
    public SaResult tokenInfo() {
        return SaResult.data(StpUtil.getTokenInfo());
    }

    @GetMapping("/profile")
    public SaResult getUserInfo() {
        // 1. 获取当前登录用户的Token信息
        // StpUtil.getTokenInfo() 会返回一个SaTokenInfo对象，包含token值、过期时间等
        Object tokenInfo = StpUtil.getTokenInfo();

        // 2. 获取当前登录用户的ID
        // 假设你的用户ID是Integer类型，如果是其他类型请相应修改
        Object loginId = StpUtil.getLoginId();

        // 3. 使用用户ID查询数据库，获取用户详细资料
        // 这里的 userService.getById() 是Mybatis-Plus的通用方法，你也可以用自己的查询方法
        User user = userService.getById((Serializable) loginId);

        // 4. 构建用户资料Map（与你原来的profile方法逻辑一致）
        Map<String, Object> profile = new HashMap<>();
        if (user != null) {
            profile.put("userId", user.getId());
            profile.put("username", user.getUsername());
            profile.put("userEmail", user.getEmail());
            profile.put("userPhone", user.getPhone());
            profile.put("role", user.getRole());
        }

        // 5. 创建一个最终的Map，将两部分信息组合起来
        Map<String, Object> result = new HashMap<>();
        result.put("tokenInfo", tokenInfo);
        result.put("profile", profile);

        // 6. 使用SaResult包装并返回
        return SaResult.data(result);
    }
}