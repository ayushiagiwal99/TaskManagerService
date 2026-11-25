package com.taskManagerService.filters;

import com.taskManagerService.model.Task;

import java.util.Comparator;

public class CreatedAtDescSort implements SortStrategy {

    @Override
    public String getSortBy() {
        return "createdAt";
    }

    @Override
    public String getOrder() {
        return "DESC";
    }

    @Override
    public Comparator<Task> getComparator() {
        return Comparator.comparingLong(Task::getCreatedAt).reversed();
    }
}