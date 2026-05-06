package com.kodebutikken.pct.repository;

import com.kodebutikken.pct.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRepository {
    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTask(Task task) {
        String sql = "INSERT INTO task (title, description, estimated_hours, deadline, subproject_id, resource_type_id, resource_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, task.getTitle(), task.getDescription(), task.getEstimatedHours(), task.getDeadline(), task.getSubprojectId(), task.getResourceTypeId(), task.getResourceId());
    }
}
