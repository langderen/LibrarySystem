package com.example.librarysystem_back.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.librarysystem_back.entity.Book;

public interface BookService extends IService<Book> {
    boolean addBook(Book book);

    boolean updateBook(Book book);

    boolean deleteBook(Long id);

    IPage<Book> searchBooks(int page, int size, String title, String author, String isbn, String category, String publisher);
}