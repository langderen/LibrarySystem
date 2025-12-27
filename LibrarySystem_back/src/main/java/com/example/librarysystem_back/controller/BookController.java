package com.example.librarysystem_back.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.librarysystem_back.entity.Book;
import com.example.librarysystem_back.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // 分页查询图书
    @GetMapping
    public IPage<Book> getBooks(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return bookService.page(new Page<>(page, size));
    }

    // 根据ID查询图书
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    // 新增图书
    @PostMapping
    public Map<String, Object> addBook(@RequestBody Book book) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bookService.addBook(book);
        result.put("success", success);
        return result;
    }

    // 更新图书
    @PutMapping
    public Map<String, Object> updateBook(@RequestBody Book book) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bookService.updateBook(book);
        result.put("success", success);
        return result;
    }

    // 删除图书
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteBook(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bookService.deleteBook(id);
        result.put("success", success);
        return result;
    }
}