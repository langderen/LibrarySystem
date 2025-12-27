package com.example.librarysystem_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.librarysystem_back.entity.Book;

public interface BookService extends IService<Book> {
    // 新增图书
    boolean addBook(Book book);

    // 更新图书信息
    boolean updateBook(Book book);

    // 删除图书
    boolean deleteBook(Long id);
}