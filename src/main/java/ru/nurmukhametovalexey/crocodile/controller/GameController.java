package ru.nurmukhametovalexey.crocodile.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.nurmukhametovalexey.crocodile.controller.dto.*;
import ru.nurmukhametovalexey.crocodile.dao.GameDAO;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.exception.UserNotFoundException;
import ru.nurmukhametovalexey.crocodile.model.ChatMessage;
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.model.GameStatus;
import ru.nurmukhametovalexey.crocodile.service.GameService;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameDAO gameDAO;

    @GetMapping()
    public ModelAndView displayGame(@RequestParam(value = "uuid") String gameUUID, Principal principal) throws GameNotFoundException {
        log.info("display game: " + gameUUID);
        if (principal == null) {
            log.info("principal == null ");
        } else {
            log.info("principal is: " + principal);
        }
        Game game = gameDAO.getGameByUUID(gameUUID);
        if (game == null) {
            throw new GameNotFoundException("Game not found: " + gameUUID);
        }

        ModelAndView modelAndView = new ModelAndView("/game");
        modelAndView.addObject(game);
        modelAndView.addObject("currentUser", principal.getName());
        return modelAndView;
    }

    /*@PostMapping("/start")
    public ResponseEntity<Game> start(@RequestBody StartRequest startRequest) {
        log.info("start game request: {}", startRequest.toString());
        return ResponseEntity.ok(gameService.createGame(startRequest.getCreator(), startRequest.getDifficulty()));
    }*/

    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestBody StartRequest startRequest, Principal principal) throws UserNotFoundException, InvalidGameStateException {
        //log.info("start game request: {}", startRequest.toString());
        log.info("start game request difficulty: {}", startRequest.getDifficulty());

        if (principal == null) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        } else {
            Game game = gameService.createGame(principal.getName(), startRequest.getDifficulty());
            return ResponseEntity.ok(game);
        }
    }

    @PostMapping("/connect")
    public ResponseEntity<?> connect(@RequestBody ConnectRequest connectRequest, Principal principal) throws InvalidGameStateException, GameNotFoundException, UserNotFoundException {
        log.info("connect game request: {}", connectRequest);

        if (principal == null) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        else if (connectRequest.getReconnect().booleanValue()) {
            Game game = gameService.reconnect(principal.getName());
            return ResponseEntity.ok(game);
        } else {
            Game game = gameService.connectByUUID(
                    connectRequest.getGameUUID(),
                    principal.getName(),
                    connectRequest.getPlayerRole()
            );
            return ResponseEntity.ok(game);
        }
    }

/*    @PostMapping("/gameplay")
    public ResponseEntity<?> gamePlay(@RequestBody WebsocketChatMessage message, Principal principal) throws InvalidGameStateException, GameNotFoundException {
       log.info("gamePlay: {}", message);

        if (principal == null) {
            return new ResponseEntity<String >("Unauthorized", HttpStatus.UNAUTHORIZED);
        } else {
            message.setUserLogin(principal.getName());
            Game game = gameService.gamePlay(message);
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameUUID(), message);
            log.info("simpMessagingTemplate sent message to /topic/game-progress/{}", game.getGameUUID());
            return ResponseEntity.ok(game);
        }
    }*/

/*    @MessageMapping("/test")
    public void response(GamePlayMessage message) {
        log.info("@MessageMapping(\"/test\"): {}", message.toString());
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + message.getGameUUID(), message);
    }*/
    @MessageMapping("/game-socket/{gameUUID}")
    @SendTo("/topic/game-progress/{gameUUID}")
    @ResponseBody
    public WebsocketMessage simple(@DestinationVariable String gameUUID, WebsocketMessage message, Principal principal) throws InvalidGameStateException, GameNotFoundException {
        //log.info("Called /game-socket/{} message: {}", gameUUID, message);

        if (principal == null) {
            throw new InvalidGameStateException("PRINCIPAL NOT FOUND");
        };

        if (message instanceof WebsocketCanvasMessage) {
            //log.info("returning canvas message {} to /topic/game-progress/", message);
            return message;
        } else if (message instanceof WebsocketChatMessage) {
            log.info("Called /game-socket/{} message: {}", gameUUID, message);

            ((WebsocketChatMessage) message).setSender(principal.getName());

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(((WebsocketChatMessage) message).getMessage());
            chatMessage.setLogin(principal.getName());
            chatMessage.setGameUUID(gameUUID);
            chatMessage.setTimeSent(LocalDateTime.now());

            Game game = gameService.gamePlay(chatMessage);
            if(game.getStatus() == GameStatus.FINISHED) {
                ((WebsocketChatMessage) message).setMessage("victory");
            }
            log.info("returning chat message to /topic/game-progress/{}", gameUUID);
            return message;
        }
        log.info("returning unknown message to /topic/game-progress/{}", gameUUID);
        return message;

    }
}
