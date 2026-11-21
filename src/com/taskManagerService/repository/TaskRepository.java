package com.taskManagerService.repository;

import com.taskManagerService.model.Task;
import com.taskManagerService.model.TaskDraft;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(TaskDraft taskDraft);

    Optional<Task> findById(int id);

    List<Task> findAll();

    boolean delete(int id);
}
