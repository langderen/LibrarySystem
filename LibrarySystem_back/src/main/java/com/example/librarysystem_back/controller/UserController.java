package com.example.librarysystem_back.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.librarysystem_back.entity.User;
import com.example.librarysystem_back.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
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
            StpUtil.login(loginUser.getId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return SaResult.data(tokenInfo);
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
        Object tokenInfo = StpUtil.getTokenInfo();
        Object loginId = StpUtil.getLoginId();
        User user = userService.getById((Serializable) loginId);

        Map<String, Object> profile = new HashMap<>();
        if (user != null) {
            profile.put("userId", user.getId());
            profile.put("username", user.getUsername());
            profile.put("userEmail", user.getEmail());
            profile.put("userPhone", user.getPhone());
            profile.put("role", user.getRole());
            profile.put("sex", user.getSex());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tokenInfo", tokenInfo);
        result.put("profile", profile);

        return SaResult.data(result);
    }

    @GetMapping("/admin/all")
    public Map<String, Object> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role) {

        Map<String, Object> result = new HashMap<>();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (StringUtils.isNotBlank(email)) {
            queryWrapper.like(User::getEmail, email);
        }
        if (StringUtils.isNotBlank(role)) {
            queryWrapper.eq(User::getRole, role);
        }

        queryWrapper.orderByDesc(User::getId);

        IPage<User> pageObj = new Page<>(page, size);
        IPage<User> userPage = userService.page(pageObj, queryWrapper);

        result.put("records", userPage.getRecords());
        result.put("total", userPage.getTotal());
        result.put("current", userPage.getCurrent());
        result.put("pages", userPage.getPages());

        return result;
    }

    @GetMapping("/admin/{id}")
    public SaResult getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            return SaResult.data(user);
        }
        return SaResult.error("用户不存在");
    }

    @PutMapping("/admin/update")
    public SaResult updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            return SaResult.error("用户ID不能为空");
        }

        User existingUser = userService.getById(user.getId());
        if (existingUser == null) {
            return SaResult.error("用户不存在");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        }

        boolean success = userService.updateById(user);
        return success ? SaResult.ok("更新成功") : SaResult.error("更新失败");
    }

    @DeleteMapping("/admin/{id}")
    public SaResult deleteUser(@PathVariable Long id) {
        if (id == null) {
            return SaResult.error("用户ID不能为空");
        }

        User user = userService.getById(id);
        if (user == null) {
            return SaResult.error("用户不存在");
        }

        if ("ADMIN".equals(user.getRole())) {
            return SaResult.error("不能删除管理员账户");
        }

        boolean success = userService.removeById(id);
        return success ? SaResult.ok("删除成功") : SaResult.error("删除失败");
    }

}