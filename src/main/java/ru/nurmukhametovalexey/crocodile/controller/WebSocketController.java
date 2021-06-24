package ru.nurmukhametovalexey.crocodile.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nurmukhametovalexey.crocodile.controller.dto.WebsocketCanvasMessage;
import ru.nurmukhametovalexey.crocodile.controller.dto.WebsocketChatMessage;
import ru.nurmukhametovalexey.crocodile.controller.dto.WebsocketMessage;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.model.*;
import ru.nurmukhametovalexey.crocodile.service.GameService;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@RestController
@AllArgsConstructor
public class WebSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/game-socket/{gameUUID}")
    @SendTo("/topic/game-progress/{gameUUID}")
    @ResponseBody
    public WebsocketMessage handleWsMessage(@DestinationVariable String gameUUID, WebsocketMessage message, Principal principal) throws InvalidGameStateException, GameNotFoundException {

        if (message instanceof WebsocketCanvasMessage) {
            //log.info("returning canvas message {} to /topic/game-progress/", message);
            return message;
        }

        log.info("Called /game-socket/{} message: {}", gameUUID, message);

        if (principal == null) {
            throw new InvalidGameStateException("PRINCIPAL NOT FOUND");
        };

        if (message instanceof WebsocketChatMessage) {

            ((WebsocketChatMessage) message).setSender(principal.getName());

            Chat chatMessage = new Chat();
            chatMessage.setMessage(((WebsocketChatMessage) message).getMessage());
            chatMessage.setLogin(principal.getName());
            chatMessage.setGameUUID(gameUUID);
            chatMessage.setTimeSent(LocalDateTime.now());

            Game game = gameService.gamePlay(chatMessage);

            ((WebsocketChatMessage) message).setMessage(gameService.chatMessageToString(chatMessage));

            if(game.getStatus() == GameStatus.FINISHED) {
                simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameUUID, message);

                message = new WebsocketChatMessage();
                ((WebsocketChatMessage) message).setMessage(principal.getName() + " WINS!");
                ((WebsocketChatMessage) message).setSender(principal.getName());
                ((WebsocketChatMessage) message).setVictory(true);
            }
            log.info("returning chat message {} to /topic/game-progress/{}", chatMessage ,gameUUID);

            return message;
        }
        log.info("returning unknown message {} to /topic/game-progress/{}", message,gameUUID);
        return message;
    }
}
