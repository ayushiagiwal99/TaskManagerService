package com.taskManagerService.filters;

import com.taskManagerService.model.Task;

public class SearchFilter implements TaskFilter {

    @Override
    public boolean matches(Task task, TaskQuery query) {

        String search = query.getSearch();

        if (search == null || search.isBlank()) {
            return true;
        }

        search = search.toLowerCase();

        return task.getTitle().toLowerCase().contains(search)
                || task.getDescription().toLowerCase().contains(search);
    }
}
