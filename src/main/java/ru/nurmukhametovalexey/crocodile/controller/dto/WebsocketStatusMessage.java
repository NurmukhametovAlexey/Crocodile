package ru.nurmukhametovalexey.crocodile.controller.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WebsocketStatusMessage extends WebsocketMessage {
    private String status;
    private String login;
}
