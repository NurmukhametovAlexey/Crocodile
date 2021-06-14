package ru.nurmukhametovalexey.crocodile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SecretWord {
    private String word;
    private int difficulty;
}
