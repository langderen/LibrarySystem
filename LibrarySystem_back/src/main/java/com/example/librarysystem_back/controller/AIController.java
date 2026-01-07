package com.example.librarysystem_back.controller;

import com.example.librarysystem_back.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> recommend(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        Map<String, Object> result = aiService.recommendBooks(question);
        return ResponseEntity.ok(result);
    }
}
