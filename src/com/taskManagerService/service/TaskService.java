package com.taskManagerService.service;

import com.taskManagerService.exception.*;
import com.taskManagerService.model.Task;
import com.taskManagerService.model.TaskDraft;
import com.taskManagerService.enums.TaskPriority;
import com.taskManagerService.model.TaskRequest;
import com.taskManagerService.model.TaskQuery;
import com.taskManagerService.parser.BreakdownStrategy;
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
    private final BreakdownStrategy breakdownStrategy;

    public TaskService(
            TaskRepository taskRepository,
            TaskParser taskParser,
            BreakdownStrategy breakdownStrategy) {

        this.taskRepository = taskRepository;
        this.taskParser = taskParser;
        this.breakdownStrategy = breakdownStrategy;
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

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (task.getParentId() <= 0) {
            throw new ValidationException(
                    "Parent ID",
                    "Cannot delete a subtask independently"
            );
        }

        List<Task> subtasks = taskRepository.findAll()
                .stream()
                .filter(subtask -> subtask.getParentId() > 0
                        && subtask.getParentId() == id)
                .toList();

        for (Task subtask : subtasks) {
            taskRepository.delete(subtask.getId());
        }

        taskRepository.delete(id);

        return true;
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

    public Task createSubtask(int parentId, TaskRequest request) {

        Task parent = taskRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent task not found"));

        if (parent.getParentId() <= 0) {
            throw new ValidationException(
                    "Parent ID",
                    "A subtask cannot have its own subtasks"
            );
        }

        TaskDraft draft = validateAndCreateDraft(request);

        draft = new TaskDraft.Builder(
                draft.getTitle(),
                draft.getDescription(),
                draft.isCompleted())
                .dueDate(draft.getDueDate())
                .priority(draft.getPriority())
                .parentId(parentId)
                .build();

        return taskRepository.save(draft);
    }

    public List<Task> listSubtasks(int parentId) {

        Task parent = taskRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        return taskRepository.findAll()
                .stream()
                .filter(task -> task.getParentId() > 0
                        && task.getParentId() == parentId)
                .toList();
    }

    public List<Task> breakDownTask(int id, String idempotencyKey) {

        Task parent = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (parent.getParentId() <= 0) {
            throw new ValidationException(
                    "Parent ID",
                    "A subtask cannot be broken down"
            );
        }

        List<TaskRequest> requests =
                breakdownStrategy.breakDown(
                        parent.getTitle() + ": " + parent.getDescription()
                );

        if (requests == null || requests.isEmpty()) {
            throw new BadGatewayException(
                    "No subtasks were generated"
            );
        }

        // Validate every generated request before saving anything.
        List<TaskDraft> drafts = new ArrayList<>();

        for (TaskRequest request : requests) {

            try {
                TaskDraft draft = validateAndCreateDraft(request);

                TaskDraft subtaskDraft = new TaskDraft.Builder(
                    draft.getTitle(),
                    draft.getDescription(),
                    draft.isCompleted()
                )
                .dueDate(draft.getDueDate())
                .priority(draft.getPriority())
                .parentId(id)
                .build();

                drafts.add(subtaskDraft);

            } catch (ValidationException e) {
                throw new BadGatewayException(
                        "LLM generated invalid subtasks"
                );
            }
        }

        // Save only after all generated subtasks pass validation.
        List<Task> subtasks = new ArrayList<>();

        for (TaskDraft draft : drafts) {
            subtasks.add(taskRepository.save(draft));
        }

        return subtasks;
    }
}