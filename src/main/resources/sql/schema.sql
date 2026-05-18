CREATE DATABASE IF NOT EXISTS pct;
USE pct;

CREATE TABLE user
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(50)  NOT NULL
);

DROP TABLE IF EXISTS project;

CREATE TABLE project
(
    id          INTEGER AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    deadline    DATE,
    created_by  INTEGER      NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES user (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS project_user
(
    project_id INTEGER NOT NULL,
    user_id    INTEGER NOT NULL,
    access_level VARCHAR(50) NOT NULL,
    PRIMARY KEY (project_id, user_id),
    FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subproject
(
    id          INTEGER AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    deadline    DATE,
    project_id  INTEGER      NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS task;

CREATE TABLE task
(
    id               INTEGER AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(255)  NOT NULL,
    description      TEXT,
    estimated_hours  DECIMAL(6, 2) NOT NULL,
    deadline         DATE,
    subproject_id    INTEGER       NOT NULL,
    resource_type_id INTEGER,
    resource_id      INTEGER,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (subproject_id) REFERENCES subproject (id) ON DELETE CASCADE
    # HVAD ER resource?
    #FOREIGN KEY (resource_type_id) REFERENCES resource_type (id) ON DELETE SET NULL,
    #FOREIGN KEY (resource_id) REFERENCES resource (id) ON DELETE SET NULL
);

