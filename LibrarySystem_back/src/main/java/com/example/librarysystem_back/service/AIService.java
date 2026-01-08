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
    private static final Set<String> TOPIC_KEYWORDS = new HashSet<>();

    static {
        KEYWORD_CATEGORIES.put("计算机", Arrays.asList("编程", "程序", "代码", "软件开发", "计算机", "程序员", "java", "python", "前端", "后端", "数据库", "算法", "人工智能", "AI", "机器学习", "深度学习", "web", "互联网", "技术", "交换机", "路由器", "网络"));
        KEYWORD_CATEGORIES.put("文学", Arrays.asList("小说", "文学", "散文", "诗歌", "名著", "经典", "故事", "文学创作", "外国文学", "中国文学"));
        KEYWORD_CATEGORIES.put("历史", Arrays.asList("历史", "古代", "近代", "世界史", "中国史", "战争", "朝代", "史记", "历史事件", "文明"));
        KEYWORD_CATEGORIES.put("科学", Arrays.asList("科学", "物理", "化学", "生物", "天文", "地理", "宇宙", "科普", "自然", "实验", "数学"));
        KEYWORD_CATEGORIES.put("艺术", Arrays.asList("艺术", "绘画", "音乐", "摄影", "设计", "美术", "雕塑", "建筑", "艺术史", "书法"));

        TOPIC_KEYWORDS.addAll(KEYWORD_CATEGORIES.get("计算机"));
        TOPIC_KEYWORDS.addAll(KEYWORD_CATEGORIES.get("文学"));
        TOPIC_KEYWORDS.addAll(KEYWORD_CATEGORIES.get("历史"));
        TOPIC_KEYWORDS.addAll(KEYWORD_CATEGORIES.get("科学"));
        TOPIC_KEYWORDS.addAll(KEYWORD_CATEGORIES.get("艺术"));
        TOPIC_KEYWORDS.addAll(Arrays.asList(
            "投资", "理财", "经济", "管理", "营销", "商业", "创业",
            "心理学", "励志", "成长", "效率", "时间管理", "习惯",
            "美食", "烹饪", "旅行", "户外", "运动", "健身", "健康",
            "育儿", "教育", "考试", "学习", "方法论",
            "哲学", "思考", "逻辑", "批判性思维",
            "悬疑", "推理", "言情", "武侠", "玄幻", "科幻"
        ));
    }

    public Map<String, Object> recommendBooks(String question) {
        Map<String, Object> result = new HashMap<>();

        try {
            String aiAnalysis = callOllama(question);

            Map<String, Object> searchParams = analyzeAIResponseForSearch(aiAnalysis, question);
            String category = (String) searchParams.get("category");
            String author = (String) searchParams.get("author");
            String titleKeyword = (String) searchParams.get("title");

            logger.info("AI提取的搜索条件 - 分类: {}, 作者: {}, 书名关键词: {}", category, author, titleKeyword);

            List<Book> books = searchBooksMultiCondition(category, author, titleKeyword);
            logger.info("数据库查询到 {} 本符合条件的书", books.size());

            String explanation = buildExplanation(question, category, author, titleKeyword, books.size(), aiAnalysis);

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

    private List<Book> searchBooksMultiCondition(String category, String author, String titleKeyword) {
        try {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Book> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 20);

            return bookMapper.searchBooks(page, titleKeyword, author, null, category, null).getRecords();
        } catch (Exception e) {
            logger.error("多条件搜索失败", e);
            return bookMapper.selectAll();
        }
    }

    private Map<String, Object> analyzeAIResponseForSearch(String aiResponse, String originalQuestion) {
        Map<String, Object> result = new HashMap<>();
        String category = null;
        String author = null;
        String titleKeyword = null;

        String lowerResponse = aiResponse.toLowerCase();
        String lowerQuestion = originalQuestion.toLowerCase();

        try {
            int jsonStart = aiResponse.indexOf('{');
            int jsonEnd = aiResponse.lastIndexOf('}');
            if (jsonStart >= 0 && jsonEnd >= jsonStart) {
                String jsonStr = aiResponse.substring(jsonStart, jsonEnd + 1);
                JsonNode jsonNode = objectMapper.readTree(jsonStr);
                
                if (jsonNode.has("category") && !jsonNode.get("category").isNull()) {
                    category = jsonNode.get("category").asText();
                }
                if (jsonNode.has("author") && !jsonNode.get("author").isNull()) {
                    author = jsonNode.get("author").asText();
                }
                if (jsonNode.has("titleKeyword") && !jsonNode.get("titleKeyword").isNull()) {
                    titleKeyword = cleanTopicSuffix(jsonNode.get("titleKeyword").asText());
                }

            }
        } catch (Exception e) {
            logger.warn("解析AI返回的JSON失败，使用关键词匹配");
        }

        for (Map.Entry<String, List<String>> entry : KEYWORD_CATEGORIES.entrySet()) {
            String cat = entry.getKey();
            if (lowerResponse.contains(cat.toLowerCase())) {
                if (category == null) category = cat;
                break;
            }
            for (String keyword : entry.getValue()) {
                if (lowerQuestion.contains(keyword.toLowerCase())) {
                    if (category == null) category = cat;
                    break;
                }
            }
            if (category != null) break;
        }

        if (author == null) {
            String[] authorPatterns = {"作者", "作家", "谁写的", "作者是", "写的", "写的书"};
            for (String pattern : authorPatterns) {
                if (lowerQuestion.contains(pattern.toLowerCase())) {
                    int idx = lowerQuestion.indexOf(pattern.toLowerCase());
                    if (idx >= 0) {
                        String after = originalQuestion.substring(idx + pattern.length()).trim();
                        String[] words = after.split("[,，\\s。]");
                        if (words.length > 0 && !words[0].isEmpty()) {
                            author = words[0].replaceAll("^[的是为]", "").trim();
                            if (author.length() > 20) author = author.substring(0, 20);
                            break;
                        }
                    }
                }
            }
        }

        if (author == null) {
            String[] authorNames = {"鲁迅", "余华", "莫言", "金庸", "村上春树", "东野圭吾", "刘慈欣", "JK罗琳", "托尔金", "海明威", "雨果"};
            for (String name : authorNames) {
                if (lowerQuestion.contains(name.toLowerCase())) {
                    author = name;
                    break;
                }
            }
        }

        if (titleKeyword == null) {
            String[] titlePatterns = {"书名", "关于", "想看", "找一本", "有什么", "推荐", "有关"};
            for (String pattern : titlePatterns) {
                if (lowerQuestion.contains(pattern.toLowerCase())) {
                    int idx = lowerQuestion.indexOf(pattern.toLowerCase());
                    if (idx >= 0 && idx + pattern.length() < lowerQuestion.length()) {
                        String after = originalQuestion.substring(idx + pattern.length()).trim();
                        String[] words = after.split("[,，\\s。;；!！?？]");
                        if (words.length > 0 && !words[0].isEmpty()) {
                            String kw = words[0].replaceAll("^[的之有关于我想找要]", "").trim();
                            if (kw.length() > 2 && kw.length() < 30) {
                                titleKeyword = kw;
                                break;
                            }
                        }
                    }
                }
            }
        }
        

        result.put("category", category);
        result.put("author", author);
        result.put("title", titleKeyword);
        return result;
    }

    private String extractGeneralTopic(String lowerQuestion) {
        String[] techPatterns = {
            "学习(.+)",
            "想学(.+)",
            "关于(.+)",
            "有关(.+)",
            "(.+)相关的书",
            "跟(.+)有关的书",
            "有关(.+)的书",
            "关于(.+)的书"
        };

        for (String pattern : techPatterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(lowerQuestion);
            if (m.find()) {
                String topic = m.group(1).trim();
                if (topic.length() >= 2 && topic.length() < 20) {
                    topic = cleanTopicSuffix(topic);
                    if (topic != null && topic.length() >= 2 && !isGenericWord(topic)) {
                        logger.info("从模式 '{}' 提取到主题: {}", pattern, topic);
                        return topic;
                    }
                }
            }
        }

        for (String keyword : TOPIC_KEYWORDS) {
            if (lowerQuestion.contains(keyword.toLowerCase())) {
                return keyword;
            }
        }

        if (lowerQuestion.endsWith("的书") || lowerQuestion.endsWith("书籍")) {
            String[] parts = lowerQuestion.split("\\s+");
            for (int i = parts.length - 1; i >= 0; i--) {
                String part = parts[i].trim();
                if (part.equals("的书") || part.equals("书籍")) continue;
                if (part.length() >= 2 && part.length() <= 15) {
                    part = cleanTopicSuffix(part);
                    if (part != null && part.length() >= 2 && !isGenericWord(part)) {
                        return part;
                    }
                }
            }
        }

        return null;
    }

    private boolean isGenericWord(String word) {
        String[] genericWords = {"学习", "知识", "内容", "书", "书籍", "教程", "指南", "手册", "入门", "进阶", "精通", "详解", "大全", "概论", "导论", "自学", "教程书", "学习资料", "读书笔记", "阅读", "精读", "泛读", "研读", "学习指南", "推荐", "书单", "必看", "值得读", "电子版", "纸质版", "购买", "借阅", "查找", "检索"};
        for (String generic : genericWords) {
            if (word.equalsIgnoreCase(generic)) {
                return true;
            }
        }
        return false;
    }

    private String cleanTopicSuffix(String topic) {
        String[] suffixes = {"的知识", "方面的", "相关的", "有关的", "的书", "的内容", "的要点", "的核心", "的重点", "的细节", "的讲解", "的分析", "的解读", "的原理", "内容", "知识", "学习", "教程", "指南", "手册", "入门", "进阶", "精通", "详解", "大全", "概论", "导论", "自学", "教程书", "学习资料", "读书笔记", "阅读", "精读", "泛读", "研读", "学习指南", "推荐", "书单", "必看", "值得读", "电子版", "纸质版", "购买", "借阅", "查找", "检索", "的"};
        String[] prefixes = {"有关", "关于", "我想", "想", "学习"};

        boolean changed = true;
        while (changed && topic != null && topic.length() > 1) {
            changed = false;
            for (String suffix : suffixes) {
                if (topic.endsWith(suffix)) {
                    topic = topic.substring(0, topic.length() - suffix.length());
                    changed = true;
                    break;
                }
            }
        }

        for (String prefix : prefixes) {
            if (topic != null && topic.startsWith(prefix)) {
                topic = topic.substring(prefix.length());
                break;
            }
        }

        return topic != null ? topic.trim() : null;
    }

    private String callOllama(String question) throws Exception {
        String host = "http://localhost:11434";
        String model = "qwen3:8b";

        String url = host + "/api/generate";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("prompt", "用户问题是: \"" + question + "\"\n\n请分析这个问题，提取图书搜索的关键信息。\n\n提取规则：\n1. titleKeyword（书名关键词）：**必须提取核心主题词**\n   - 技术名称：java、python、docker、人工智能、机器学习、计算机网络\n   - 主题词：投资理财、健身、心理学、育儿、旅行\n   - **不要包含**：的知识、的内容、方面的、相关的、有关的、学习、书、书籍等后缀\n   - 例如：\"学习java\" → \"java\"，\"关于网络的知识\" → \"网络\"，\"投资理财方面的书\" → \"投资理财\"，\"想学习docker\" → \"docker\"\n\n2. author（作者）：提取作者姓名\n   - 例如：\"鲁迅的书\" → \"鲁迅\"，\"刘慈欣的作品\" → \"刘慈欣\"\n\n3. category（分类）：判断图书分类\n   - 计算机：编程、软件、算法、人工智能、网络、数据库、docker等\n   - 文学：小说、诗歌、散文、名著等\n   - 历史：历史、古代、战争、朝代等\n   - 科技：物理、化学、生物、天文、地理等\n   - 艺术：绘画、音乐、设计、摄影等\n   - 其他：投资理财、心理学、育儿、旅行等\n\n**重要**：如果用户提到具体的技术名称或主题词（如docker、java、python等），必须将其提取为titleKeyword。\n\n请用JSON格式回答：\n{\n  \"category\": \"分类名称，如果没有明确分类则为null\",\n  \"author\": \"作者名称，如果没有提到则为null\",\n  \"titleKeyword\": \"核心主题词（简洁，无后缀），如果提到技术名称或主题词必须提取，否则为null\",\n  \"reason\": \"简要说明你的分析\"\n}\n\n只回答JSON，不要有其他内容。");
        requestBody.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        logger.info("请求体: {}", jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        String response = restTemplate.postForObject(url, entity, String.class);
        logger.info("Ollama响应: {}", response);

        try {
            JsonNode root = objectMapper.readTree(response);
            String rawResponse = root.has("response") ? root.get("response").asText() : "";

            // 修复第1点：直接返回 AI 生成的原始 JSON 内容（rawResponse），
            // 不要在这里解析并提取 reason，把解析工作留给 analyzeAIResponseForSearch
            return rawResponse;
        } catch (Exception e) {
            return response.trim();
        }
    }

    private String buildExplanation(String question, String category, String author, String titleKeyword, int count, String aiAnalysis) {
        StringBuilder sb = new StringBuilder();
        sb.append("根据您的问题「").append(question).append("」\n");

        List<String> conditions = new ArrayList<>();
        if (category != null) conditions.add("分类：「" + category + "」");
        if (author != null) conditions.add("作者：「" + author + "」");
        if (titleKeyword != null) conditions.add("关键词：「" + titleKeyword + "」");

        if (!conditions.isEmpty()) {
            sb.append("我按照");
            for (int i = 0; i < conditions.size(); i++) {
                if (i > 0) sb.append("、");
                sb.append(conditions.get(i));
            }
            sb.append(" 条件为您搜索，");
        }
        sb.append("找到了 ").append(count).append(" 本相关的图书：\n\n");

        if (aiAnalysis != null && !aiAnalysis.isEmpty()) {
            try {
                int jsonStart = aiAnalysis.indexOf('{');
                int jsonEnd = aiAnalysis.lastIndexOf('}');
                if (jsonStart >= 0 && jsonEnd >= jsonStart) {
                    String jsonStr = aiAnalysis.substring(jsonStart, jsonEnd + 1);
                    JsonNode jsonNode = objectMapper.readTree(jsonStr);
                    if (jsonNode.has("reason")) {
                        sb.append("AI分析: ").append(jsonNode.get("reason").asText()).append("\n\n");
                    }
                }
            } catch (Exception e) {
                sb.append("AI分析: ").append(aiAnalysis).append("\n\n");
            }
        }

        sb.append("这些图书涵盖了您关注的领域，");
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
