package com.aladin.todo_api.todos;

public enum Status {
    PENDING,
    IN_PROGRESS,
    COMPLETED;

    public static Status fromString(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return PENDING;
        }
    }
}
