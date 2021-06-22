package ru.nurmukhametovalexey.crocodile.controller.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WebsocketCanvasMessage extends WebsocketMessage {
    private Double x_start;
    private Double y_start;
    private Double x_finish;
    private Double y_finish;

}
