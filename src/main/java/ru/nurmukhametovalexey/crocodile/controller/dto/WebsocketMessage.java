package ru.nurmukhametovalexey.crocodile.controller.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.ToString;

@ToString
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = WebsocketChatMessage.class, name = "chat"),
        @JsonSubTypes.Type(value = WebsocketCanvasMessage.class, name = "canvas"),
        @JsonSubTypes.Type(value = WebsocketStatusMessage.class, name = "status"),
})
public abstract class WebsocketMessage {
}
