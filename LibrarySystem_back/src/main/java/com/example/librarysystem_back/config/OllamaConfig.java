package com.example.librarysystem_back.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ollama")
public class OllamaConfig {
    private String host = "http://localhost:11434";
    private String model = "qwen3:8b";
}
