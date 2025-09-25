-- Production Environment Database Schema
-- AningCall Database Schema (Production)

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS aningcall_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE aningcall_prod;

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

-- Performance optimization indexes for production
CREATE INDEX idx_users_points ON users(points);
CREATE INDEX idx_call_logs_user_result ON call_logs(user_id, result);
CREATE INDEX idx_call_logs_date_range ON call_logs(user_id, call_start, call_end);
CREATE INDEX idx_points_history_user_type_date ON points_history(user_id, type, created_at);
CREATE INDEX idx_mission_results_type_success ON mission_results(mission_type, success);