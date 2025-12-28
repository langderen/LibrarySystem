package com.example.librarysystem_back.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.librarysystem_back.entity.User;
import com.example.librarysystem_back.mapper.UserMapper;
import com.example.librarysystem_back.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String username, String password) {
        User user = baseMapper.selectByUsername(username);
        if (user != null && user.getPassword().equals(password)) { // 实际项目需加密验证
            return user;
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        if (baseMapper.selectByUsername(user.getUsername()) != null) {
            return false; // 用户名已存在
        }
        user.setRole("USER"); // 默认普通用户
        return save(user);
    }
}