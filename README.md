## 2. Project Structure

The project follows a layered package structure, separating API handling, business logic, data access, parsing, filtering, and supporting components.

```text
TaskManagerService/
│
├── docs/
│   ├── API_CONTRACT_TEMPLATE.md
│   ├── LLM_API_CHEATSHEET.md
│   └── PROBLEM_STATEMENT.md
│
├── src/
│   └── com/
│       └── taskManagerService/
│           │
│           ├── Main.java
│           │
│           ├── api/
│           │   ├── ApiResponse.java
│           │   ├── ErrorResponseFactory.java
│           │   └── TaskApi.java
│           │
│           ├── app/
│           │   ├── LlmClientSelfCheck.java
│           │   ├── LlmSmokeTest.java
│           │   ├── PromptAndEnvWalkthrough.java
│           │   └── TestRunner.java
│           │
│           ├── enums/
│           │   └── TaskPriority.java
│           │
│           ├── exception/
│           │   ├── ApiException.java
│           │   ├── BadGatewayException.java
│           │   ├── ConflictException.java
│           │   ├── FieldIssue.java
│           │   ├── NotFoundException.java
│           │   ├── ServiceUnavailableException.java
│           │   └── ValidationException.java
│           │
│           ├── filters/
│           │   ├── CompletedFilter.java
│           │   ├── CreatedAtAscSort.java
│           │   ├── CreatedAtDescSort.java
│           │   ├── DueDateAscSort.java
│           │   ├── DueDateDescSort.java
│           │   ├── PriorityFilter.java
│           │   ├── SearchFilter.java
│           │   ├── SortStrategy.java
│           │   ├── SortStrategyFactory.java
│           │   ├── TaskFilter.java
│           │   └── TaskQuery.java
│           │
│           ├── listener/
│           │   ├── AuditTrailListener.java
│           │   ├── ConsoleLogListener.java
│           │   └── TaskEventListener.java
│           │
│           ├── model/
│           │   ├── Task.java
│           │   ├── TaskDraft.java
│           │   └── TaskRequest.java
│           │
│           ├── parser/
│           │   ├── BreakdownStrategy.java
│           │   ├── HttpLlmClient.java
│           │   ├── LlmBreakdownStrategy.java
│           │   ├── LlmClient.java
│           │   ├── LlmTaskParser.java
│           │   ├── ParsedTask.java
│           │   ├── ParserFactory.java
│           │   ├── RuleBasedBreakdownStrategy.java
│           │   ├── RuleBasedTaskParser.java
│           │   ├── SimpleJson.java
│           │   └── TaskParser.java
│           │
│           ├── primer/
│           │   └── ListMapEnumPrimer.java
│           │
│           ├── repository/
│           │   ├── IdempotencyFlowOutline.java
│           │   ├── IdempotencyStore.java
│           │   ├── InMemoryTaskRepository.java
│           │   └── TaskRepository.java
│           │
│           ├── service/
│           │   └── TaskService.java
│           │
│           └── utils/
│               ├── DateHelper.java
│               └── InputValidator.java
│
├── .gitignore
├── README.md
└── TaskManagerService.iml
```

### Package Responsibilities

| Package | Responsibility |
|---|---|
| `api` | Exposes task operations through `TaskApi`, represents API responses, and handles error-response conversion. |
| `app` | Contains standalone application utilities and test programs for LLM configuration, smoke testing, and test execution. |
| `enums` | Defines task-related enumerations, including task priority. |
| `exception` | Contains custom API exceptions and validation error details. |
| `filters` | Implements task filtering, sorting strategies, query construction, and sort strategy selection. |
| `listener` | Implements the Observer pattern for task lifecycle notifications and audit logging. |
| `model` | Contains task domain objects, task drafts, and API request models. |
| `parser` | Handles natural-language task parsing, LLM integration, rule-based parsing, and task breakdown strategies. |
| `primer` | Contains standalone Java examples demonstrating collections and enum concepts. |
| `repository` | Defines task persistence operations, in-memory storage, and idempotency management. |
| `service` | Contains task business logic and coordinates repository, parser, filtering, and event notifications. |
| `utils` | Provides reusable validation and date-handling utilities. |

## 3. Prerequisites

- JDK 17 or later.
- IntelliJ IDEA or another Java IDE (optional).
- An LLM API key for LLM-based functionality.
- No external Java libraries are required for the core application.

## 4. Setup and Execution

### Step 1: Verify Java installation

```bash
java -version
javac -version
```

### Step 2: Compile the project

Run the following commands from the project root.

**Windows PowerShell:**

```powershell
New-Item -ItemType Directory -Force -Path out

$files = Get-ChildItem -Path src -Recurse -Filter *.java |
         ForEach-Object { $_.FullName }

javac -d out $files
```

**Linux/macOS:**

```bash
mkdir -p out

find src -name "*.java" > sources.txt

javac -d out @sources.txt
```

### Step 3: Run the application

The main application entry point is `Main.java`, located directly inside `com.taskManagerService`.

```bash
java -cp out com.taskManagerService.Main
```

The `Main` class initializes the application components and executes the available scenarios.

### Step 4: Configure the LLM API key

The application reads the API key from the `LLM_API_KEY` environment variable.

**Windows PowerShell:**

```powershell
$env:LLM_API_KEY="your_actual_api_key"
```

**Linux/macOS:**

```bash
export LLM_API_KEY="your_actual_api_key"
```

To verify that the environment variable is configured:

```powershell
if ($env:LLM_API_KEY) {
    Write-Host "LLM API key is configured"
} else {
    Write-Host "LLM API key is missing"
}
```

After setting the variable, run the application from the same terminal.

Alternatively, in IntelliJ IDEA:

1. Open **Run → Edit Configurations**.
2. Select the `Main` run configuration.
3. Add `LLM_API_KEY=your_actual_api_key` under Environment variables.
4. Apply and run.

> **Note:** Configuring the environment variable does not automatically select an LLM implementation. The application must be configured to use `LlmTaskParser` or `LlmBreakdownStrategy`. The rule-based implementations can be used without an API key.