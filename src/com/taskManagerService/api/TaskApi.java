package com.taskManagerService.api;

import com.taskManagerService.model.Task;
import com.taskManagerService.model.TaskQuery;
import com.taskManagerService.model.TaskRequest;
import com.taskManagerService.service.TaskService;
import com.taskManagerService.exception.ApiException;

import java.util.List;
import java.util.Map;

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

    public ApiResponse createSubtask(int parentId, TaskRequest request) {

        try {
            Task task = taskService.createSubtask(parentId, request);

            return new ApiResponse(201, task.toString());

        } catch (ApiException e) {
            return ErrorResponseFactory.from(e);

        } catch (Exception e) {
            return ErrorResponseFactory.fromUnexpected(e);
        }
    }

    public ApiResponse listSubtasks(int parentId) {

        try {
            List<Task> subtasks = taskService.listSubtasks(parentId);

            List<String> response = subtasks.stream()
                    .map(Task::toString)
                    .toList();

            return new ApiResponse(200, response);

        } catch (ApiException e) {
            return ErrorResponseFactory.from(e);

        } catch (Exception e) {
            return ErrorResponseFactory.fromUnexpected(e);
        }
    }

    public ApiResponse breakDownTask(int id, String idempotencyKey) {

        try {
            List<Task> subtasks =
                    taskService.breakDownTask(id, idempotencyKey);

            List<String> response = subtasks.stream()
                    .map(Task::toString)
                    .toList();

            return new ApiResponse(201, response);

        } catch (ApiException e) {
            return ErrorResponseFactory.from(e);

        } catch (Exception e) {
            return ErrorResponseFactory.fromUnexpected(e);
        }
    }
}