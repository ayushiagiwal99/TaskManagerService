package com.taskManagerService.parser;

import com.taskManagerService.exception.BadGatewayException;

import java.io.IOException;
import java.util.Map;

public class LlmTaskParser implements TaskParser {

    private final LlmClient llmClient;

    public LlmTaskParser(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    @Override
    public ParsedTask parse(String text, String today) {

        String prompt = "Today's date is " + today + " (format YYYY-MM-DD).\n"
                + "Turn the user's text into ONE task.\n"
                + "Reply with ONLY a JSON object, no explanation, no code fences, "
                + "with exactly these fields:\n"
                + "  \"title\": short string, required\n"
                + "  \"description\": string, required\n"
                + "  \"dueDate\": YYYY-MM-DD or null\n"
                + "  \"recurrence\": string such as \"monthly\", or null\n"
                + "User text: " + text;

        try {
            String response = llmClient.send(prompt);

            Map<String, String> fields = SimpleJson.parseFlat(response);

            return new ParsedTask(
                    fields.get("title"),
                    fields.get("description"),
                    fields.get("dueDate"),
                    fields.get("recurrence")
            );

        } catch (IOException | IllegalArgumentException e) {
            throw new BadGatewayException("Failed to parse task using LLM");
        }
    }
}