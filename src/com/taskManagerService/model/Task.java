package com.taskManagerService.model;

import com.taskManagerService.enums.TaskPriority;
import com.taskManagerService.utils.InputValidator;

public class Task {
    private final int id;

    private final String title; // required
    private final String description; // required
    private final boolean completed; // required, default false

    private final String dueDate; // format validator - YYYY-MM-DD
    private final TaskPriority priority; // default medium
    private final int parentId;
    private final long createdAt;

    private Task(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.completed = builder.completed;
        this.dueDate = builder.dueDate;
        this.priority = builder.priority;
        this.parentId = builder.parentId;
        this.createdAt = System.currentTimeMillis();
    }

    private Task(int id, TaskDraft draft) {
        this.id = id;
        this.title = draft.getTitle();
        this.description = draft.getDescription();
        this.completed = draft.isCompleted();
        this.dueDate = draft.getDueDate();
        this.priority = draft.getPriority();
        this.parentId = draft.getParentId();
        this.createdAt = System.currentTimeMillis();
    }

    public static Task createPersistedTask(int id, TaskDraft draft) {
        InputValidator.validatePositiveNumber(id, "Task ID");
        return new Task(id, draft);
    }

    public static class Builder {
        private int id;

        private final String title;
        private final String description;
        private boolean completed = false;

        private String dueDate; // format validator - YYYY-MM-DD
        private TaskPriority priority = TaskPriority.MEDIUM;
        private int parentId;

        public Builder(String title, String description, boolean completed) {
            this.title = title;
            this.description = description;
            this.completed = completed;
        }

        public Builder dueDate(String dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder priority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder parentId(int parentId) {
            this.parentId = parentId;
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }
}
