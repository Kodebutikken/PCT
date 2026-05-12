package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.TaskForm;
import com.kodebutikken.pct.model.Task;
import com.kodebutikken.pct.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final SubprojectService subprojectService;

    public TaskService(TaskRepository taskRepository, SubprojectService subprojectService) {
        this.taskRepository = taskRepository;
        this.subprojectService = subprojectService;
    }

    public void createTask(TaskForm taskForm, int subprojectId) {
        if(!subprojectService.existsById(subprojectId)) {
            throw new IllegalArgumentException("Delprojekt findes ikke");
        }
        if(taskForm.getDeadline() != null && taskForm.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Deadline må ikke være i fortiden");
        }

        Task task = new Task();
        task.setTitle(taskForm.getTitle());
        task.setDescription(taskForm.getDescription());
        task.setEstimatedHours(taskForm.getEstimatedTime());
        task.setDeadline(taskForm.getDeadline());
        task.setSubprojectId(subprojectId);

        taskRepository.createTask(task);
    }

    public List<Task> getTasksBySubprojectId(int subprojectId) {
        // Implementer logikken for at hente alle opgaver for et givent delprojekt
        // Brug subprojectId til at filtrere opgaverne i databasen
        return taskRepository.getTasksBySubprojectId(subprojectId);
    }
}
