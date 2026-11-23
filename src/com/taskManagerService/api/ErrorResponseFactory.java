package com.taskManagerService.api;

import com.taskManagerService.exception.ApiException;
import com.taskManagerService.exception.BadGatewayException;
import com.taskManagerService.exception.FieldIssue;
import com.taskManagerService.exception.ValidationException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ErrorResponseFactory {

    public static ApiResponse from(ApiException exception) {

        Map<String, Object> error = new LinkedHashMap<>();

        error.put("code", exception.getCode());
        error.put("message", exception.getMessage());

        List<Map<String, String>> details = new ArrayList<>();

        if (exception instanceof ValidationException validationException) {

            addFieldIssues(details, validationException.getIssues());

        } else if (exception instanceof BadGatewayException badGatewayException) {

            addFieldIssues(details, badGatewayException.getIssues());
        }

        error.put("details", details);

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("error", error);

        return new ApiResponse(exception.getStatus(), responseBody);
    }

    public static ApiResponse fromUnexpected(Exception exception) {

        Map<String, Object> error = new LinkedHashMap<>();

        error.put("code", "INTERNAL_ERROR");
        error.put("message", "An unexpected error occurred");
        error.put("details", new ArrayList<>());

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("error", error);

        return new ApiResponse(500, responseBody);
    }

    private static void addFieldIssues(
            List<Map<String, String>> details,
            List<FieldIssue> issues) {

        for (FieldIssue issue : issues) {

            Map<String, String> fieldDetail = new LinkedHashMap<>();

            fieldDetail.put("field", issue.getField());
            fieldDetail.put("issue", issue.getIssue());

            details.add(fieldDetail);
        }
    }
}