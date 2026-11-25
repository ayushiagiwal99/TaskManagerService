package com.taskManagerService.parser;

import com.taskManagerService.model.TaskRequest;
import java.util.List;

public interface BreakdownStrategy {
    List<TaskRequest> breakDown(String text);
}