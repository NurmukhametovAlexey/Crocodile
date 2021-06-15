package ru.nurmukhametovalexey.crocodile.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nurmukhametovalexey.crocodile.Exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.Exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.controller.dto.ConnectRequest;
import ru.nurmukhametovalexey.crocodile.controller.dto.StartRequest;
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.service.GameService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    //private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/start")
    public ResponseEntity<Game> start(@RequestBody StartRequest startRequest) {
        log.info("start game request: {}", startRequest.toString());
        return ResponseEntity.ok(gameService.createGame(startRequest.getCreator(), startRequest.getDifficulty()));
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connect(@RequestBody ConnectRequest connectRequest) throws InvalidGameStateException, GameNotFoundException {
        log.info("connect game request: {}", connectRequest);
        return ResponseEntity.ok(
                gameService.connectByUUID(
                        connectRequest.getGameUUID(),
                        connectRequest.getNewPlayer(),
                        connectRequest.getPlayerRole()
                ));
    }
}
