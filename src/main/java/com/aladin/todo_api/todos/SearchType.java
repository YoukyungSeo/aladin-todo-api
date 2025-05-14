package com.aladin.todo_api.todos;

public enum SearchType {
    ALL,
    TITLE,
    DESCRIPTION;

    public static SearchType fromString(String type) {
        if (type == null || type.trim().isEmpty()) {
            return ALL;
        }
        try {
            return SearchType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            return ALL;
        }
    }
}