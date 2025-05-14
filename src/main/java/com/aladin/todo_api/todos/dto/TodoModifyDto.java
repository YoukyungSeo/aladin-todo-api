package com.aladin.todo_api.todos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class TodoModifyDto {

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private String status;
}
