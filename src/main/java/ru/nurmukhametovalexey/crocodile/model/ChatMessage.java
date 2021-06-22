package ru.nurmukhametovalexey.crocodile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String message;
    private String login;
    private String gameUUID;
    private LocalDateTime timeSent;
}
