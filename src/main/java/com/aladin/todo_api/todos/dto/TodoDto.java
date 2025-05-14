package com.aladin.todo_api.todos.dto;

import com.aladin.todo_api.todos.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class TodoDto {

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private String status;
}
