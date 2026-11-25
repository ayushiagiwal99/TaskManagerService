package com.taskManagerService.filters;

import com.taskManagerService.model.Task;

public class CompletedFilter implements TaskFilter {

    @Override
    public boolean matches(Task task, TaskQuery query) {

        if (query.getCompleted() == null) {
            return true;
        }

        return task.isCompleted() == query.getCompleted();
    }
}