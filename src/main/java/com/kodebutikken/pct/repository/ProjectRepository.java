package com.kodebutikken.pct.repository;

import com.kodebutikken.pct.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createProject(Project project, int profileId) {
        String sql = "INSERT INTO project (title, description, profile_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, project.getTitle(), project.getDescription(), profileId);

    }

    public List<Project> getProjectsByProfileId(int profileId) {
        String sql = "SELECT * FROM project WHERE created_by = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Project(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDate("created_at").toLocalDate(),
                rs.getInt("created_by")
        ), profileId);
    }
}
