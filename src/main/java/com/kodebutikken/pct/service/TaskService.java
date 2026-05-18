package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.TaskForm;
import com.kodebutikken.pct.exception.UnauthorizedException;
import com.kodebutikken.pct.model.Task;
import com.kodebutikken.pct.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final SubprojectService subprojectService;
    private final ProjectAccessService projectAccessService;

    public TaskService(TaskRepository taskRepository,
                       SubprojectService subprojectService,
                       ProjectAccessService projectAccessService) {
        this.taskRepository = taskRepository;
        this.subprojectService = subprojectService;
        this.projectAccessService = projectAccessService;
    }

    public void createTask(TaskForm taskForm, int subprojectId, int userId) {
        if(!subprojectService.existsById(subprojectId)) {
            throw new IllegalArgumentException("Delprojekt findes ikke");
        }

        if(taskForm.getEstimatedTime() <= 0) {
            throw new IllegalArgumentException("Timer skal være større end 0");
        }

        if(taskForm.getTitle() == null || taskForm.getTitle().isBlank()) {
            throw new IllegalArgumentException("Titel må ikke være tom");
        }

        if(taskForm.getDeadline() != null && taskForm.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Deadline må ikke være i fortiden");
        }
        Integer projectId = subprojectService.getProjectIdBySubprojectId(subprojectId);
        if(projectId == null || !projectAccessService.canEditProject(projectId, userId)) {
            throw new UnauthorizedException("Du har ikke adgang til dette projekt");
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
