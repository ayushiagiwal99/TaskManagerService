package com.taskManagerService.service;

import com.taskManagerService.repository.TaskRepository;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}
