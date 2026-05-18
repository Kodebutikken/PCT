package com.kodebutikken.pct.repository;

import com.kodebutikken.pct.exception.DatabaseOperationException;
import com.kodebutikken.pct.exception.ProjectNotFoundException;
import com.kodebutikken.pct.model.ProjectAccessLevel;
import com.kodebutikken.pct.model.ProjectMember;
import com.kodebutikken.pct.model.Project;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
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

    private final RowMapper<ProjectMember> projectMemberRowMapper = (rs, rowNum) -> new ProjectMember(
            rs.getInt("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            ProjectAccessLevel.valueOf(rs.getString("access_level"))
    );

    public int save(Project project, int userId) {
        try {
            String sql = "INSERT INTO project (title, description, deadline, created_by) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, project.getTitle());
                ps.setString(2, project.getDescription());
                ps.setObject(3, project.getDeadline());
                ps.setInt(4, userId);
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key == null) {
                throw new DatabaseOperationException("Kunne ikke hente projektets id efter oprettelse");
            }

            return key.intValue();
        } catch (DataAccessException exception) {
            throw new DatabaseOperationException(exception.getMessage());
        }
    }

    public List<Project> getProjectsAccessibleByUserId(int userId) {
        try {
            String sql = """
                    SELECT DISTINCT p.*
                    FROM project p
                    LEFT JOIN project_user pu ON p.id = pu.project_id
                    WHERE p.created_by = ? OR pu.user_id = ?
                    ORDER BY p.deadline ASC, p.id DESC
                    """;
            return jdbcTemplate.query(sql, projectRowMapper, userId, userId);
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

    public void update(Project project) {
        try {
            String sql = "UPDATE project SET title = ?, description = ?, deadline = ? WHERE id = ?";
            jdbcTemplate.update(sql, project.getTitle(), project.getDescription(), project.getDeadline(), project.getId());
        } catch (DataAccessException exception) {
            throw new DatabaseOperationException(exception.getMessage());
        }
    }

    public boolean isProjectOwner(int projectId, int userId) {
        String sql = "SELECT created_by FROM project WHERE id = ?";
        Integer ownerId = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
        return ownerId != null && ownerId == userId;
    }

    public boolean isProjectMember(int projectId, int userId) {
        String sql = "SELECT COUNT(*) FROM project_user WHERE project_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, projectId, userId);
        return count != null && count > 0;
    }

    public boolean hasProjectAccessLevel(int projectId, int userId, Collection<ProjectAccessLevel> accessLevels) {
        if (accessLevels == null || accessLevels.isEmpty()) {
            return false;
        }

        String placeholders = String.join(",", Collections.nCopies(accessLevels.size(), "?"));
        String sql = "SELECT COUNT(*) FROM project_user WHERE project_id = ? AND user_id = ? AND access_level IN (" + placeholders + ")";

        Object[] params = new Object[2 + accessLevels.size()];
        params[0] = projectId;
        params[1] = userId;

        int index = 2;
        for (ProjectAccessLevel accessLevel : accessLevels) {
            params[index++] = accessLevel.name();
        }

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, params);
        return count != null && count > 0;
    }

    public List<ProjectMember> getProjectMembers(int projectId) {
        try {
            String sql = """
                    SELECT pu.user_id, u.name, u.email, pu.access_level
                    FROM project_user pu
                    JOIN user u ON u.id = pu.user_id
                    WHERE pu.project_id = ?
                    ORDER BY u.name ASC
                    """;
            return jdbcTemplate.query(sql, projectMemberRowMapper, projectId);
        } catch (DataAccessException exception) {
            throw new DatabaseOperationException(exception.getMessage());
        }
    }

    public void replaceProjectMembers(int projectId, List<ProjectMember> members) {
        try {
            jdbcTemplate.update("DELETE FROM project_user WHERE project_id = ?", projectId);

            if (members == null || members.isEmpty()) {
                return;
            }

            String sql = "INSERT INTO project_user (project_id, user_id, access_level) VALUES (?, ?, ?)";
            for (ProjectMember member : members) {
                jdbcTemplate.update(sql, projectId, member.getUserId(), member.getAccessLevel().name());
            }
        } catch (DataAccessException exception) {
            throw new DatabaseOperationException(exception.getMessage());
        }
    }
}
