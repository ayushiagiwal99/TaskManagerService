package com.taskManagerService.utils;

import com.taskManagerService.exception.ValidationException;

public final class InputValidator {
    public static void validateString(String value, String fieldName) {

        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    fieldName,
                    fieldName + " cannot be blank"
            );
        }
    }

    public static void validatePositiveNumber(int value, String fieldName) {

        if (value <= 0) {
            throw new ValidationException(
                    fieldName,
                    fieldName + " must be a positive number"
            );
        }
    }
}
