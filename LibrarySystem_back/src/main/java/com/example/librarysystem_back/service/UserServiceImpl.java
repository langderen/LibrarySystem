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
        User user = null;
        if (username != null) {
            user = baseMapper.selectByUsername(username);
            if (user == null) {
                try {
                    Long id = Long.parseLong(username.trim());
                    user = baseMapper.selectById(id);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        if (user != null && user.getPassword().equals(password)) {
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