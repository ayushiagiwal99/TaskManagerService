package com.taskManagerService.filters;

import com.taskManagerService.model.Task;

import java.util.Comparator;

public class CreatedAtAscSort implements SortStrategy {

    @Override
    public String getSortBy() {
        return "createdAt";
    }

    @Override
    public String getOrder() {
        return "ASC";
    }

    @Override
    public Comparator<Task> getComparator() {
        return Comparator.comparingLong(Task::getCreatedAt);
    }
}