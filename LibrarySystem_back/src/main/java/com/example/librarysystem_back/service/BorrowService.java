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

    // 管理员查询所有借阅记录
    List<BorrowRecord> getAllBorrowRecords();

    // 按图书ID查询借阅记录
    List<BorrowRecord> getBorrowRecordsByBookId(Long bookId);

    // 管理员催还图书
    boolean remindReturn(Long recordId);
}