package ru.nurmukhametovalexey.crocodile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Game {
    private String gameUUID;
    private SecretWord secretWord;
    private LocalDateTime timeStarted;
    private LocalDateTime timeFinished;
    private GameStatus status;
    private User painter;
    private List<User> guessers;

    public Game() {
        guessers = new ArrayList<>();
        timeStarted = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameUUID='" + gameUUID + '\'' +
                ", secretWord=" + secretWord +
                ", timeStarted=" + timeStarted +
                ", timeFinished=" + timeFinished +
                ", status=" + status +
                ", painter=" + painter +
                ", guessers=" + guessers +
                '}';
    }
}