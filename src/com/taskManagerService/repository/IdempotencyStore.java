package com.taskManagerService.repository;

import com.taskManagerService.api.ApiResponse;

import java.util.HashMap;
import java.util.Map;

public class IdempotencyStore {

    private static final IdempotencyStore INSTANCE =
            new IdempotencyStore();

    private final Map<String, Record> records = new HashMap<>();

    public static class Record {

        private final String fingerprint;
        private final ApiResponse response;

        public Record(String fingerprint, ApiResponse response) {
            this.fingerprint = fingerprint;
            this.response = response;
        }

        public String getFingerprint() {
            return fingerprint;
        }

        public ApiResponse getResponse() {
            return response;
        }
    }

    private IdempotencyStore() {
    }

    public static IdempotencyStore getInstance() {
        return INSTANCE;
    }

    public synchronized Record find(String key) {
        return records.get(key);
    }

    public synchronized void save(
            String key,
            String fingerprint,
            ApiResponse response) {

        records.put(key, new Record(fingerprint, response));
    }
}