package com.spring.userservice.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {
    @NotNull(message = "is Not null")
    @Email
    private String email;

    @NotNull(message = "is Not null")
    @Size(min = 2, message = "2글자를 넘겨야 됩니다.")
    private String name;

    @NotNull(message = "is Not null")
    @Size(min = 8, message = "8글자를 넘겨야 됩니다.")
    private String password;
}
