package com.example.librarysystem_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.librarysystem_back.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
    // 核心 SQL：只有当库存 > 0 时才减 1，返回受影响行数
    @Update("UPDATE books SET available_stock = available_stock - 1 WHERE id = #{id} AND available_stock > 0")
    int decreaseStock(Long id);
}
