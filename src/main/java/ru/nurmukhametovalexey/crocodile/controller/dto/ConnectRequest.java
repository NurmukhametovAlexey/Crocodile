package ru.nurmukhametovalexey.crocodile.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;
import ru.nurmukhametovalexey.crocodile.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConnectRequest {
    String gameUUID;
    PlayerRole playerRole;
    Boolean reconnect = false;

    @Override
    public String toString() {
        return "ConnectRequest{" +
                "gameUUID='" + gameUUID + '\'' +
                ", playerRole=" + playerRole +
                ", reconnect=" + reconnect +
                '}';
    }
}
