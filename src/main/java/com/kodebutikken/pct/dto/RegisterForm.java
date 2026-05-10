package com.kodebutikken.pct.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {

    @NotBlank(message = "Navn må ikke være tomt")
    @Size(min = 3, max = 50, message = "Navn skal være mellem 3 og 50 tegn")
    private String name;

    @NotBlank(message = "Email må ikke være tomt")
    @Email(message = "Email skal være en email")
    private String email;

    @NotBlank(message = "Password må ikke være tomt")
    @Size(min = 6, message = "Password skal være mindst 6 tegn")
    private String password;
}
