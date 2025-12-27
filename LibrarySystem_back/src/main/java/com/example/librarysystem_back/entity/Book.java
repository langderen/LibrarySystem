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
    private String title; // 书名
    private String author; // 作者
    private String isbn; // ISBN编号
    private String publisher; // 出版社
    private Integer totalStock; // 总库存
    private Integer availableStock; // 可借库存
}