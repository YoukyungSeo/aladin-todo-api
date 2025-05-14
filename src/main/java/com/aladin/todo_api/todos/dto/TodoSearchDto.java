package com.aladin.todo_api.todos.dto;

import com.aladin.todo_api.todos.SearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class TodoSearchDto {

    @NotNull
    private String searchType;

    @NotNull
    private String searchWord;
}
