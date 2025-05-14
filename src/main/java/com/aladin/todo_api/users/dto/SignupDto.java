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
public class SignupDto {

    @NotNull
    private String userId;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String confirmPassword;

    @NotNull
    private String phoneNo;

    @NotNull
    private String email;
}
