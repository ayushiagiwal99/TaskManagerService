package com.taskManagerService.filters;

import java.util.List;

public class SortStrategyFactory {

    private static final List<SortStrategy> STRATEGIES = List.of(
            new CreatedAtAscSort(),
            new CreatedAtDescSort(),
            new DueDateAscSort(),
            new DueDateDescSort()
    );

    public static SortStrategy getStrategy(String sortBy, String order) {

        if (sortBy == null || order == null) {
            return null;
        }

        return STRATEGIES.stream()
                .filter(strategy ->
                        strategy.getSortBy().equalsIgnoreCase(sortBy)
                                && strategy.getOrder().equalsIgnoreCase(order))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported sorting: " + sortBy + " " + order
                ));
    }
}