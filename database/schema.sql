-- CodeMentor database schema
-- Run: mysql -uroot -p < database/schema.sql
-- (Server start par db.js ye automatically bhi create kar deta hai)

CREATE DATABASE IF NOT EXISTS codementor
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE codementor;

-- Users table (chat_logs ka FK parent)
CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    streak_count INT DEFAULT 0,
    target_topic VARCHAR(100),
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Chat exchange: user ka message + AI ka response ek row mein
CREATE TABLE IF NOT EXISTS chat_logs (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    response TEXT,
    sender_type VARCHAR(20) DEFAULT 'AI', -- Values: 'AI', 'HUMAN', 'STUDENT'
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY user_id (user_id),
    CONSTRAINT chat_logs_ibfk_1 FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
