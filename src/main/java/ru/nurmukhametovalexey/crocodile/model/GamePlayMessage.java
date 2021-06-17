package ru.nurmukhametovalexey.crocodile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayMessage {
    private String message;
    private String gameUUID;
    private String userLogin;

    @Override
    public String toString() {
        return "GamePlayMessage{" +
                "message='" + message + '\'' +
                ", gameUUID='" + gameUUID + '\'' +
                ", userLogin=" + userLogin +
                '}';
    }
}
