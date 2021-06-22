package ru.nurmukhametovalexey.crocodile.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebsocketChatMessage extends WebsocketMessage {

    String message;
    String sender;

    @Override
    public String toString() {
        return "WebsocketChatMessage{" +
                "message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
