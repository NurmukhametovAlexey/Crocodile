package ru.nurmukhametovalexey.crocodile.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.nurmukhametovalexey.crocodile.dao.GameDAO;
import ru.nurmukhametovalexey.crocodile.exception.UserNotFoundException;
import ru.nurmukhametovalexey.crocodile.model.GamePlayMessage;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.controller.dto.ConnectRequest;
import ru.nurmukhametovalexey.crocodile.controller.dto.StartRequest;
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.service.GameService;

import java.security.Principal;

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
            log.info("principal is: " + principal.toString());
        }
        Game game = gameDAO.getGameByUUID(gameUUID);
        if (game == null) {
            throw new GameNotFoundException("Game not found: " + gameUUID);
        }

        ModelAndView modelAndView = new ModelAndView("/game");
        modelAndView.addObject(game);

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

    @PostMapping("/gameplay")
    public ResponseEntity<?> gamePlay(@RequestBody GamePlayMessage message, Principal principal) throws InvalidGameStateException, GameNotFoundException {
       log.info("gamePlay: {}", message);

        if (principal == null) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        } else {
            message.setUserLogin(principal.getName());
            Game game = gameService.gamePlay(message);
            simpMessagingTemplate.convertAndSend("topic/game_progress/" + game.getGameUUID(),message);
            return ResponseEntity.ok(game);
        }
    }
}
