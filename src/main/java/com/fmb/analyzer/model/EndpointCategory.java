package com.fmb.analyzer.model;

import java.util.Arrays;
import java.util.List;

/**
 * Enum for FMB Endpoint Categories
 */
public enum EndpointCategory {
    LOVs("List of Values", "Static reference data lookups"),
    POST_CHANGE("POST-CHANGE", "Post-change validation and processing"),
    VALIDATIONS("Validations", "Data validation rules and constraints"),
    CRUD_DIRECT("CRUD (Direct)", "Create, Read, Update, Delete - Direct operations"),
    CRUD_INDIRECT("CRUD (Indirect)", "Create, Read, Update, Delete - Indirect operations"),
    ACTIONS("Actions", "Custom actions and operations"),
    COMPLEX_CALCULATIONS("Complex (Key-next calculations)", "Key-next calculations and computations"),
    COMMIT_LOGIC("Complex (Commit logic)", "Commit transaction logic"),
    HISTORY_TRACKING("Complex (History)", "Historical tracking and audit trails");

    private final String displayName;
    private final String description;

    EndpointCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static EndpointCategory fromString(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.toUpperCase().trim().replaceAll("[\\s-]", "_");
        for (EndpointCategory category : EndpointCategory.values()) {
            if (category.name().equalsIgnoreCase(normalized)) {
                return category;
            }
        }
        return null;
    }

    public static List<EndpointCategory> getAllCategories() {
        return Arrays.asList(EndpointCategory.values());
    }
}
