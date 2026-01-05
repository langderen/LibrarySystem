package com.example.librarysystem_back.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.librarysystem_back.entity.BorrowRecord;
import com.example.librarysystem_back.mapper.BookMapper;
import com.example.librarysystem_back.mapper.BorrowRecordMapper;
import com.example.librarysystem_back.service.BorrowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowService {

    private final BookMapper bookMapper;

    public BorrowServiceImpl(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Transactional // 事务保证库存和记录一致性
    @Override
    public boolean borrowBook(Long userId, Long bookId) {
        // 1. 减少图书库存
        int rows = bookMapper.decreaseStock(bookId);
        if (rows <= 0) {
            return false; // 库存不足
        }
        // 2. 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowTime(LocalDateTime.now());
        record.setStatus(0); // 未归还
        return save(record);
    }

    @Transactional
    @Override
    public boolean returnBook(Long recordId) {
        BorrowRecord record = getById(recordId);
        if (record == null || record.getStatus() == 1) {
            return false; // 记录不存在或已归还
        }
        // 1. 更新图书库存
        bookMapper.increaseStock(record.getBookId());
        // 2. 更新借阅记录
        record.setReturnTime(LocalDateTime.now());
        record.setStatus(1); // 已归还
        return baseMapper.updateReturnInfo(record) > 0;
    }

    @Override
    public List<BorrowRecord> getUserBorrowRecords(Long userId) {

        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<BorrowRecord> getAllBorrowRecords() {
        return baseMapper.selectAllBorrowRecords();
    }

    @Override
    public List<BorrowRecord> getBorrowRecordsByBookId(Long bookId) {
        return baseMapper.selectByBookId(bookId);
    }

    @Override
    public boolean remindReturn(Long recordId) {
        BorrowRecord record = getById(recordId);
        if (record == null || record.getStatus() == 1) {
            return false;
        }
        System.out.println("催还通知：用户 " + record.getUserId() + " 借阅的图书 " + record.getBookId() + " 请及时归还");
        return true;
    }
}