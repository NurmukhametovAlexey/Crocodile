package ru.nurmukhametovalexey.crocodile.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameUser {
    private String login;
    private String gameUUID;
    private PlayerRole playerRole;
}
