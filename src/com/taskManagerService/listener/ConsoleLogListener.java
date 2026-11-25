package com.taskManagerService.listener;

import com.taskManagerService.model.Task;

public class ConsoleLogListener implements TaskEventListener {

    @Override
    public void onEvent(String eventType, Task task) {
        System.out.println(
                "[Console Log] Event: " + eventType
                        + ", Task ID: " + task.getId()
                        + ", Title: " + task.getTitle()
        );
    }
}