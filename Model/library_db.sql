CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- 1. 图书表 (核心：available_stock 用于并发控制)
CREATE TABLE `books` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `total_stock` int NOT NULL DEFAULT '0',
  `available_stock` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);

-- 2. 借阅记录表
CREATE TABLE `borrow_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `borrow_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `return_time` datetime DEFAULT NULL,
  `status` tinyint DEFAULT '0' COMMENT '0:借阅中, 1:已归还',
  PRIMARY KEY (`id`)
);

-- 初始化测试数据
INSERT INTO `books` (`title`, `total_stock`, `available_stock`) VALUES 
('深入理解计算机系统', 10, 10),
('高并发实战', 1, 1); -- 这本书只有1本，用于测试并发