package com.taskManagerService.parser;

import java.time.LocalDate;

public class RuleBasedTaskParser implements TaskParser {

    @Override
    public ParsedTask parse(String text, String today) {

        String title = text.trim();
        String description = text.trim();
        String dueDate = null;
        String recurrence = null;

        LocalDate currentDate = LocalDate.parse(today);

        if (text.toLowerCase().contains("tomorrow")) {
            dueDate = currentDate.plusDays(1).toString();
        }

        if (text.toLowerCase().contains("every month")) {
            recurrence = "monthly";
        }

        if (text.toLowerCase().contains("1st")) {
            recurrence = "monthly";

            LocalDate firstDay = currentDate.withDayOfMonth(1);

            if (!firstDay.isAfter(currentDate)) {
                firstDay = firstDay.plusMonths(1);
            }

            dueDate = firstDay.toString();
        }

        return new ParsedTask(
                title,
                description,
                dueDate,
                recurrence
        );
    }
}