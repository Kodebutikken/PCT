package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.TaskForm;
import com.kodebutikken.pct.model.Task;
import com.kodebutikken.pct.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SubprojectService subprojectService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_success() {

        TaskForm taskForm = new TaskForm();
        taskForm.setTitle("Ny task");
        taskForm.setDescription("Beskrivelse");
        taskForm.setEstimatedTime(5.0);
        taskForm.setDeadline(LocalDate.now().plusDays(3));

        int subprojectId = 1;
        int profileId = 1;

        when(subprojectService.existsById(subprojectId)).thenReturn(true);
        when(subprojectService.getProjectOwnerId(subprojectId)).thenReturn(profileId);

        taskService.createTask(taskForm, subprojectId, profileId);

        ArgumentCaptor<Task> taskCaptor =
                ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).createTask(taskCaptor.capture());

        Task capturedTask = taskCaptor.getValue();

        assertEquals("Ny task", capturedTask.getTitle());
        assertEquals("Beskrivelse", capturedTask.getDescription());
        assertEquals(5, capturedTask.getEstimatedHours());
        assertEquals(subprojectId, capturedTask.getSubprojectId());
    }

    @Test
    void createTask_shouldThrowException_whenTitleIsEmpty() {

        TaskForm taskForm = new TaskForm();
        taskForm.setTitle("");
        taskForm.setEstimatedTime(5.0);

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(taskForm, 1, 1);
        });

        verify(taskRepository, never()).createTask(any());
    }

    @Test
    void createTask_shouldThrowException_whenEstimatedTimeIsZero() {

        TaskForm taskForm = new TaskForm();
        taskForm.setTitle("Ny task");
        taskForm.setEstimatedTime(0.0);

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(taskForm, 1, 1);
        });

        verify(taskRepository, never()).createTask(any());
    }

    @Test
    void createTask_shouldThrowException_whenSubprojectDoesNotExist() {

        TaskForm taskForm = new TaskForm();
        taskForm.setTitle("Ny task");
        taskForm.setEstimatedTime(5.0);

        when(subprojectService.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(taskForm, 1, 1);
        });

        verify(taskRepository, never()).createTask(any());
    }

    @Test
    void createTask_shouldThrowException_whenDeadlineIsPast() {

        TaskForm taskForm = new TaskForm();
        taskForm.setTitle("Ny task");
        taskForm.setEstimatedTime(5.0);
        taskForm.setDeadline(LocalDate.now().minusDays(1));

        when(subprojectService.existsById(1)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(taskForm, 1, 1);
        });

        verify(taskRepository, never()).createTask(any());
    }

    @Test
    void createTask_shouldThrowException_whenUserIsNotProjectOwner() {

        TaskForm taskForm = new TaskForm();
        taskForm.setTitle("Ny task");
        taskForm.setEstimatedTime(5.0);

        when(subprojectService.existsById(1)).thenReturn(true);
        when(subprojectService.getProjectOwnerId(1)).thenReturn(2);

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(taskForm, 1, 1);
        });
        verify(taskRepository, never()).createTask(any());
    }

    @Test
    void getTasksBySubprojectId() {
        List<Task> mockTasks = List.of(
                new Task(1, "Task 1", "Beskrivelse 1", 5, LocalDate.now().plusDays(2), 1, 0, 0),
                new Task(2, "Task 2", "Beskrivelse 2", 3, LocalDate.now().plusDays(5), 1, 0, 0));
        when(taskRepository.getTasksBySubprojectId(1))
                .thenReturn(mockTasks);

        List<Task> result = taskService.getTasksBySubprojectId(1);

        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals("Task 2", result.get(1).getTitle());

        verify(taskRepository).getTasksBySubprojectId(1);
    }
}