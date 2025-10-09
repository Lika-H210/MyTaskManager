CREATE TABLE user_accounts (
  id INT NOT NULL AUTO_INCREMENT,
  public_id CHAR(36) NOT NULL,
  user_name VARCHAR(50) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT(1) DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY unique_email (email),
  UNIQUE KEY unique_public_id (public_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE projects (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  public_id CHAR(36) NOT NULL,
  project_caption VARCHAR(100) NOT NULL,
  description TEXT,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT(1) DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY unique_public_id (public_id),
  KEY idx_user_id (user_id),
  CONSTRAINT fk_projects_user FOREIGN KEY (user_id) REFERENCES user_accounts (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE tasks (
  id INT NOT NULL AUTO_INCREMENT,
  project_id INT NOT NULL,
  public_id CHAR(36) NOT NULL,
  parent_task_id INT DEFAULT NULL,
  task_caption VARCHAR(100) NOT NULL,
  description TEXT,
  due_date DATE NOT NULL,
  estimated_time INT NOT NULL,
  actual_time INT NOT NULL DEFAULT 0,
  progress INT NOT NULL DEFAULT 0,
  priority VARCHAR(20) DEFAULT 'LOW',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT(1) DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY unique_public_id (public_id),
  KEY idx_project_id (project_id),
  KEY idx_parent_task_id (parent_task_id),
  CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects (id),
  CONSTRAINT fk_tasks_parent FOREIGN KEY (parent_task_id) REFERENCES tasks (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;