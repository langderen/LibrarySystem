package com.example.librarysystem_back.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.librarysystem_back.config.BorrowRulesConfig;
import com.example.librarysystem_back.entity.BorrowRecord;
import com.example.librarysystem_back.mapper.BookMapper;
import com.example.librarysystem_back.mapper.BorrowRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowService {

    private final BookMapper bookMapper;
    private final BorrowRulesConfig borrowRulesConfig;

    public BorrowServiceImpl(BookMapper bookMapper, BorrowRulesConfig borrowRulesConfig) {
        this.bookMapper = bookMapper;
        this.borrowRulesConfig = borrowRulesConfig;
    }

    @Transactional // 事务保证库存和记录一致性
    @Override
    public boolean borrowBook(Long userId, Long bookId) {
        BorrowResult result = borrowBookWithResult(userId, bookId);
        return result.isSuccess();
    }

    @Transactional // 事务保证库存和记录一致性
    @Override
    public BorrowResult borrowBookWithResult(Long userId, Long bookId) {
        int maxBorrowCount = borrowRulesConfig.getMaxBorrowCount();

        int currentBorrowCount = baseMapper.countUnreturnedByUserId(userId);
        if (currentBorrowCount >= maxBorrowCount) {
            return BorrowResult.failure("您已达到最大借阅数量限制（" + maxBorrowCount + "本），请先归还部分图书后再借阅");
        }

        int rows = bookMapper.decreaseStock(bookId);
        if (rows <= 0) {
            return BorrowResult.failure("库存不足或图书不存在");
        }

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowTime(LocalDateTime.now());
        record.setStatus(0);

        boolean saved = save(record);
        if (saved) {
            return BorrowResult.success("借阅成功",record);
        } else {
            bookMapper.increaseStock(bookId);
            return BorrowResult.failure("借阅失败，请重试");
        }
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
        List<BorrowRecord> records = baseMapper.selectByUserId(userId);
        calculateOverdue(records);
        return records;
    }

    @Override
    public List<BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecord> records = baseMapper.selectAllBorrowRecords();
        calculateOverdue(records);
        return records;
    }

    @Override
    public List<BorrowRecord> getBorrowRecordsByBookId(Long bookId) {
        List<BorrowRecord> records = baseMapper.selectByBookId(bookId);
        calculateOverdue(records);
        return records;
    }

    private void calculateOverdue(List<BorrowRecord> records) {
        int borrowDays = borrowRulesConfig.getBorrowDays();
        LocalDateTime now = LocalDateTime.now();

        for (BorrowRecord record : records) {
            if (record.getStatus() == 0 && record.getBorrowTime() != null) {
                LocalDateTime dueDate = record.getBorrowTime().plusDays(borrowDays);
                if (now.isAfter(dueDate)) {
                    record.setOverdue(true);
                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(dueDate, now);
                    record.setOverdueDays((int) daysBetween);
                } else {
                    record.setOverdue(false);
                    record.setOverdueDays(0);
                }
            } else {
                record.setOverdue(false);
                record.setOverdueDays(0);
            }
        }
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

    @Override
    public List<BorrowRecord> getOverdueRecords(Long userId) {
        List<BorrowRecord> records = baseMapper.selectByUserId(userId);
        calculateOverdue(records);
        return records.stream()
                .filter(record -> record.getOverdue() != null && record.getOverdue())
                .toList();
    }
}