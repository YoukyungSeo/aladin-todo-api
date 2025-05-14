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
public class LoginDto {

    @NotNull
    private String userId;

    @NotNull
    private String password;
}
