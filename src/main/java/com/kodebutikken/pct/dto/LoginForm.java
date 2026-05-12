package com.kodebutikken.pct.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
    @NotBlank(message = "Email må ikke være tomt")
    @Email(message = "Email skal være en email")
    private String email;

    @NotBlank(message = "Password må ikke være tomt")
    private String password;
}
