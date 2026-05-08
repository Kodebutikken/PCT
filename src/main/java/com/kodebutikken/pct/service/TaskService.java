package com.kodebutikken.pct.service;

import com.kodebutikken.pct.model.Task;
import com.kodebutikken.pct.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(Task task) {
        // Implementer logikken for at oprette en opgave i databasen
        // Du kan bruge en repository eller DAO til at håndtere databaseoperationerne
        taskRepository.createTask(task);
    }

    public List<Task> getTasksBySubprojectId(int subprojectId) {
        // Implementer logikken for at hente alle opgaver for et givent delprojekt
        // Brug subprojectId til at filtrere opgaverne i databasen
        return taskRepository.getTasksBySubprojectId(subprojectId);
    }
}
