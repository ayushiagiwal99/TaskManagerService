package com.taskManagerService.service;

import com.taskManagerService.exception.*;
import com.taskManagerService.model.Task;
import com.taskManagerService.model.TaskDraft;
import com.taskManagerService.enums.TaskPriority;
import com.taskManagerService.api.TaskRequest;
import com.taskManagerService.api.TaskQuery;
import com.taskManagerService.parser.TaskParser;
import com.taskManagerService.parser.ParsedTask;
import com.taskManagerService.repository.TaskRepository;
import com.taskManagerService.utils.DateHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskParser taskParser;

    public TaskService(TaskRepository taskRepository, TaskParser taskParser) {
        this.taskRepository = taskRepository;
        this.taskParser = taskParser;
    }

    public Task createTask(TaskRequest request) {

        TaskDraft draft = validateAndCreateDraft(request);

        return taskRepository.save(draft);
    }

    public Task getTask(int id) {

        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }

    public List<Task> listTasks(TaskQuery query) {

        // Filtering and pagination will be implemented in Step 7.
        return taskRepository.findAll();
    }

    public Task updateTask(int id, TaskRequest request) {

        Task existingTask = getTask(id);

        TaskDraft draft = validateAndCreateDraft(request);

        Task updatedTask = Task.createUpdatedTask(existingTask, draft);

        return taskRepository.update(updatedTask);
    }

    public boolean deleteTask(int id) {

        Task task = getTask(id);

        // Delete subtasks along with their parent.
        List<Task> tasks = taskRepository.findAll();

        for (Task existingTask : tasks) {
            if (existingTask.getParentId() == id) {
                taskRepository.delete(existingTask.getId());
            }
        }

        return taskRepository.delete(task.getId());
    }

    public Task createTaskFromText(String text, String idempotencyKey) {

        String today = DateHelper.today();

        ParsedTask parsedTask;

        try {
            parsedTask = taskParser.parse(text, today);
        } catch (Exception e) {
            if (e instanceof ApiException apiException) {
                throw apiException;
            }

            throw new BadGatewayException("Failed to generate task using LLM");
        }

        TaskRequest request = new TaskRequest(
                parsedTask.getTitle(),
                parsedTask.getDescription(),
                "false",
                parsedTask.getDueDate(),
                "MEDIUM"
        );

        TaskDraft draft;

        try {
            draft = validateAndCreateDraft(request);
        } catch (ValidationException e) {
            throw new BadGatewayException("LLM returned an invalid task");
        }

        return taskRepository.save(draft);
    }

    private TaskDraft validateAndCreateDraft(TaskRequest request) {

        List<FieldIssue> issues = new ArrayList<>();

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            issues.add(new FieldIssue("title", "must not be empty"));
        }

        if (request.getDescription() == null
                || request.getDescription().isBlank()) {
            issues.add(new FieldIssue("description", "must not be empty"));
        }

        boolean completed = false;

        if ("true".equals(request.getCompleted())) {
            completed = true;
        } else if (!"false".equals(request.getCompleted())) {
            issues.add(new FieldIssue(
                    "completed",
                    "must be true or false"
            ));
        }

        String dueDate = request.getDueDate();

        if (dueDate != null && !dueDate.isBlank()) {
            try {
                LocalDate.parse(
                        dueDate,
                        DateTimeFormatter.ISO_LOCAL_DATE
                );
            } catch (DateTimeParseException exception) {
                issues.add(new FieldIssue(
                        "dueDate",
                        "must be a valid date in YYYY-MM-DD format"
                ));
            }
        }

        TaskPriority taskPriority = TaskPriority.MEDIUM;

        if (request.getPriority() != null
                && !request.getPriority().isBlank()) {
            try {
                taskPriority = TaskPriority.valueOf(request.getPriority());
            } catch (IllegalArgumentException exception) {
                issues.add(new FieldIssue(
                        "TaskPriority",
                        "must be LOW, MEDIUM or HIGH"
                ));
            }
        }

        if (!issues.isEmpty()) {
            throw new ValidationException(issues);
        }

        return new TaskDraft.Builder(
                request.getTitle(),
                request.getDescription(),
                completed
        )
                .dueDate(dueDate)
                .priority(taskPriority)
                .build();
    }
}