package com.taskManagerService.filters;

import com.taskManagerService.enums.TaskPriority;
import com.taskManagerService.exception.ValidationException;

public class TaskQuery {

    private final Boolean completed;
    private final TaskPriority priority;
    private final String search;
    private final String sortBy;
    private final String order;
    private final int page;
    private final int pageSize;

    private TaskQuery(Builder builder) {
        this.completed = builder.completed;
        this.priority = builder.priority;
        this.search = builder.search;
        this.sortBy = builder.sortBy;
        this.order = builder.order;
        this.page = builder.page;
        this.pageSize = builder.pageSize;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public String getSearch() {
        return search;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getOrder() {
        return order;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Boolean completed;
        private TaskPriority priority;
        private String search;
        private String sortBy;
        private String order;
        private int page = 1;
        private int pageSize = 10;

        public Builder completed(Boolean completed) {
            this.completed = completed;
            return this;
        }

        public Builder priority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder search(String search) {
            this.search = search;
            return this;
        }

        public Builder sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder order(String order) {
            this.order = order;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public TaskQuery build() {

            if (page < 1) {
                throw new ValidationException(
                        "Page",
                        "Page must be greater than or equal to 1"
                );
            }

            if (pageSize < 1) {
                throw new ValidationException(
                        "Page size",
                        "Page size must be greater than or equal to 1"
                );
            }

            return new TaskQuery(this);
        }
    }
}
