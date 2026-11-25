package com.taskManagerService.filters;

import com.taskManagerService.model.Task;

import java.util.Comparator;

public interface SortStrategy {

    String getSortBy();

    String getOrder();

    Comparator<Task> getComparator();
}