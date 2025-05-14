package com.aladin.todo_api.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class PasswordDto {

    @NotNull
    private String password;
}
