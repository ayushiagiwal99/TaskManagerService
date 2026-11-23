package com.taskManagerService.api;

public class TaskQuery {

    private final String completed;
    private final String priority;
    private final int page;
    private final int size;

    public TaskQuery(String completed, String priority, int page, int size) {
        this.completed = completed;
        this.priority = priority;
        this.page = page;
        this.size = size;
    }

    public String getCompleted() {
        return completed;
    }

    public String getPriority() {
        return priority;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
