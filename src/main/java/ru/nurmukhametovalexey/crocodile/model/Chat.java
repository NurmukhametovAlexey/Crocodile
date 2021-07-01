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
    String login;
    String gameUUID;
    LocalDateTime timeSent;
}
