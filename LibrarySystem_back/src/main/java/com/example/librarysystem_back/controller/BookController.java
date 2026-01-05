package com.example.librarysystem_back.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    @GetMapping
    public IPage<Book> getBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        return bookService.searchBooks(page, size, null, null, null, null, null);
    }

    @GetMapping("/search")
    public IPage<Book> searchBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String publisher) {

        return bookService.searchBooks(page, size, title, author, isbn, category, publisher);
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @PostMapping
    public Map<String, Object> addBook(@RequestBody Book book) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bookService.addBook(book);
        result.put("success", success);
        result.put("message", success ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping
    public Map<String, Object> updateBook(@RequestBody Book book) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bookService.updateBook(book);
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteBook(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bookService.deleteBook(id);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
}