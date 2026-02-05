-- PMT Database Schema

CREATE TABLE IF NOT EXISTS user_tl (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    contact_number VARCHAR(50),
    created_at DATE,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS project_tl (
    project_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_name VARCHAR(255) NOT NULL UNIQUE,
    project_description TEXT,
    project_start_date DATE,
    project_status VARCHAR(50),
    project_status_updated_date DATE,
    task_created_by BIGINT
);

CREATE TABLE IF NOT EXISTS project_member_tl (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50),
    joined_at DATE,
    UNIQUE KEY unique_member (user_id, project_id)
);

CREATE TABLE IF NOT EXISTS task_tl (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(255) NOT NULL,
    task_description TEXT,
    task_status VARCHAR(50),
    task_priority VARCHAR(50),
    due_date DATE,
    project_id_fk BIGINT,
    assigned_to BIGINT,
    created_by BIGINT,
    created_at DATE,
    updated_at DATE
);

CREATE TABLE IF NOT EXISTS task_history_tl (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    field_changed VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    changed_by BIGINT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);