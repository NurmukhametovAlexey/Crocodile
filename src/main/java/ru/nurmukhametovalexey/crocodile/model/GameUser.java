package ru.nurmukhametovalexey.crocodile.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameUser {
    private String gameUUID;
    private String login;
    private PlayerRole playerRole;
}
