package com.kodebutikken.pct.repository;

import com.kodebutikken.pct.model.Subproject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubprojectRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubprojectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createSubproject(Subproject subproject) {
        String sql = "INSERT INTO subproject (titel, description, deadline, project_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, subproject.getTitel(), subproject.getDescription(), subproject.getDeadline(), subproject.getProjectId());
    }

    public List<Subproject> getSubprojectsByProjectId(int projectId) {
        String sql = "SELECT * FROM subproject WHERE project_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Subproject(
                rs.getInt("id"),
                rs.getString("titel"),
                rs.getString("description"),
                rs.getDate("deadline").toLocalDate(),
                rs.getInt("project_id"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ), projectId);
    }
}
