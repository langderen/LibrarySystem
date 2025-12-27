package com.example.librarysystem_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.librarysystem_back.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
    // 借阅时减少库存（仅当库存>0）
    @Update("UPDATE books SET available_stock = available_stock - 1 WHERE id = #{id} AND available_stock > 0")
    int decreaseStock(Long id);

    // 归还时增加库存
    @Update("UPDATE books SET available_stock = available_stock + 1 WHERE id = #{id}")
    int increaseStock(Long id);
}