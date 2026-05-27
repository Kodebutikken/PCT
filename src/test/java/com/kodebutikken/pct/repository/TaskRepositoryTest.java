package com.kodebutikken.pct.repository;

import com.kodebutikken.pct.model.ProjectStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:h2init.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
class TaskRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO users (id, name, email, password_hash, role) VALUES (1, 'Test', 'test@test.com', 'hash', 'ADMINISTRATOR')");
        jdbcTemplate.update("INSERT INTO project (id, title, created_by) VALUES (1, 'Projekt', 1)");
        jdbcTemplate.update("INSERT INTO subproject (id, title, project_id) VALUES (1, 'Delprojekt 1', 1)");
        jdbcTemplate.update("INSERT INTO resource (id, name, skills, daily_working_hours, hourly_wage) VALUES (1, 'Ressource A', 'Java', 8, 100.00)");
        jdbcTemplate.update("INSERT INTO resource (id, name, skills, daily_working_hours, hourly_wage) VALUES (2, 'Ressource B', 'Design', 8, 200.00)");
    }

    @Test
    void getProjectStats_noTasks_returnsZeroStats() {
        ProjectStats stats = taskRepository.getProjectStats(1);

        assertAll(
                () -> assertEquals(1, stats.getSubprojectCount()),
                () -> assertEquals(0, stats.getTaskCount()),
                () -> assertEquals(0.0, stats.getTotalEstimatedHours(), 0.001),
                () -> assertEquals(0L, stats.getTotalProjectedCost())
        );
    }

    @Test
    void getProjectStats_multipleTasks_sumsCorrectly() {
        int task1 = insertTask(1, 5.0);
        int task2 = insertTask(1, 3.0);
        linkResource(task1, 1); // 5h * 100 kr/t = 500
        linkResource(task2, 1); // 3h * 100 kr/t = 300

        ProjectStats stats = taskRepository.getProjectStats(1);

        assertAll(
                () -> assertEquals(2, stats.getTaskCount()),
                () -> assertEquals(8.0, stats.getTotalEstimatedHours(), 0.001),
                () -> assertEquals(800L, stats.getTotalProjectedCost())
        );
    }

    @Test
    void getProjectStats_afterAddingTask_sumIncreases() {
        int task1 = insertTask(1, 5.0);
        linkResource(task1, 1);

        ProjectStats before = taskRepository.getProjectStats(1);
        assertEquals(1, before.getTaskCount());
        assertEquals(5.0, before.getTotalEstimatedHours(), 0.001);
        assertEquals(500L, before.getTotalProjectedCost());

        int task2 = insertTask(1, 3.0);
        linkResource(task2, 1);

        ProjectStats after = taskRepository.getProjectStats(1);
        assertEquals(2, after.getTaskCount());
        assertEquals(8.0, after.getTotalEstimatedHours(), 0.001);
        assertEquals(800L, after.getTotalProjectedCost());
    }

    @Test
    void getProjectStats_afterDeletingTask_sumDecreases() {
        int task1 = insertTask(1, 5.0);
        int task2 = insertTask(1, 3.0);
        linkResource(task1, 1);
        linkResource(task2, 1);

        ProjectStats before = taskRepository.getProjectStats(1);
        assertEquals(2, before.getTaskCount());
        assertEquals(800L, before.getTotalProjectedCost());

        jdbcTemplate.update("DELETE FROM task WHERE id = ?", task2);

        ProjectStats after = taskRepository.getProjectStats(1);
        assertEquals(1, after.getTaskCount());
        assertEquals(5.0, after.getTotalEstimatedHours(), 0.001);
        assertEquals(500L, after.getTotalProjectedCost());
    }

    @Test
    void getProjectStats_taskWithNoResource_hoursCountedCostExcluded() {
        int task1 = insertTask(1, 5.0);
        int task2 = insertTask(1, 3.0);
        linkResource(task1, 1);

        ProjectStats stats = taskRepository.getProjectStats(1);

        assertAll(
                () -> assertEquals(2, stats.getTaskCount()),
                () -> assertEquals(8.0, stats.getTotalEstimatedHours(), 0.001),
                () -> assertEquals(500L, stats.getTotalProjectedCost())
        );
    }

    @Test
    void getTaskCountsByProjectId_multipleSubprojects_returnsCorrectMap() {
        jdbcTemplate.update("INSERT INTO subproject (id, title, project_id) VALUES (2, 'Delprojekt 2', 1)");

        insertTask(1, 2.0);
        insertTask(1, 3.0);
        insertTask(2, 4.0);

        Map<Integer, Integer> counts = taskRepository.getTaskCountsByProjectId(1);

        assertEquals(2, counts.get(1));
        assertEquals(1, counts.get(2));
    }

    private int insertTask(int subprojectId, double hours) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO task (title, estimated_hours, subproject_id) VALUES (?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, "Opgave");
            ps.setDouble(2, hours);
            ps.setInt(3, subprojectId);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    private void linkResource(int taskId, int resourceId) {
        jdbcTemplate.update(
                "INSERT INTO task_resource (task_id, resource_id) VALUES (?, ?)",
                taskId, resourceId
        );
    }
}
