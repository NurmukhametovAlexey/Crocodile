package ru.nurmukhametovalexey.crocodile.model;

public enum GameStatus {
    NEW,
    IN_PROGRESS,
    FINISHED,
    CANCELLED;

    @Override
    public String toString() {
        switch (this) {
            case NEW:
                return "NEW";
            case IN_PROGRESS:
                return "IN_PROGRESS";
            case FINISHED:
                return "FINISHED";
            case CANCELLED:
                return "CANCELLED";
            default:
                throw new IllegalArgumentException();
        }
    }
}
