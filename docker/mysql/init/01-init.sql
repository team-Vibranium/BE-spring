-- Development Environment Database Initialization
-- AningCall Database Schema

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS aningcall_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE aningcall_dev;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    points INT DEFAULT 0,
    selected_avatar VARCHAR(50) DEFAULT 'avatar_1',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_nickname (nickname)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Call logs table
CREATE TABLE IF NOT EXISTS call_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    call_start TIMESTAMP,
    call_end TIMESTAMP,
    result ENUM('SUCCESS','FAIL_NO_TALK','FAIL_SNOOZE') NOT NULL,
    snooze_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_result (result),
    INDEX idx_call_start (call_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Mission results table
CREATE TABLE IF NOT EXISTS mission_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    call_log_id BIGINT NOT NULL,
    mission_type ENUM('PUZZLE','MATH','MEMORY','QUIZ') NOT NULL,
    success BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (call_log_id) REFERENCES call_logs(id) ON DELETE CASCADE,
    INDEX idx_call_log_id (call_log_id),
    INDEX idx_mission_type (mission_type),
    INDEX idx_success (success)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Points history table
CREATE TABLE IF NOT EXISTS points_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('GRADE','CONSUMPTION') NOT NULL,
    amount INT NOT NULL,
    description VARCHAR(200),
    related_alarm_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data for development
INSERT IGNORE INTO users (id, email, password_hash, nickname, points) VALUES
(1, 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFigkfZNpwp.NzC5t6fBo8e', '테스트유저', 100),
(2, 'dev@aningcall.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFigkfZNpwp.NzC5t6fBo8e', '개발자', 500);

INSERT IGNORE INTO call_logs (user_id, call_start, call_end, result, snooze_count) VALUES
(1, '2024-01-15 07:30:00', '2024-01-15 07:32:30', 'SUCCESS', 0),
(1, '2024-01-14 07:30:00', NULL, 'FAIL_NO_TALK', 2),
(2, '2024-01-15 08:00:00', '2024-01-15 08:01:15', 'SUCCESS', 1);

INSERT IGNORE INTO mission_results (call_log_id, mission_type, success) VALUES
(1, 'PUZZLE', true),
(3, 'PUZZLE', true);

INSERT IGNORE INTO points_history (user_id, type, amount, description, related_alarm_id) VALUES
(1, 'GRADE', 10, '알람 성공 보너스', 'alarm_123'),
(1, 'CONSUMPTION', -50, '아바타 구매', NULL),
(2, 'GRADE', 10, '알람 성공 보너스', 'alarm_124');