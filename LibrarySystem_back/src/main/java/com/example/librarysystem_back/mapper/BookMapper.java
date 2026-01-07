package com.example.librarysystem_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.librarysystem_back.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
    @Select("<script>" +
            "SELECT * FROM books " +
            "<where>" +
            "<if test='title != null and title != \"\"'> AND title LIKE CONCAT('%', #{title}, '%') </if>" +
            "<if test='author != null and author != \"\"'> AND author LIKE CONCAT('%', #{author}, '%') </if>" +
            "<if test='isbn != null and isbn != \"\"'> AND isbn LIKE CONCAT('%', #{isbn}, '%') </if>" +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "<if test='publisher != null and publisher != \"\"'> AND publisher LIKE CONCAT('%', #{publisher}, '%') </if>" +
            "</where>" +
            "ORDER BY id DESC" +
            "</script>")
    IPage<Book> searchBooks(Page<Book> page,
                          @Param("title") String title,
                          @Param("author") String author,
                          @Param("isbn") String isbn,
                          @Param("category") String category,
                          @Param("publisher") String publisher);

    @Update("UPDATE books SET available_stock = available_stock - 1 WHERE id = #{id} AND available_stock > 0")
    int decreaseStock(Long id);

    @Update("UPDATE books SET available_stock = available_stock + 1 WHERE id = #{id}")
    int increaseStock(Long id);

    @Select("SELECT * FROM books WHERE category = #{category} ORDER BY id DESC")
    List<Book> selectByCategory(@Param("category") String category);

    @Select("SELECT * FROM books ORDER BY id DESC")
    List<Book> selectAll();
}