package com.example.librarysystem_back.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("borrow_records")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId; // 借阅用户ID
    private Long bookId; // 图书ID
    private LocalDateTime borrowTime; // 借阅时间
    private LocalDateTime returnTime; // 归还时间（null表示未归还）
    private Integer status; // 状态：0-未归还，1-已归还
    @TableField(exist = false)
    private String bookTitle;

    @TableField(exist = false)
    private String bookAuthor;
    @TableField(exist = false)
    private String isbn;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private Boolean overdue; // 是否超期
    @TableField(exist = false)
    private Integer overdueDays; // 超期天数
}
