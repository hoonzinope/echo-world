CREATE TABLE directories
(
    dir_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    parent_id  BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_path UNIQUE (name, parent_id),
    FOREIGN KEY (parent_id) REFERENCES directories (dir_id) ON DELETE CASCADE
);

CREATE TABLE echo_logs
(
    log_seq    BIGINT AUTO_INCREMENT PRIMARY KEY,
    dir_id     BIGINT NOT NULL,
    message    CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dir_id) REFERENCES directories (dir_id) ON DELETE CASCADE
);
CREATE INDEX idx_dir_time ON echo_logs (dir_id, created_at);