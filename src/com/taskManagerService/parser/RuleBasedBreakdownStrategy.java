package com.taskManagerService.parser;

import com.taskManagerService.model.TaskRequest;

import java.util.ArrayList;
import java.util.List;

public class RuleBasedBreakdownStrategy implements BreakdownStrategy {

    @Override
    public List<TaskRequest> breakDown(String text) {

        String[] parts = text.split("(?i)\\s+and\\s+|,");

        List<TaskRequest> requests = new ArrayList<>();

        for (String part : parts) {

            String title = part.trim();

            if (title.isEmpty()) {
                continue;
            }

            requests.add(new TaskRequest(
                    title,
                    title,
                    "false",
                    null,
                    "MEDIUM"
            ));
        }

        return requests;
    }
}