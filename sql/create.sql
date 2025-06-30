CREATE DATABASE ECHO_WORLD CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ECHO_WORLD;

CREATE TABLE directories (
                             dir_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             parent_id BIGINT,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                             UNIQUE KEY unique_path (name, parent_id),
                             FOREIGN KEY (parent_id) REFERENCES directories(dir_id) ON DELETE CASCADE
);

CREATE TABLE echo_logs (
                           log_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
                           dir_id BIGINT NOT NULL,
                           message TEXT,
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (dir_id) REFERENCES directories(dir_id) ON DELETE CASCADE,
                           INDEX idx_dir_time (dir_id, created_at)
);

USE ECHO_WORLD;
CREATE TABLE `rss_items` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `title` varchar(500) NOT NULL,
                             `link` varchar(512) NOT NULL,
                             `description` varchar(2000) DEFAULT NULL COMMENT 'description',
                             `pub_date` datetime DEFAULT NULL,
                             `source` varchar(100) DEFAULT NULL,
                             `collected` tinyint(1) DEFAULT '0',
                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `link` (`link`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

USE ECHO_WORLD;
CREATE TABLE `articles` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `rss_item_id` bigint DEFAULT NULL,
                            `url` varchar(512) NOT NULL,
                            `og_title` varchar(500) DEFAULT NULL,
                            `og_description` text,
                            `og_image` varchar(1000) DEFAULT NULL,
                            `content` mediumtext,
                            `posted` tinyint(1) DEFAULT '0',
                            `posted_ts` datetime DEFAULT NULL,
                            `parse_failed` tinyint(1) DEFAULT '0',
                            `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            KEY `rss_item_id` (`rss_item_id`),
                            CONSTRAINT `articles_ibfk_1` FOREIGN KEY (`rss_item_id`) REFERENCES `rss_items` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
