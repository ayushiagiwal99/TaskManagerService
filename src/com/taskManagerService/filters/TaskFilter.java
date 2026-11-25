package com.taskManagerService.filters;

import com.taskManagerService.model.Task;

public interface TaskFilter {

    boolean matches(Task task, TaskQuery query);
}
