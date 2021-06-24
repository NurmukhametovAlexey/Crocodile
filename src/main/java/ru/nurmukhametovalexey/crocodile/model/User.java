package ru.nurmukhametovalexey.crocodile.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

    @NotBlank(message = "Enter login")
    @Size(min = 2, max = 20, message = "Login should consist of 2 to 20 symbols")
    private String login;

    @NotBlank(message = "Enter password")
    @Size(min = 5, max = 20, message = "Password should consist of 5 to 20 symbols")
    private transient String password;

    @NotBlank(message = "Enter email")
    @Email(message = "Invalid email")
    private String email;

    private String role;

    private Integer score;

    private Boolean enabled;

    public void increaseScore(int inc) {
        score += inc;
    }
}
