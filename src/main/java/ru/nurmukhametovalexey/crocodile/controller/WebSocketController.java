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
import ru.nurmukhametovalexey.crocodile.controller.dto.WebsocketCommandMessage;
import ru.nurmukhametovalexey.crocodile.controller.dto.WebsocketMessage;
import ru.nurmukhametovalexey.crocodile.exception.DictionaryException;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.model.Chat;
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.model.GameStatus;
import ru.nurmukhametovalexey.crocodile.model.GameUser;
import ru.nurmukhametovalexey.crocodile.service.GameService;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class WebSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/game-socket/{gameUUID}")
    @SendTo("/topic/game-progress/{gameUUID}")
    @ResponseBody
    public WebsocketMessage handleWsMessage(@DestinationVariable String gameUUID, WebsocketMessage message,
                                            Principal principal){

        if (! (message instanceof WebsocketCanvasMessage)) {
            log.info("Called /game-socket/{} message: {}", gameUUID, message);
        }

        if (message instanceof WebsocketCanvasMessage) {
            // do nothing
        }
        else if (message instanceof WebsocketCommandMessage) {
            handleCommandMessage(gameUUID, principal.getName(), ((WebsocketCommandMessage) message).getCommand());
        }
        else if (message instanceof WebsocketChatMessage) {
            ((WebsocketChatMessage) message).setSender(principal.getName());
            try {
                message = handleChatMessage(gameUUID, (WebsocketChatMessage) message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            log.info("returning unknown message {} to /topic/game-progress/{}", message,gameUUID);
        }

        return message;
    }



    private void handleCommandMessage(String gameUUID, String login, String command) {

        if (command.equals("begin game")) {
            log.info("begin game");
            String beginMessage = "Game begins!";
            WebsocketChatMessage gameStartMessage = new WebsocketChatMessage();
            gameStartMessage.setMessage(beginMessage);
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameUUID, gameStartMessage);

            gameService.getDaoService().saveChatMessage(gameUUID, login, beginMessage);

            Game game = gameService.getDaoService().getGameDAO().getGameByUUID(gameUUID);
            if (game.getStatus() == GameStatus.NEW) {
                game.setStatus(GameStatus.IN_PROGRESS);
                log.info("updating game: {}", game);
                gameService.getDaoService().getGameDAO().update(game);
            }
        }
        else if (command.equals("cancel game")) {

            log.info("cancel game");
            String cancelMessage = "The game is cancelled!";
            WebsocketChatMessage gameCancelMessage = new WebsocketChatMessage();
            gameCancelMessage.setMessage(cancelMessage);
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameUUID, gameCancelMessage);

            gameService.getDaoService().saveChatMessage(gameUUID, login, cancelMessage);

            Game game = gameService.getDaoService().getGameDAO().getGameByUUID(gameUUID);
            game.setStatus(GameStatus.CANCELLED);
            gameService.getDaoService().getGameDAO().update(game);

        }
        else if (command.equals("leave game")) {

            log.info("leave game");
            String leaveMessage = login + " left the game!";
            WebsocketChatMessage playerLeaveMessage = new WebsocketChatMessage();
            playerLeaveMessage.setMessage(leaveMessage);
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameUUID, playerLeaveMessage);

            gameService.getDaoService().saveChatMessage(gameUUID, login, leaveMessage);

            GameUser gameUser = gameService.getDaoService().getGameUserDAO().getByGameUuidAndLogin(gameUUID, login);
            gameService.getDaoService().getGameUserDAO().delete(gameUser);

        }
    }

    private WebsocketChatMessage handleChatMessage(String gameUUID, WebsocketChatMessage message)
            throws InvalidGameStateException, GameNotFoundException, DictionaryException {

        Chat chatMessage = gameService.getDaoService().saveChatMessage(
                gameUUID, message.getSender(), message.getMessage()
        );

        Game game = gameService.gamePlay(chatMessage);

        message.setMessage(gameService.chatMessageToString(chatMessage));

        if (game.getStatus() == GameStatus.FINISHED) {
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameUUID, message);

            message.setMessage("\"" + game.getWord() + "\" is the right word!!!");
            message.setVictory(true);

            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameUUID, message);

            Map<String, Integer> winnersBounty = gameService.increaseWinnersScore(gameUUID, message.getSender());
            for (var entry : winnersBounty.entrySet()) {
                String bountyMessage = entry.getKey() + " gets " + entry.getValue() + " points!";
                message.setMessage(bountyMessage);
                simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameUUID, message);
                gameService.getDaoService().saveChatMessage(gameUUID, message.getSender(),bountyMessage);
            }
            String finishMessage = "GAME FINISHED!";
            message.setMessage(finishMessage);
            gameService.getDaoService().saveChatMessage(gameUUID, message.getSender(), finishMessage);
        }

        return message;
    }
}
