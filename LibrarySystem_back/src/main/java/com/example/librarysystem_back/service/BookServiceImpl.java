package com.example.librarysystem_back.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.librarysystem_back.entity.Book;
import com.example.librarysystem_back.mapper.BookMapper;
import com.example.librarysystem_back.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Override
    public boolean addBook(Book book) {
        book.setAvailableStock(book.getTotalStock());
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

    @Override
    public IPage<Book> searchBooks(int page, int size, String title, String author, String isbn, String category, String publisher) {
        Page<Book> pageObj = new Page<>(page, size);
        return baseMapper.searchBooks(pageObj, title, author, isbn, category, publisher);
    }
}