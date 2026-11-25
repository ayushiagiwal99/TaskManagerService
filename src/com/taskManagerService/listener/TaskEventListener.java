package com.taskManagerService.listener;

import com.taskManagerService.model.Task;

public interface TaskEventListener {
    void onEvent(String eventType, Task task);
}
