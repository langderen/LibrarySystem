package com.example.librarysystem_back.controller;

import com.example.librarysystem_back.entity.BorrowRecord;
import com.example.librarysystem_back.service.BorrowService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/borrows")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // 借阅图书
    @PostMapping
    public Map<String, Object> borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = borrowService.borrowBook(userId, bookId);
        result.put("success", success);
        result.put("msg", success ? "借阅成功" : "库存不足或图书不存在");
        return result;
    }

    // 归还图书
    @PutMapping("/return")
    public Map<String, Object> returnBook(@RequestParam Long recordId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = borrowService.returnBook(recordId);
        result.put("success", success);
        return result;
    }

    // 查询用户借阅记录
    @GetMapping("/getBrrowByUserId")
    public List<BorrowRecord> getUserBorrows(@RequestParam Long userId) {
        return borrowService.getUserBorrowRecords(userId);
    }
}