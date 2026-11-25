package com.taskManagerService.api;

import com.taskManagerService.model.Task;
import com.taskManagerService.filters.TaskQuery;
import com.taskManagerService.model.TaskRequest;
import com.taskManagerService.repository.IdempotencyStore;
import com.taskManagerService.service.TaskService;
import com.taskManagerService.exception.ApiException;
import com.taskManagerService.exception.ConflictException;

import java.util.List;
import java.util.function.Supplier;

public class TaskApi {

    private final TaskService taskService;
    private final IdempotencyStore idempotencyStore =
            IdempotencyStore.getInstance();

    public TaskApi(TaskService taskService) {
        this.taskService = taskService;
    }

    public ApiResponse createTask(
            TaskRequest request,
            String idempotencyKey) {

        String fingerprint = fingerprintOf(
                "createTask",
                request.getTitle(),
                request.getDescription(),
                request.getCompleted(),
                request.getDueDate(),
                request.getPriority()
        );

        return executeIdempotently(
                idempotencyKey,
                fingerprint,
                () -> {
                    Task task = taskService.createTask(request);
                    return new ApiResponse(201, task);
                }
        );
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

    public ApiResponse listByPriority(String level) {

        try {
            List<Task> tasks = taskService.listByPriority(level);

            return new ApiResponse(200, tasks);

        } catch (ApiException exception) {
            return ErrorResponseFactory.from(exception);

        } catch (Exception exception) {
            return ErrorResponseFactory.fromUnexpected(exception);
        }
    }

    public ApiResponse createTaskFromText(
            String text,
            String idempotencyKey) {

        String fingerprint = fingerprintOf(
                "createTaskFromText",
                text
        );

        return executeIdempotently(
                idempotencyKey,
                fingerprint,
                () -> {
                    Task task = taskService.createTaskFromText(
                            text,
                            idempotencyKey
                    );

                    return new ApiResponse(201, task);
                }
        );
    }

    public ApiResponse createSubtask(int parentId, TaskRequest request) {

        try {
            Task task = taskService.createSubtask(parentId, request);

            return new ApiResponse(201, task.toString());

        } catch (ApiException exception) {
            return ErrorResponseFactory.from(exception);

        } catch (Exception exception) {
            return ErrorResponseFactory.fromUnexpected(exception);
        }
    }

    public ApiResponse listSubtasks(int parentId) {

        try {
            List<Task> subtasks = taskService.listSubtasks(parentId);

            List<String> response = subtasks.stream()
                    .map(Task::toString)
                    .toList();

            return new ApiResponse(200, response);

        } catch (ApiException exception) {
            return ErrorResponseFactory.from(exception);

        } catch (Exception exception) {
            return ErrorResponseFactory.fromUnexpected(exception);
        }
    }

    public ApiResponse breakDownTask(int id, String idempotencyKey) {

        String fingerprint = fingerprintOf(
                "breakDownTask",
                String.valueOf(id)
        );

        return executeIdempotently(
                idempotencyKey,
                fingerprint,
                () -> {
                    List<Task> subtasks =
                            taskService.breakDownTask(id, idempotencyKey);

                    List<String> response = subtasks.stream()
                            .map(Task::toString)
                            .toList();

                    return new ApiResponse(201, response);
                }
        );
    }

    private ApiResponse executeIdempotently(
            String key,
            String fingerprint,
            Supplier<ApiResponse> operation) {

        try {
            // Null key means normal execution without idempotency.
            if (key == null) {
                return operation.get();
            }

            synchronized (idempotencyStore) {

                IdempotencyStore.Record existing =
                        idempotencyStore.find(key);

                if (existing != null) {

                    if (existing.getFingerprint().equals(fingerprint)) {
                        // Same key and same request: return stored response.
                        return existing.getResponse();
                    }

                    // Same key but different request: return 422.
                    throw new ConflictException(
                            "Idempotency key reused with a different request"
                    );
                }

                // Execute the operation for a new key.
                ApiResponse response = operation.get();

                // Store only successful responses.
                if (response.getStatus() >= 200
                        && response.getStatus() < 300) {

                    idempotencyStore.save(
                            key,
                            fingerprint,
                            response
                    );
                }

                return response;
            }

        } catch (ApiException exception) {
            return ErrorResponseFactory.from(exception);

        } catch (Exception exception) {
            return ErrorResponseFactory.fromUnexpected(exception);
        }
    }

    private String fingerprintOf(String operation, String... values) {

        StringBuilder fingerprint = new StringBuilder();

        fingerprint.append(operation).append(";");

        for (String value : values) {

            if (value == null) {
                fingerprint.append("-1:");
            } else {
                fingerprint.append(value.length())
                        .append(":")
                        .append(value);
            }

            fingerprint.append(";");
        }

        return fingerprint.toString();
    }
}