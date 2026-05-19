package com.kodebutikken.pct.repository;

import com.kodebutikken.pct.exception.DatabaseOperationException;
import com.kodebutikken.pct.model.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;

@Repository
public class ResourceRepository {

    private JdbcTemplate jdbcTemplate;

    public ResourceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Resource resource) {
        try {
            String sql = "INSERT INTO resources (name, compentencies, daily_working_hours, hourly_wage) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, resource.getName());
                ps.setString(2, resource.getCompentencies());
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


}
