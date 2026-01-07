package com.example.librarysystem_back.service;

import com.example.librarysystem_back.config.OllamaConfig;
import com.example.librarysystem_back.entity.Book;
import com.example.librarysystem_back.mapper.BookMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private OllamaConfig ollamaConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, List<String>> KEYWORD_CATEGORIES = new HashMap<>();

    static {
        KEYWORD_CATEGORIES.put("计算机", Arrays.asList("编程", "程序", "代码", "软件开发", "计算机", "程序员", "java", "python", "前端", "后端", "数据库", "算法", "人工智能", "AI", "机器学习", "深度学习", "web", "互联网", "技术"));
        KEYWORD_CATEGORIES.put("文学", Arrays.asList("小说", "文学", "散文", "诗歌", "名著", "经典", "故事", "文学创作", "外国文学", "中国文学"));
        KEYWORD_CATEGORIES.put("历史", Arrays.asList("历史", "古代", "近代", "世界史", "中国史", "战争", "朝代", "史记", "历史事件", "文明"));
        KEYWORD_CATEGORIES.put("科学", Arrays.asList("科学", "物理", "化学", "生物", "天文", "地理", "宇宙", "科普", "自然", "实验", "数学"));
        KEYWORD_CATEGORIES.put("艺术", Arrays.asList("艺术", "绘画", "音乐", "摄影", "设计", "美术", "雕塑", "建筑", "艺术史", "书法"));
    }

    public Map<String, Object> recommendBooks(String question) {
        Map<String, Object> result = new HashMap<>();
        logger.info("收到AI推荐请求: {}", question);

        try {
            String aiAnalysis = callOllama(question);
            logger.info("AI分析结果: {}", aiAnalysis);

            String analyzedCategory = analyzeAIResponse(aiAnalysis, question);
            logger.info("分析出的分类: {}", analyzedCategory);

            List<Book> books;
            String explanation;

            if (analyzedCategory != null && !analyzedCategory.isEmpty()) {
                books = bookMapper.selectByCategory(analyzedCategory);
                logger.info("数据库查询到 {} 本分类为 {} 的书", books.size(), analyzedCategory);
                explanation = buildExplanation(question, analyzedCategory, books.size(), aiAnalysis);
            } else {
                books = bookMapper.selectAll();
                logger.info("未识别分类，返回全部 {} 本书", books.size());
                explanation = "根据您的问题，我没有找到明确的分类偏好，为您推荐一些热门图书：\n\n" + aiAnalysis;
            }

            result.put("recommendedBooks", books);
            result.put("explanation", explanation);

        } catch (Exception e) {
            logger.error("AI服务调用失败", e);
            result.put("error", "AI服务调用失败: " + e.getMessage());
            result.put("recommendedBooks", Collections.emptyList());
            result.put("explanation", "抱歉，AI服务暂时不可用。请检查Ollama是否运行。错误详情: " + e.getClass().getSimpleName());
        }

        return result;
    }

    private String callOllama(String question) throws Exception {
        // 硬编码 Ollama 地址，避免配置读取问题
        String host = "http://localhost:11434";
        String model = "qwen3:8b";

        String url = host + "/api/generate";
        logger.info("调用Ollama: {} 模型: {}", url, model);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("prompt", "用户问题是: \"" + question + "\"\n\n请分析这个问题，用户想要找什么类型的书？\n只回答分类名称，如：计算机、文学、历史、科学、艺术。如果不确定，回答\"未知\"。");
        requestBody.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        logger.info("请求体: {}", jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        String response = restTemplate.postForObject(url, entity, String.class);
        logger.info("Ollama响应: {}", response);

        JsonNode root = objectMapper.readTree(response);
        return root.has("response") ? root.get("response").asText().trim() : "";
    }

    private String analyzeAIResponse(String aiResponse, String originalQuestion) {
        String lowerResponse = aiResponse.toLowerCase();
        String lowerQuestion = originalQuestion.toLowerCase();

        for (Map.Entry<String, List<String>> entry : KEYWORD_CATEGORIES.entrySet()) {
            String category = entry.getKey();
            if (lowerResponse.contains(category.toLowerCase())) {
                return category;
            }

            for (String keyword : entry.getValue()) {
                if (lowerQuestion.contains(keyword.toLowerCase())) {
                    return category;
                }
            }
        }

        return null;
    }

    private String buildExplanation(String question, String category, int count, String aiAnalysis) {
        StringBuilder sb = new StringBuilder();
        sb.append("根据您的问题「").append(question).append("」\n");
        sb.append("我为您找到了 ").append(count).append(" 本与「").append(category).append("」相关的图书：\n\n");

        if (aiAnalysis != null && !aiAnalysis.isEmpty()) {
            sb.append("AI分析: ").append(aiAnalysis).append("\n\n");
        }

        sb.append("这些图书涵盖了 ").append(category).append(" 领域的多个方面，");
        if (count >= 5) {
            sb.append("希望您能找到感兴趣的书籍！");
        } else if (count > 0) {
            sb.append("虽然数量不多，但都是相关的推荐。");
        } else {
            sb.append("很抱歉，符合您要求的图书暂时没有入库。");
        }
        return sb.toString();
    }
}
