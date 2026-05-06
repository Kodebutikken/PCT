CREATE DATABASE IF NOT EXISTS pct;
USE pct;

DROP TABLE IF EXISTS project;

CREATE TABLE project (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    deadline DATE,
    created_by INTEGER NOT NULL,
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE CASCADE
)
CREATE TABLE IF NOT EXISTS subproject (
    id          INTEGER         AUTO_INCREMENT PRIMARY KEY,
    titel       VARCHAR(255)    NOT NULL,
    beskrivelse TEXT,
    deadline    DATE,
    project_id  INTEGER         NOT NULL,
    created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
    );