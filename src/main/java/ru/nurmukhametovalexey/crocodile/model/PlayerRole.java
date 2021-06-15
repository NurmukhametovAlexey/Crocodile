package ru.nurmukhametovalexey.crocodile.model;

import java.io.Serializable;

public enum PlayerRole implements Serializable {
    PAINTER,
    GUESSER,
    PAINTER_WINNER,
    GUESSER_WINNER;

    @Override
    public String toString() {
        switch (this) {
            case PAINTER:
                return "PAINTER";
            case GUESSER:
                return "GUESSER";
            case PAINTER_WINNER:
                return "PAINTER_WINNER";
            case GUESSER_WINNER:
                return "GUESSER_WINNER";
            default:
                throw new IllegalArgumentException();
        }
    }
}
