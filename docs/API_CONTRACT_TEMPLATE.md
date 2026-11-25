# API_CONTRACT.md — Task Manager Service

> This document defines the contract for the Task Manager Service. It describes the public `TaskApi` operations, their inputs, success responses, and error responses.
>
> If the service is later exposed over HTTP, this contract can be used as the basis for an OpenAPI specification.

---

## Conventions

- Every `TaskApi` method returns an `ApiResponse`: a numeric **status** and a JSON-style **body**.
- Success bodies are a task object, a list of task objects, or a small confirmation object.
- Every failure body uses the single error shape in **Errors** below. `code` and `message` are always strings.
- `idempotencyKey` is optional (may be `null`). See each operation for how it behaves.
- Task IDs are assigned by the repository.
- Dates use the `YYYY-MM-DD` format.
- Priority values are `LOW`, `MEDIUM`, or `HIGH`.
- Unless specified otherwise, optional filters are not applied when omitted.

---

# Operations

## createTask

| | |
|---|---|
| **Java method** | `ApiResponse createTask(TaskRequest request, String idempotencyKey)` |
| **Purpose** | Create one task from structured input |
| **Success** | `201` — the created task |
| **Errors** | `400` VALIDATION_ERROR · `422` CONFLICT (key reused with a different request) · `500` INTERNAL_ERROR |

### Parameters

| Name | Type | Required | Rules |
|---|---|---|---|
| `title` | String | yes | Not empty or blank |
| `description` | String | yes | Not empty or blank |
| `completed` | String | yes | Exactly `"true"` or `"false"` |
| `dueDate` | String | no | A real calendar date, `YYYY-MM-DD` (`2026-02-30` is rejected) |
| `priority` | String | no | `LOW`, `MEDIUM` or `HIGH`; defaults to `MEDIUM` |
| `idempotencyKey` | String | no | Same key + same request returns the original response; nothing new is created |

### Example request

```text
title = "Pay rent"
description = "Monthly rent"
completed = "false"
dueDate = "2026-12-01"
priority = "HIGH"
idempotencyKey = "rent-001"
```

### Example success response

```json
{
  "status": 201,
  "body": {
    "id": 1,
    "title": "Pay rent",
    "description": "Monthly rent",
    "completed": false,
    "dueDate": "2026-12-01",
    "priority": "HIGH",
    "parentId": null,
    "createdAt": 1788000000000
  }
}
```

### Example error response (`title = ""`)

```json
{
  "status": 400,
  "body": {
    "error": {
      "code": "VALIDATION_ERROR",
      "message": "Validation failed",
      "details": [
        {
          "field": "title",
          "issue": "must not be empty"
        }
      ]
    }
  }
}
```

---

## getTask

| | |
|---|---|
| **Java method** | `ApiResponse getTask(int id)` |
| **Purpose** | Retrieve a task using its ID |
| **Success** | `200` — the requested task |
| **Errors** | `404` NOT_FOUND · `500` INTERNAL_ERROR |

### Parameters

| Name | Type | Required | Rules |
|---|---|---|---|
| `id` | int | yes | Must identify an existing task |

### Example request

```text
id = 1
```

### Example success response

```json
{
  "status": 200,
  "body": {
    "id": 1,
    "title": "Pay rent",
    "description": "Monthly rent",
    "completed": false,
    "dueDate": "2026-12-01",
    "priority": "HIGH",
    "parentId": null,
    "createdAt": 1788000000000
  }
}
```

### Example error response (`id = 99` does not exist)

```json
{
  "status": 404,
  "body": {
    "error": {
      "code": "NOT_FOUND",
      "message": "Task not found",
      "details": []
    }
  }
}
```

---

## listTasks

| | |
|---|---|
| **Java method** | `ApiResponse listTasks(TaskQuery query)` |
| **Purpose** | Retrieve tasks with optional filtering, searching, sorting, and pagination |
| **Success** | `200` — a list of matching tasks |
| **Errors** | `400` VALIDATION_ERROR · `500` INTERNAL_ERROR |

### Parameters

| Name | Type | Required | Rules |
|---|---|---|---|
| `completed` | Boolean | no | `true` returns completed tasks; `false` returns open tasks |
| `priority` | Priority | no | Filter by `LOW`, `MEDIUM`, or `HIGH` |
| `search` | String | no | Case-insensitive search in task title and description |
| `sortBy` | String | no | `createdAt` or `dueDate` |
| `order` | String | no | `ASC` or `DESC`; used with `sortBy` |
| `page` | int | no | Page number starting from 1; defaults to `1` |
| `pageSize` | int | no | Number of tasks per page; defaults to `10` |

**Notes:**
- All filters can be combined in one query.
- Sorting is optional. When specified, both `sortBy` and `order` must be valid.
- Pagination is applied after filtering and sorting.
- Page numbers and page sizes must be positive integers.

### Example request

```text
TaskQuery.builder()
    .completed(false)
    .priority(HIGH)
    .search("presentation")
    .sortBy("dueDate")
    .order("ASC")
    .page(1)
    .pageSize(10)
    .build()
```

### Example success response

```json
{
  "status": 200,
  "body": [
    {
      "id": 1,
      "title": "Prepare presentation",
      "description": "Prepare the project presentation",
      "completed": false,
      "dueDate": "2026-12-01",
      "priority": "HIGH",
      "parentId": null,
      "createdAt": 1788000000000
    },
    {
      "id": 2,
      "title": "Review presentation",
      "description": "Review the presentation slides",
      "completed": false,
      "dueDate": "2026-12-05",
      "priority": "HIGH",
      "parentId": null,
      "createdAt": 1788000001000
    }
  ]
}
```

### Example error response (invalid sort order)

```json
{
  "status": 400,
  "body": {
    "error": {
      "code": "VALIDATION_ERROR",
      "message": "Validation failed",
      "details": [
        {
          "field": "order",
          "issue": "must be ASC or DESC"
        }
      ]
    }
  }
}
```

---

## updateTask

| | |
|---|---|
| **Java method** | `ApiResponse updateTask(int id, TaskRequest request)` |
| **Purpose** | Update an existing task |
| **Success** | `200` — the updated task |
| **Errors** | `400` VALIDATION_ERROR · `404` NOT_FOUND · `500` INTERNAL_ERROR |

### Parameters

| Name | Type | Required | Rules |
|---|---|---|---|
| `id` | int | yes | Must identify an existing task |
| `title` | String | yes | Not empty or blank |
| `description` | String | yes | Not empty or blank |
| `completed` | String | yes | Exactly `"true"` or `"false"` |
| `dueDate` | String | no | A real calendar date in `YYYY-MM-DD` format |
| `priority` | String | no | `LOW`, `MEDIUM` or `HIGH`; defaults to `MEDIUM` |

**Notes:**
- The task ID remains unchanged.
- The original `createdAt` value is preserved.
- The task's `parentId` remains unchanged.

### Example request

```text
id = 1

title = "Pay rent"
description = "Pay December rent"
completed = "true"
dueDate = "2026-12-01"
priority = "HIGH"
```

### Example success response

```json
{
  "status": 200,
  "body": {
    "id": 1,
    "title": "Pay rent",
    "description": "Pay December rent",
    "completed": true,
    "dueDate": "2026-12-01",
    "priority": "HIGH",
    "parentId": null,
    "createdAt": 1788000000000
  }
}
```

### Example error response (`id = 99` does not exist)

```json
{
  "status": 404,
  "body": {
    "error": {
      "code": "NOT_FOUND",
      "message": "Task not found",
      "details": []
    }
  }
}
```

---

## deleteTask

| | |
|---|---|
| **Java method** | `ApiResponse deleteTask(int id)` |
| **Purpose** | Delete a task and all its subtasks |
| **Success** | `200` — a confirmation object |
| **Errors** | `404` NOT_FOUND · `500` INTERNAL_ERROR |

### Parameters

| Name | Type | Required | Rules |
|---|---|---|---|
| `id` | int | yes | Must identify an existing task |

**Notes:**
- Deleting a parent task also deletes all its subtasks.
- The operation returns a confirmation after successful deletion.

### Example request

```text
id = 1
```

### Example success response

```json
{
  "status": 200,
  "body": {
    "message": "Task and its subtasks deleted successfully"
  }
}
```

### Example error response (`id = 99` does not exist)

```json
{
  "status": 404,
  "body": {
    "error": {
      "code": "NOT_FOUND",
      "message": "Task not found",
      "details": []
    }
  }
}
```

---

## createTaskFromText

| | |
|---|---|
| **Java method** | `ApiResponse createTaskFromText(String text, String idempotencyKey)` |
| **Purpose** | Create a task from natural-language input using a task parser |
| **Success** | `201` — the created task |
| **Errors** | `400` VALIDATION_ERROR · `422` CONFLICT · `502` BAD_GATEWAY · `503` SERVICE_UNAVAILABLE · `500` INTERNAL_ERROR |

### Parameters

| Name | Type | Required | Rules |
|---|---|---|---|
| `text` | String | yes | Natural-language description of the task; must not be empty or blank |
| `idempotencyKey` | String | no | Same key + same text returns the original response; the parser is not called again |

**Notes:**
- The parser extracts the task title, description, and due date from the supplied text.
- The current date is provided to the parser to help interpret relative dates such as "tomorrow".
- The parsed result is converted into a `TaskRequest` and validated using the same rules as `createTask`.
- If the parsed output is invalid, no task is saved.
- If the LLM call fails or times out, no task is saved.
- If `LLM_API_KEY` is missing when the LLM parser is selected, the operation returns `503`.
- A rule-based parser can be used for offline operation.

### Example request

```text
text = "Remind me to pay rent on December 1st with high priority"
idempotencyKey = "rent-text-001"
```

### Example success response

```json
{
  "status": 201,
  "body": {
    "id": 3,
    "title": "Pay rent",
    "description": "Pay rent",
    "completed": false,
    "dueDate": "2026-12-01",
    "priority": "HIGH",
    "parentId": null,
    "createdAt": 1788000002000
  }
}
```

### Example error response (LLM API key is missing)

```json
{
  "status": 503,
  "body": {
    "error": {
      "code": "SERVICE_UNAVAILABLE",
      "message": "LLM service is unavailable",
      "details": []
    }
  }
}
```

---

## createSubtask

| | |
|---|---|
| **Java method** | `ApiResponse createSubtask(int parentId, TaskRequest request)` |
| **Purpose** | Create a subtask associated