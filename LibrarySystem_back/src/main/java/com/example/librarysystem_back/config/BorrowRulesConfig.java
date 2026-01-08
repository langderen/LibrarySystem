package com.example.librarysystem_back.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "borrow.rules")
public class BorrowRulesConfig {
    private int maxBorrowCount = 5;
    private int borrowDays = 30;
}
