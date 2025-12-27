package com.example.librarysystem_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.librarysystem_back.entity.BorrowRecord;

import java.util.List;

public interface BorrowService extends IService<BorrowRecord> {
    // 借阅图书
    boolean borrowBook(Long userId, Long bookId);

    // 归还图书
    boolean returnBook(Long recordId);

    // 查询用户借阅记录
    List<BorrowRecord> getUserBorrowRecords(Long userId);
}