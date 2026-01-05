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

    // 管理员查询所有借阅记录
    @GetMapping("/admin/all")
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowService.getAllBorrowRecords();
    }

    // 管理员按图书ID查询借阅记录
    @GetMapping("/admin/byBookId/{bookId}")
    public List<BorrowRecord> getBorrowRecordsByBookId(@PathVariable Long bookId) {
        return borrowService.getBorrowRecordsByBookId(bookId);
    }

    // 管理员催还图书
    @PostMapping("/admin/remind")
    public Map<String, Object> remindReturn(@RequestParam Long recordId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = borrowService.remindReturn(recordId);
        result.put("success", success);
        result.put("msg", success ? "催还通知已发送" : "发送失败或记录不存在");
        return result;
    }
}