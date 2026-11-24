package com.taskManagerService.api;

import com.taskManagerService.model.Task;
import com.taskManagerService.service.TaskService;
import com.taskManagerService.exception.ApiException;

import java.util.List;

public class TaskApi {

    private final TaskService taskService;

    public TaskApi(TaskService taskService) {
        this.taskService = taskService;
    }

    public ApiResponse createTask(
            TaskRequest request,
            String idempotencyKey) {

        try {
            Task task = taskService.createTask(request);

            return new ApiResponse(201, task);

        } catch (ApiException exception) {
            return ErrorResponseFactory.from(exception);

        } catch (Exception exception) {
            return ErrorResponseFactory.fromUnexpected(exception);
        }
    }

    public ApiResponse getTask(int id) {

        try {
            Task task = taskService.getTask(id);

            return new ApiResponse(200, task);

        } catch (ApiException exception) {
            return ErrorResponseFactory.from(exception);

        } catch (Exception exception) {
            return ErrorResponseFactory.fromUnexpected(exception);
        }
    }

    public ApiResponse listTasks(TaskQuery query) {

        try {
            List<Task> tasks = taskService.listTasks(query);

            return new ApiResponse(200, tasks);

        } catch (ApiException exception) {
            return ErrorResponseFactory.from(exception);

        } catch (Exception exception) {
            return ErrorResponseFactory.fromUnexpected(exception);
        }
    }

    public ApiResponse updateTask(int id, TaskRequest request) {

        try {
            Task task = taskService.updateTask(id, request);

            return new ApiResponse(200, task);

        } catch (ApiException exception) {
            return ErrorResponseFactory.from(exception);

        } catch (Exception exception) {
            return ErrorResponseFactory.fromUnexpected(exception);
        }
    }

    public ApiResponse deleteTask(int id) {

        try {
            taskService.deleteTask(id);

            return new ApiResponse(200, "Task deleted successfully");

        } catch (ApiException exception) {
            return ErrorResponseFactory.from(exception);

        } catch (Exception exception) {
            return ErrorResponseFactory.fromUnexpected(exception);
        }
    }

    public ApiResponse createTaskFromText(String text, String idempotencyKey) {

        try {
            Task task = taskService.createTaskFromText(text, idempotencyKey);

            return new ApiResponse(201, task);

        } catch (ApiException e) {
            return ErrorResponseFactory.from(e);

        } catch (Exception e) {
            return ErrorResponseFactory.fromUnexpected(e);
        }
    }
}