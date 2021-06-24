package ru.nurmukhametovalexey.crocodile.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Chat {
    String message;
    String gameUUID;
    String login;
    LocalDateTime timeSent;
}
