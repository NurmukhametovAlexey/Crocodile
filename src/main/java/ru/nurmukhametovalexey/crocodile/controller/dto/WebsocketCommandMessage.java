package ru.nurmukhametovalexey.crocodile.controller.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WebsocketCommandMessage extends WebsocketMessage {
    private String command;
}
