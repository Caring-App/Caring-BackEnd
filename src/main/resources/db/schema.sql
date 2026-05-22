USE caringdb;

-- ===========================================================
-- 기존 테이블이 있다면 초기화
-- ===========================================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS system_log;
DROP TABLE IF EXISTS notification_log;
DROP TABLE IF EXISTS location_log;
DROP TABLE IF EXISTS health_record;
DROP TABLE IF EXISTS member_disease;
DROP TABLE IF EXISTS disease;
DROP TABLE IF EXISTS setting;
DROP TABLE IF EXISTS place_info;
DROP TABLE IF EXISTS task_schedule;
DROP TABLE IF EXISTS pill_log;
DROP TABLE IF EXISTS pill_schedule;
DROP TABLE IF EXISTS connection;
DROP TABLE IF EXISTS member;

SET FOREIGN_KEY_CHECKS = 1;

-- ===========================================================
-- Member 테이블
-- ===========================================================
CREATE TABLE member(
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NULL,
    name VARCHAR(50) NOT NULL,
    nickname VARCHAR(50) NULL,
    birth_date DATE NOT NULL,
    address VARCHAR(255) NOT NULL,
    role ENUM('PROTECTOR', 'WARD') NOT NULL,
    provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    provider_id VARCHAR(255) NULL,
    protector_code VARCHAR(10) NULL UNIQUE,
    auth_level ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    fcm_token VARCHAR(255) NULL
);

-- ===========================================================
-- Connection 테이블
-- ===========================================================
CREATE TABLE connection (
    connection_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    protector_id BIGINT NOT NULL,
    ward_id BIGINT NOT NULL UNIQUE,
    linked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (protector_id) REFERENCES member(member_id) ON DELETE CASCADE,
    FOREIGN KEY (ward_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ===========================================================
-- Pill_Schedule 테이블
-- ===========================================================
CREATE TABLE pill_schedule (
    pill_schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ward_id BIGINT NOT NULL,
    pill_name VARCHAR(100) NOT NULL,
    take_days VARCHAR(50) NOT NULL,
    take_time TIME NOT NULL,
    retry_alarm INT NOT NULL DEFAULT 10,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (ward_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ===========================================================
-- Pill_Log 테이블
-- ===========================================================
CREATE TABLE pill_log (
    pill_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pill_schedule_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    is_taken BOOLEAN NOT NULL DEFAULT FALSE,
    confirmed_at DATETIME NULL,
    current_retry_count INT NOT NULL DEFAULT 0,
    FOREIGN KEY (pill_schedule_id) REFERENCES pill_schedule(pill_schedule_id) ON DELETE CASCADE
);

-- ===========================================================
-- Task_Schedule 테이블
-- ===========================================================
CREATE TABLE task_schedule (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ward_id BIGINT NOT NULL,
    task_name VARCHAR(100) NOT NULL,
    location_name VARCHAR(100) NULL,
    task_date DATE NOT NULL,
    task_time TIME NOT NULL,
    tts_voice_time TIME NULL,
    tts_message TEXT NULL,
    FOREIGN KEY (ward_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ===========================================================
-- Place_info 테이블
-- ===========================================================
CREATE TABLE place_info (
    place_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ward_id BIGINT NOT NULL,
    place_name VARCHAR(100) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    FOREIGN KEY (ward_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ===========================================================
-- Disease 테이블
-- ===========================================================
CREATE TABLE disease (
    disease_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    disease_name VARCHAR(50) NOT NULL UNIQUE
);

-- ===========================================================
-- Member_disease 테이블
-- ===========================================================
CREATE TABLE member_disease (
    member_disease_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ward_id BIGINT NOT NULL,
    disease_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ward_id) REFERENCES member(member_id) ON DELETE CASCADE,
    FOREIGN KEY (disease_id) REFERENCES disease(disease_id) ON DELETE CASCADE,
    UNIQUE KEY unique_ward_disease (ward_id, disease_id)
);

-- ===========================================================
-- Health_Record 테이블
-- ===========================================================
CREATE TABLE health_record (
    health_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ward_id BIGINT NOT NULL,
    health_type VARCHAR(50) NOT NULL,
    health_value INT NOT NULL,
    steps INT NOT NULL DEFAULT 0,
    recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ward_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ===========================================================
-- Location_Log 테이블
-- ===========================================================
CREATE TABLE location_log (
    location_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ward_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    stay_duration INT NOT NULL DEFAULT 0,
    is_visit_verified BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (ward_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ===========================================================
-- Notification_Log 테이블
-- ===========================================================
CREATE TABLE notification_log (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiver_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (receiver_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ===========================================================
-- Setting 테이블
-- ===========================================================
CREATE TABLE setting (
    setting_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    is_push_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    is_location_agreed BOOLEAN NOT NULL DEFAULT FALSE,
    font_size INT NOT NULL DEFAULT 1,
    tts_rate DOUBLE NOT NULL DEFAULT 1.0,
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ===========================================================
-- System_Log 테이블
-- ===========================================================
CREATE TABLE system_log (
    system_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    log_type ENUM('LOGOUT', 'WITHDRAW') NOT NULL,
    reason VARCHAR(255) NOT NULL,
    occurred_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ===========================================================
-- 초기 질병 데이터 삽입 (Insert)
-- ===========================================================
INSERT INTO disease (disease_name) VALUES
    ('고혈압'),
    ('당뇨병'),
    ('치매'),
    ('골다공증'),
    ('고지혈증'),
    ('관절염'),
    ('심혈관 질환'),
    ('만성 신부전'),
    ('파킨슨병'),
    ('기타')
ON DUPLICATE KEY UPDATE disease_name=disease_name;
