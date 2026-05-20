package com.kodebutikken.pct.repository;

import com.kodebutikken.pct.exception.DatabaseOperationException;
import com.kodebutikken.pct.model.Resource;
import com.kodebutikken.pct.model.Subproject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubprojectRepository {
    private final JdbcTemplate jdbcTemplate;
    public SubprojectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Subproject> subprojectRowMapper = (rs, rowNum) -> new Subproject(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getDate("deadline").toLocalDate(),
            rs.getInt("project_id"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public void createSubproject(Subproject subproject) {
        try {
            String sql = "INSERT INTO subproject (title, description, deadline, project_id) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, subproject.getTitle(), subproject.getDescription(), subproject.getDeadline(), subproject.getProjectId());
        } catch (DataAccessException exception) {
            throw new DatabaseOperationException(exception.getMessage());
        }
    }

    public List<Subproject> getSubprojectsByProjectId(int projectId) {
        try {
            String sql = "SELECT * FROM subproject WHERE project_id = ?";
            return jdbcTemplate.query(sql, subprojectRowMapper, projectId);
        } catch (DataAccessException exception) {
            throw new DatabaseOperationException(exception.getMessage());
        }
    }

    public Subproject getSubprojectById(int id) {
        try {
            String sql = "SELECT * FROM subproject WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, subprojectRowMapper, id);
        } catch (DataAccessException exception) {
            throw new DatabaseOperationException(exception.getMessage());
        }
    }

    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM subproject WHERE ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public Integer getProjectOwnerId(int subprojectId) {
        String sql = "SELECT p.created_by FROM project p JOIN subproject s ON p.id = s.project_id WHERE s.id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, subprojectId);
    }

    public Integer getProjectIdBySubprojectId(int subprojectId) {
        String sql = "SELECT project_id FROM subproject WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, subprojectId);
    }
}
