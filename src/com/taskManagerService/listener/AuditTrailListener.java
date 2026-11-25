package com.taskManagerService.listener;

import com.taskManagerService.model.Task;

import java.util.HashMap;
import java.util.Map;

public class AuditTrailListener implements TaskEventListener {

    private final Map<String, Integer> eventCounts = new HashMap<>();

    @Override
    public void onEvent(String eventType, Task task) {
        eventCounts.put(
                eventType,
                eventCounts.getOrDefault(eventType, 0) + 1
        );

        System.out.println(
                "[Audit Trail] " + eventType
                        + " event recorded for Task ID: " + task.getId()
        );
    }

    public void printEventCounts() {
        System.out.println("\nAudit Trail Summary:");

        for (Map.Entry<String, Integer> entry : eventCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}