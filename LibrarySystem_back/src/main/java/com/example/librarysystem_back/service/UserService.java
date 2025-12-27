package com.example.librarysystem_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.librarysystem_back.entity.User;

public interface UserService extends IService<User> {
    // 用户登录
    User login(String username, String password);

    // 用户注册
    boolean register(User user);
}