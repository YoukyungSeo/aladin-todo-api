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
public class ModifyDto {

    private String username;

    @NotNull
    private String password;

    private String newPassword;

    private String phoneNo;

    private String email;
}
