package ru.nurmukhametovalexey.crocodile.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Dictionary {
    private String word;
    private Integer difficulty;
}
