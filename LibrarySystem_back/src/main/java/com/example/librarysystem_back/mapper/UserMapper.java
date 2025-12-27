package com.example.librarysystem_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.librarysystem_back.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 根据用户名查询用户
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(String username);
}