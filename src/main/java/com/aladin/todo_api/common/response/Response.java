package com.aladin.todo_api.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {
    private int status;
    private String message;
    private T data;
}
