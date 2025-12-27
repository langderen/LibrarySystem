package com.example.librarysystem_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username; // 用户名（唯一）
    private String password; // 密码（建议加密存储）
    private String role; // 角色：ADMIN/USER
    private String email;
    private String phone;
}