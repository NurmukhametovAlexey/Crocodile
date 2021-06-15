package ru.nurmukhametovalexey.crocodile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer userId;

    @NotBlank(message = "Enter login")
    @Size(min = 2, max = 20, message = "Login should consist of 2 to 20 symbols")
    private String login;

    @NotBlank(message = "Enter password")
    @Size(min = 5, max = 20, message = "Password should consist of 5 to 20 symbols")
    private String password;

    @NotBlank(message = "Enter email")
    @Email(message = "Invalid email")
    private String email;

    private String role;

    @PositiveOrZero
    private Integer score;

    private Boolean enabled;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", score=" + score +
                ", enabled=" + enabled +
                '}';
    }
}
