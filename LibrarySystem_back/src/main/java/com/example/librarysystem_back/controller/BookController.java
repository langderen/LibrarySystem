package com.example.librarysystem_back.controller;

import com.example.librarysystem_back.service.BrrowService;
import com.example.librarysystem_back.entity.Book;
import com.example.librarysystem_back.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*") // 允许前端跨域
public class BookController {

    @Autowired private BookMapper bookMapper;
    @Autowired private BrrowService borrowService;

    // 获取列表
    @GetMapping
    public List<Book> getList() {
        return bookMapper.selectList(null);
    }

    // 借阅接口
    @PostMapping("/{bookId}/borrow")
    public String borrow(@PathVariable Long bookId, @RequestParam Long userId) {
        try {
            borrowService.borrowBook(userId, bookId);
            return "借阅成功";
        } catch (RuntimeException e) {
            return e.getMessage(); // 返回"库存不足"
        }
    }
}
