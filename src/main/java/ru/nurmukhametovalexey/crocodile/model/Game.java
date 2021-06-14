package ru.nurmukhametovalexey.crocodile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    private String gameUUID;
    private String secretWord;
    private LocalDateTime timeStarted;
    private LocalDateTime timeFinished;
    private GameStatus status;
}