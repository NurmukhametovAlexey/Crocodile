package ru.nurmukhametovalexey.crocodile.controller.dto;

import lombok.*;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;
import ru.nurmukhametovalexey.crocodile.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConnectRequest {
    String gameUUID;
    PlayerRole playerRole;
}
