package com.taskManagerService;

import com.taskManagerService.api.ApiResponse;
import com.taskManagerService.api.TaskApi;
import com.taskManagerService.model.TaskRequest;
import com.taskManagerService.parser.RuleBasedTaskParser;
import com.taskManagerService.parser.RuleBasedBreakdownStrategy;
import com.taskManagerService.repository.InMemoryTaskRepository;
import com.taskManagerService.service.TaskService;

public class Main {

    private static TaskApi taskApi;

    public static void main(String[] args) {

        // Initialize dependencies
        InMemoryTaskRepository repository =
                InMemoryTaskRepository.getInstance();

        RuleBasedTaskParser taskParser =
                new RuleBasedTaskParser();

        RuleBasedBreakdownStrategy breakdownStrategy =
                new RuleBasedBreakdownStrategy();

        TaskService taskService = new TaskService(
                repository,
                taskParser,
                breakdownStrategy
        );

        taskApi = new TaskApi(taskService);

        System.out.println("========================================");
        System.out.println("       TASK MANAGER SERVICE TESTS");
        System.out.println("========================================");

        testCreateTask();
        testGetTask();
        testListTasks();
        testUpdateTask();

        testValidation();

        testNotFound();

        testCreateTaskFromText();

        testCreateSubtask();
        testListSubtasks();
        testBreakDownTask();
        testSubtaskValidation();

        testIdempotency();

        testDeleteTask();

        System.out.println("\n========================================");
        System.out.println("       ALL TEST SCENARIOS EXECUTED");
        System.out.println("========================================");
    }

    // --------------------------------------------------
    // STEP 2: CRUD OPERATIONS
    // --------------------------------------------------

    private static void testCreateTask() {

        printSection("STEP 2 - CREATE TASK");

        TaskRequest request = new TaskRequest(
                "Learn Java",
                "Complete Core Java concepts",
                "false",
                "2026-12-10",
                "HIGH"
        );

        printResponse(
                "Create valid task",
                taskApi.createTask(request, null)
        );
    }

    private static void testGetTask() {

        printSection("GET TASK");

        printResponse(
                "Get existing task",
                taskApi.getTask(1)
        );
    }

    private static void testListTasks() {

        printSection("LIST TASKS");

        printResponse(
                "List all tasks",
                taskApi.listTasks(null)
        );
    }

    private static void testUpdateTask() {

        printSection("UPDATE TASK");

        TaskRequest request = new TaskRequest(
                "Learn Java",
                "Complete Core Java, OOP and Collections",
                "true",
                "2026-12-10",
                "HIGH"
        );

        printResponse(
                "Update existing task",
                taskApi.updateTask(1, request)
        );
    }

    private static void testDeleteTask() {

        printSection("DELETE TASK");

        printResponse(
                "Delete parent task",
                taskApi.deleteTask(1)
        );

        printResponse(
                "Verify deleted parent",
                taskApi.getTask(1)
        );

        printResponse(
                "Verify deleted subtasks",
                taskApi.listSubtasks(1)
        );
    }

    // --------------------------------------------------
    // STEP 3: VALIDATION AND ERROR HANDLING
    // --------------------------------------------------

    private static void testValidation() {

        printSection("STEP 3 - VALIDATION");

        // Empty title
        TaskRequest emptyTitle = new TaskRequest(
                "",
                "Description",
                "false",
                "2026-12-10",
                "MEDIUM"
        );

        printResponse(
                "Empty title",
                taskApi.createTask(emptyTitle, null)
        );

        // Empty description
        TaskRequest emptyDescription = new TaskRequest(
                "Valid title",
                "",
                "false",
                "2026-12-10",
                "MEDIUM"
        );

        printResponse(
                "Empty description",
                taskApi.createTask(emptyDescription, null)
        );

        // Invalid completed value
        TaskRequest invalidCompleted = new TaskRequest(
                "Test task",
                "Test completed validation",
                "yes",
                "2026-12-10",
                "MEDIUM"
        );

        printResponse(
                "Invalid completed value",
                taskApi.createTask(invalidCompleted, null)
        );

        // Invalid date format
        TaskRequest invalidDateFormat = new TaskRequest(
                "Test task",
                "Test date validation",
                "false",
                "10-12-2026",
                "MEDIUM"
        );

        printResponse(
                "Invalid date format",
                taskApi.createTask(invalidDateFormat, null)
        );

        // Impossible date
        TaskRequest impossibleDate = new TaskRequest(
                "Test task",
                "Test impossible date",
                "false",
                "2026-02-30",
                "MEDIUM"
        );

        printResponse(
                "Impossible date",
                taskApi.createTask(impossibleDate, null)
        );

        // Invalid priority
        TaskRequest invalidPriority = new TaskRequest(
                "Test task",
                "Test priority validation",
                "false",
                "2026-12-10",
                "URGENT"
        );

        printResponse(
                "Invalid priority",
                taskApi.createTask(invalidPriority, null)
        );
    }

    private static void testNotFound() {

        printSection("NOT FOUND SCENARIOS");

        printResponse(
                "Get unknown task",
                taskApi.getTask(9999)
        );

        TaskRequest request = new TaskRequest(
                "Updated task",
                "Trying to update unknown task",
                "false",
                "2026-12-10",
                "LOW"
        );

        printResponse(
                "Update unknown task",
                taskApi.updateTask(9999, request)
        );

        printResponse(
                "Delete unknown task",
                taskApi.deleteTask(9999)
        );
    }

    // --------------------------------------------------
    // STEP 4: CREATE TASK FROM NATURAL LANGUAGE
    // --------------------------------------------------

    private static void testCreateTaskFromText() {

        printSection("STEP 4 - CREATE TASK FROM TEXT");

        printResponse(
                "Create task from natural language",
                taskApi.createTaskFromText(
                        "Remind me to pay rent on December 1st with high priority",
                        null
                )
        );

        printResponse(
                "Create another task from natural language",
                taskApi.createTaskFromText(
                        "Complete project documentation by December 15",
                        null
                )
        );
    }

    // --------------------------------------------------
    // STEP 5: SUBTASKS AND BREAKDOWN
    // --------------------------------------------------

    private static void testCreateSubtask() {

        printSection("STEP 5 - CREATE SUBTASK");

        TaskRequest subtaskRequest = new TaskRequest(
                "Study inheritance",
                "Understand inheritance in Java",
                "false",
                "2026-12-05",
                "MEDIUM"
        );

        printResponse(
                "Create subtask under task 1",
                taskApi.createSubtask(1, subtaskRequest)
        );
    }

    private static void testListSubtasks() {

        printSection("LIST SUBTASKS");

        printResponse(
                "List subtasks of task 1",
                taskApi.listSubtasks(1)
        );
    }

    private static void testBreakDownTask() {

        printSection("BREAK DOWN TASK");

        printResponse(
                "Break down task 1",
                taskApi.breakDownTask(1, null)
        );

        printResponse(
                "List subtasks after breakdown",
                taskApi.listSubtasks(1)
        );
    }

    private static void testSubtaskValidation() {

        printSection("SUBTASK VALIDATION");

        TaskRequest request = new TaskRequest(
                "Nested subtask",
                "This should not be allowed",
                "false",
                "2026-12-10",
                "LOW"
        );

        // Unknown parent
        printResponse(
                "Create subtask under unknown parent",
                taskApi.createSubtask(9999, request)
        );

        // Try breaking down a subtask.
        // Assumes task 1 has at least one subtask.
        // Replace 2 with the actual subtask ID if necessary.
        printResponse(
                "Break down a subtask",
                taskApi.breakDownTask(2, null)
        );
    }

    // --------------------------------------------------
    // STEP 6: IDEMPOTENCY
    // --------------------------------------------------

    private static void testIdempotency() {

        printSection("STEP 6 - IDEMPOTENCY");

        TaskRequest request = new TaskRequest(
                "Prepare presentation",
                "Prepare project presentation",
                "false",
                "2026-12-15",
                "HIGH"
        );

        // First request
        printResponse(
                "First request",
                taskApi.createTask(request, "presentation-001")
        );

        // Same key and same request
        printResponse(
                "Repeat same request with same key",
                taskApi.createTask(request, "presentation-001")
        );

        // Same key but different request
        TaskRequest differentRequest = new TaskRequest(
                "Prepare report",
                "Prepare a different report",
                "false",
                "2026-12-20",
                "LOW"
        );

        printResponse(
                "Same key with different request",
                taskApi.createTask(differentRequest, "presentation-001")
        );

        // Invalid request should not reserve the key.
        TaskRequest invalidRequest = new TaskRequest(
                "",
                "Invalid request",
                "false",
                "2026-12-20",
                "LOW"
        );

        printResponse(
                "Invalid request with key",
                taskApi.createTask(invalidRequest, "retry-001")
        );

        TaskRequest correctedRequest = new TaskRequest(
                "Corrected task",
                "Retry after correcting validation error",
                "false",
                "2026-12-20",
                "LOW"
        );

        printResponse(
                "Retry same key with corrected request",
                taskApi.createTask(correctedRequest, "retry-001")
        );

        // Idempotency for natural-language task creation
        printResponse(
                "First text creation request",
                taskApi.createTaskFromText(
                        "Remind me to submit assignment by December 20",
                        "text-task-001"
                )
        );

        printResponse(
                "Repeat text creation request",
                taskApi.createTaskFromText(
                        "Remind me to submit assignment by December 20",
                        "text-task-001"
                )
        );
    }

    // --------------------------------------------------
    // COMMON HELPERS
    // --------------------------------------------------

    private static void printSection(String title) {

        System.out.println("\n\n----------------------------------------");
        System.out.println(title);
        System.out.println("----------------------------------------");
    }

    private static void printResponse(
            String scenario,
            ApiResponse response
    ) {

        System.out.println("\nScenario: " + scenario);
        System.out.println(response);
    }
}