package com.taskManagerService.repository;

import com.taskManagerService.exception.NotFoundException;
import com.taskManagerService.model.Task;
import com.taskManagerService.model.TaskDraft;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryTaskRepository implements TaskRepository {

    private static final InMemoryTaskRepository INSTANCE = new InMemoryTaskRepository();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    // Private constructor prevents external instantiation.
    private InMemoryTaskRepository() {
    }

    // Global access point.
    public static InMemoryTaskRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Task save(TaskDraft taskDraft) {
        int id = idGenerator.getAndIncrement();
        Task task = Task.createPersistedTask(id, taskDraft);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Optional<Task> findById(int id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public boolean delete(int id) {
        return tasks.remove(id) != null;
    }

    @Override
    public Task update(Task updatedTask) {
        if (!tasks.containsKey(updatedTask.getId())) {
            throw new NotFoundException("Task not found");
        }
        tasks.put(updatedTask.getId(), updatedTask);
        return updatedTask;
    }
}

