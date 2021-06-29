package ru.nurmukhametovalexey.crocodile.model;

import lombok.*;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameHistory {
    String timeStarted;
    String timeFinished;
    String gameUUID;
    PlayerRole playerRole;
    Boolean win;
}
