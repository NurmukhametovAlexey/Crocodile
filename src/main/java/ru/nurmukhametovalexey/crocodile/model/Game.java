package ru.nurmukhametovalexey.crocodile.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Game {
    String gameUUID;
    String word;
    LocalDateTime timeStarted;
    LocalDateTime timeFinished;
    GameStatus status;
}
