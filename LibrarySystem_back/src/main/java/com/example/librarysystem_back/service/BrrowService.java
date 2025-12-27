package com.example.librarysystem_back.service;


import com.example.librarysystem_back.mapper.BookMapper;
import com.example.librarysystem_back.entity.BrrowRecord;
import com.example.librarysystem_back.mapper.BrrowRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrrowService {
    @Autowired private BookMapper bookMapper;
    @Autowired private BrrowRecordMapper recordMapper;

    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(Long userId, Long bookId) {
        // 1. 尝试扣减库存 (利用数据库原子锁)
        int updateCount = bookMapper.decreaseStock(bookId);

        if (updateCount == 0) {
            throw new RuntimeException("借阅失败：库存不足！");
        }

        // 2. 只有扣减成功，才写入借阅记录
        BrrowRecord record = new BrrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setStatus(0);
        recordMapper.insert(record);
    }
}
