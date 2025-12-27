package com.example.librarysystem_back.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("")
public class BrrowRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private Long userId;
    private Date borrowTime;
    private Date returnTime;
    private Integer status;
}
