package com.taskManagerService.parser;

import com.taskManagerService.model.TaskRequest;
import com.taskManagerService.exception.BadGatewayException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LlmBreakdownStrategy implements BreakdownStrategy {

    private final LlmClient llmClient;

    public LlmBreakdownStrategy(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    @Override
    public List<TaskRequest> breakDown(String text) {

        String prompt = """
                Break down the following task into smaller, actionable subtasks.

                Return ONLY the subtasks in the following format:
                title|description

                Each subtask must be on a separate line.
                Do not include numbering, explanations, or code fences.
                Generate at least one subtask.

                Task: %s
                """.formatted(text);

        try {
            String response = llmClient.send(prompt);

            List<TaskRequest> requests = new ArrayList<>();

            for (String line : response.split("\\R")) {

                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|", 2);

                if (parts.length != 2) {
                    throw new BadGatewayException(
                            "LLM returned an invalid subtask format"
                    );
                }

                requests.add(new TaskRequest(
                        parts[0].trim(),
                        parts[1].trim(),
                        "false",
                        null,
                        "MEDIUM"
                ));
            }

            if (requests.isEmpty()) {
                throw new BadGatewayException(
                        "LLM did not generate any subtasks"
                );
            }

            return requests;

        } catch (IOException e) {
            throw new BadGatewayException(
                    "Failed to generate subtasks using LLM"
            );
        }
    }
}