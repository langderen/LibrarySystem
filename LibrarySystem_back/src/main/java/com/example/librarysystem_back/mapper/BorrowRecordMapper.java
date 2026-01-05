package com.example.librarysystem_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.librarysystem_back.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
    // 查询用户的借阅记录
    @Select("SELECT br.*, b.title as bookTitle, b.author as bookAuthor ,b.isbn " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.id " +
            "WHERE br.user_id = #{userId}")
    List<BorrowRecord> selectByUserId(Long userId);

    // 更新归还时间和状态
    @Update("UPDATE borrow_records SET return_time = #{returnTime}, status = 1 WHERE id = #{id}")
    int updateReturnInfo(BorrowRecord record);

    // 管理员查询所有借阅记录
    @Select("SELECT br.*, b.title as bookTitle, b.author as bookAuthor ,b.isbn " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.id")
    List<BorrowRecord> selectAllBorrowRecords();

    // 按图书ID查询借阅记录
    @Select("SELECT br.*, b.title as bookTitle, b.author as bookAuthor ,b.isbn, u.username as userName " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.id " +
            "LEFT JOIN users u ON br.user_id = u.id " +
            "WHERE br.book_id = #{bookId}")
    List<BorrowRecord> selectByBookId(Long bookId);
}