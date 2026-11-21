package com.taskManagerService.model;

import com.taskManagerService.enums.TaskPriority;
import com.taskManagerService.exception.ValidationException;
import com.taskManagerService.utils.DateHelper;
import com.taskManagerService.utils.InputValidator;

public class TaskDraft {

    private final String title;
    private final String description;
    private final boolean completed;
    private final String dueDate;
    private final TaskPriority priority;
    private final int parentId;

    private TaskDraft(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.completed = builder.completed;
        this.dueDate = builder.dueDate;
        this.priority = builder.priority;
        this.parentId = builder.parentId;
    }

    // Getters

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getDueDate() {
        return dueDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public int getParentId() {
        return parentId;
    }

    public static class Builder {

        private final String title;
        private final String description;
        private final boolean completed;

        private String dueDate;
        private TaskPriority priority = TaskPriority.MEDIUM;
        private int parentId;

        public Builder(String title, String description, boolean completed) {

            InputValidator.validateString(title, "Title");
            InputValidator.validateString(description, "Description");

            this.title = title.trim();
            this.description = description.trim();
            this.completed = completed;
        }

        public Builder dueDate(String dueDate) {

            if (dueDate != null && !DateHelper.isValidDate(dueDate)) {
                throw new ValidationException("Due Date", "Invalid due date. Expected format: YYYY-MM-DD");
            }

            this.dueDate = dueDate;
            return this;
        }

        public Builder priority(TaskPriority priority) {
            this.priority = priority != null
                    ? priority
                    : TaskPriority.MEDIUM;
            return this;
        }

        public Builder parentId(int parentId) {

            InputValidator.validatePositiveNumber(parentId, "Parent ID");

            this.parentId = parentId;
            return this;
        }

        public TaskDraft build() {
            return new TaskDraft(this);
        }
    }
}
