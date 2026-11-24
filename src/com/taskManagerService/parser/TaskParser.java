package com.taskManagerService.parser;

public interface TaskParser {

    ParsedTask parse(String text, String today);
}
