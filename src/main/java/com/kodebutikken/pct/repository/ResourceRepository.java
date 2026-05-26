package com.kodebutikken.pct.repository;

import com.kodebutikken.pct.exception.DatabaseOperationException;
import com.kodebutikken.pct.model.Resource;
import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;

@Repository
public class ResourceRepository {

    private final JdbcTemplate jdbcTemplate;

    public ResourceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Resource resource) {
        try {
            String sql = "INSERT INTO resource (name, skills, daily_working_hours, hourly_wage) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, resource.getName());
                ps.setString(2, resource.getSkills());
                ps.setInt(3, resource.getDailyWorkingHours());
                ps.setDouble(4, resource.getHourlyWage());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key == null) {
                throw new DatabaseOperationException("Kunne ikke hente ressourcens id efter oprettelse");
            }
        } catch (DataAccessException e) {
            throw new DatabaseOperationException(e.getMessage());
        }
    }


    public List<Resource> getAllResources() {
        try {
            String sql = "SELECT * FROM resource";
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Resource resource = new Resource();
                resource.setId(rs.getInt("id"));
                resource.setName(rs.getString("name"));
                resource.setSkills(rs.getString("skills"));
                resource.setDailyWorkingHours(rs.getInt("daily_working_hours"));
                resource.setHourlyWage(rs.getDouble("hourly_wage"));
                return resource;
            });
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Fejl ved hentning af ressourcer: " + e.getMessage());
        }
    }

    public Resource getResourceById(int id) {
        try {
            String sql = "SELECT * FROM resource WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Resource resource = new Resource();
                resource.setId(rs.getInt("id"));
                resource.setName(rs.getString("name"));
                resource.setSkills(rs.getString("skills"));
                resource.setDailyWorkingHours(rs.getInt("daily_working_hours"));
                resource.setHourlyWage(rs.getDouble("hourly_wage"));
                return resource;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Fejl ved hentning af ressource: " + e.getMessage());
        }
    }

    public void update(@Valid Resource resource) {
        try {
            String sql = "UPDATE resource SET name = ?, skills = ?, daily_working_hours = ?, hourly_wage = ? WHERE id = ?";
            jdbcTemplate.update(sql, resource.getName(), resource.getSkills(), resource.getDailyWorkingHours(), resource.getHourlyWage(), resource.getId());
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Fejl ved opdatering af ressource: " + e.getMessage());
        }
    }

    public void delete(int id) {
        try {
            String sql = "DELETE FROM resource WHERE id = ?";
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Fejl ved sletning af ressource: " + e.getMessage());
        }
    }
}
