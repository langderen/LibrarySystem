package com.example.librarysystem_back.service;

import com.example.librarysystem_back.entity.BorrowRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowResult {
    private boolean success;
    private String message;
    private BorrowRecord record;

    public static BorrowResult success(String message, BorrowRecord record) {
        return new BorrowResult(true, message, record);
    }

    public static BorrowResult failure(String message) {
        BorrowResult result = new BorrowResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setRecord(null);
        return result;

    }
}
