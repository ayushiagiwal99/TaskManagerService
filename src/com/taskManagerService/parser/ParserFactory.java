package com.taskManagerService.parser;

import com.taskManagerService.exception.ServiceUnavailableException;

public class ParserFactory {

    public static TaskParser createLlmParser() {

        String apiKey = System.getenv("LLM_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new ServiceUnavailableException("LLM_API_KEY is missing");
        }

        String provider = System.getenv("LLM_PROVIDER");
        String model = System.getenv("LLM_MODEL");

        LlmClient llmClient = new HttpLlmClient(
                apiKey,
                provider,
                model
        );

        return new LlmTaskParser(llmClient);
    }

    public static TaskParser createRuleBasedParser() {
        return new RuleBasedTaskParser();
    }
}