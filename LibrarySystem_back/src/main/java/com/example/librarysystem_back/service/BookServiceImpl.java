package com.example.librarysystem_back.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.librarysystem_back.entity.Book;
import com.example.librarysystem_back.mapper.BookMapper;
import com.example.librarysystem_back.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Override
    public boolean addBook(Book book) {
        book.setAvailableStock(book.getTotalStock()); // 初始可借库存=总库存
        return save(book);
    }

    @Override
    public boolean updateBook(Book book) {
        return updateById(book);
    }

    @Override
    public boolean deleteBook(Long id) {
        return removeById(id);
    }
}