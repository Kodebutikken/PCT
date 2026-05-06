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


DROP TABLE IF EXISTS task;

CREATE TABLE task (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    estimated_hours DECIMAL(6,2) NOT NULL,
    deadline DATE,
    subproject_id INTEGER NOT NULL,
    resource_type_id INTEGER,
    resource_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (subproject_id) REFERENCES subproject(id) ON DELETE CASCADE,
    FOREIGN KEY (resource_type_id) REFERENCES resource_type(id) ON DELETE SET NULL,
    FOREIGN KEY (resource_id) REFERENCES resource(id) ON DELETE SET NULL
);