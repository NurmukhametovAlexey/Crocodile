package ru.nurmukhametovalexey.crocodile.model;

import java.io.Serializable;

public enum PlayerRole implements Serializable {
    PAINTER,
    GUESSER;

    @Override
    public String toString() {
        switch (this) {
            case PAINTER:
                return "PAINTER";
            case GUESSER:
                return "GUESSER";
            default:
                throw new IllegalArgumentException();
        }
    }
}
