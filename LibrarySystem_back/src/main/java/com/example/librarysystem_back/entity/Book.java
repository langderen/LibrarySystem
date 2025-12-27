package com.example.librarysystem_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("books")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Integer totalStock;
    private Integer availableStock;
}