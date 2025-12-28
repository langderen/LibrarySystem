package com.example.librarysystem_back.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.librarysystem_back.entity.Book;
import com.example.librarysystem_back.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // 分页查询图书

    @GetMapping
    public IPage<Book> getBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            // 新增：搜索关键词，非必传（required = false）
            @RequestParam(required = false) String keyword) {

        // 1. 构建分页对象
        Page<Book> pageObj = new Page<>(page, size);

        // 2. 构建模糊查询条件
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        // 如果关键词不为空/空串，添加多字段模糊匹配
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper
                    .like(Book::getTitle, keyword.trim())    // 书名模糊匹配
                    .or()
                    .like(Book::getAuthor, keyword.trim())   // 作者模糊匹配
                    .or()
                    .like(Book::getIsbn, keyword.trim());    // ISBN 模糊匹配
        }

        // 3. 分页 + 条件查询
        return bookService.page(pageObj, queryWrapper);
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