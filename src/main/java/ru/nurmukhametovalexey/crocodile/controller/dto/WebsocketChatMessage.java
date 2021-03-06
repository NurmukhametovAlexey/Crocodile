package ru.nurmukhametovalexey.crocodile.controller.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WebsocketChatMessage extends WebsocketMessage {
    String message;
    String sender;
    Boolean victory;
}
