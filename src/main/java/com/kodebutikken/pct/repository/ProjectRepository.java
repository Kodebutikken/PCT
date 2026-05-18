package com.kodebutikken.pct.repository;

import com.kodebutikken.pct.exception.DatabaseOperationException;
import com.kodebutikken.pct.exception.ProjectNotFoundException;
import com.kodebutikken.pct.model.Project;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectRepository {
    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Project> projectRowMapper = (rs, rowNum) -> new Project(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getDate("deadline").toLocalDate(),
            rs.getInt("created_by")
    );

    public void save(Project project, int userId) {
        try {
            String sql = "INSERT INTO project (title, description, deadline, created_by) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, project.getTitle(), project.getDescription(), project.getDeadline(), userId);
        } catch (DataAccessException exception) {
            throw new DatabaseOperationException(exception.getMessage());
        }
    }

    public List<Project> getProjectsByUserId(int userId) {
        try {
            String sql = "SELECT * FROM project WHERE created_by = ?";
            return jdbcTemplate.query(sql, projectRowMapper, userId);
        } catch (DataAccessException exception) {
            throw new DatabaseOperationException(exception.getMessage());
        }
    }

    public Project getProjectById(int projectId) {
        try {
            String sql = "SELECT * FROM project WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, projectRowMapper, projectId);
        } catch (EmptyResultDataAccessException exception) {
            throw new ProjectNotFoundException("Projekt med id " + projectId + " blev ikke fundet");
        } catch (DataAccessException exception) {
            throw new DatabaseOperationException(exception.getMessage());
        }
    }

    public void delete(int id, int userId) {
        String sql = "DELETE FROM project WHERE id = ? AND created_by = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    public boolean isProjectOwner(int projectId, int userId) {
        String sql = "SELECT created_by FROM project WHERE id = ?";
        Integer ownerId = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
        return ownerId != null && ownerId == userId;
    }
}
