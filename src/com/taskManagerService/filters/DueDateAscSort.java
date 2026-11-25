package com.taskManagerService.filters;

import com.taskManagerService.model.Task;

import java.time.LocalDate;
import java.util.Comparator;

public class DueDateAscSort implements SortStrategy {

    @Override
    public String getSortBy() {
        return "dueDate";
    }

    @Override
    public String getOrder() {
        return "ASC";
    }

    @Override
    public Comparator<Task> getComparator() {

        return Comparator.comparing(
                task -> task.getDueDate() == null
                        ? null
                        : LocalDate.parse(task.getDueDate()),
                Comparator.nullsLast(Comparator.naturalOrder())
        );
    }
}