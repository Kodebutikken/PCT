package com.kodebutikken.pct.repository;

import com.kodebutikken.pct.model.ProjectStats;
import com.kodebutikken.pct.model.Resource;
import com.kodebutikken.pct.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TaskRepository {
    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setEstimatedHours(rs.getDouble("estimated_hours"));
        task.setDeadline(rs.getDate("deadline") != null
                ? rs.getDate("deadline").toLocalDate()
                : null);
        task.setSubprojectId(rs.getInt("subproject_id"));
        task.setResourceTypeId((Integer) rs.getObject("resource_type_id"));
        task.setResourceId((Integer) rs.getObject("resource_id"));
        task.setCreatedAt(rs.getDate("created_at").toLocalDate());
        return task;
    };

    public List<Task> getTasksBySubprojectId(int subprojectId) {
        String sql = "SELECT * FROM task WHERE subproject_id = ?";
        return jdbcTemplate.query(sql, taskRowMapper, subprojectId);
    }

    public void createTask(Task task) {
        String sql = "INSERT INTO task (title, description, estimated_hours, deadline, subproject_id, resource_type_id, resource_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, task.getTitle(), task.getDescription(), task.getEstimatedHours(), task.getDeadline(), task.getSubprojectId(), task.getResourceTypeId(), task.getResourceId());

        Integer taskId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        String updateTaskResourceSql = "INSERT INTO task_resource (resource_id, task_id) VALUES (?, ?)";
        jdbcTemplate.update(updateTaskResourceSql, task.getResourceId(), taskId);
    }

    public void deleteTask(int pid, int tid) {
        String sql = "DELETE FROM task WHERE id = ? AND subproject_id = ?";
        jdbcTemplate.update(sql, tid, pid);
    }

    public ProjectStats getProjectStats(int projectId) {
        String sql = """
                SELECT
                    COUNT(DISTINCT s.id)                                        AS subproject_count,
                    COUNT(t.id)                                                  AS task_count,
                    COALESCE(SUM(t.estimated_hours), 0)                          AS total_hours,
                    COALESCE(ROUND(SUM(t.estimated_hours * r.hourly_wage)), 0)   AS total_cost
                FROM subproject s
                LEFT JOIN task t ON t.subproject_id = s.id
                LEFT JOIN task_resource tr ON tr.task_id = t.id
                LEFT JOIN resource r ON r.id = tr.resource_id
                WHERE s.project_id = ?
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ProjectStats(
                rs.getInt("subproject_count"),
                rs.getInt("task_count"),
                rs.getDouble("total_hours"),
                rs.getLong("total_cost")
        ), projectId);
    }

    public Map<Integer, Integer> getTaskCountsByProjectId(int projectId) {
        String sql = """
                SELECT t.subproject_id, COUNT(*) AS task_count
                FROM task t
                JOIN subproject s ON s.id = t.subproject_id
                WHERE s.project_id = ?
                GROUP BY t.subproject_id
                """;
        Map<Integer, Integer> result = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            result.put(rs.getInt("subproject_id"), rs.getInt("task_count"));
        }, projectId);
        return result;
    }

    public List<Resource> getResourcesForSubproject(int subprojectId) {
        String sql = """
                SELECT r.* FROM resource r
                JOIN task_resource tr ON r.id = tr.resource_id
                JOIN task t ON tr.task_id = t.id
                WHERE t.subproject_id = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Resource resource = new Resource();
            resource.setId(rs.getInt("id"));
            resource.setName(rs.getString("name"));
            resource.setSkills(rs.getString("skills"));
            resource.setDailyWorkingHours(rs.getInt("daily_working_hours"));
            resource.setHourlyWage(rs.getDouble("hourly_wage"));
            return resource;
        }, subprojectId);
    }
}
