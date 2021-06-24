package ru.nurmukhametovalexey.crocodile.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.nurmukhametovalexey.crocodile.controller.dto.*;
import ru.nurmukhametovalexey.crocodile.exception.DictionaryException;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.exception.UserNotFoundException;
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;
import ru.nurmukhametovalexey.crocodile.service.GameService;

import java.security.Principal;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /*@GetMapping()
    public ModelAndView displayGame(@RequestParam(value = "uuid") String gameUUID, Principal principal) throws GameNotFoundException {
        log.info("display game: " + gameUUID);
        if (principal == null) {
            log.info("principal == null ");
        } else {
            log.info("principal is: " + principal);
        }
        GameDeprecated gameDeprecated = gameDAODeprecated.getGameByUUID(gameUUID);
        if (gameDeprecated == null) {
            throw new GameNotFoundException("Game not found: " + gameUUID);
        }

        ModelAndView modelAndView = new ModelAndView("/game");
        modelAndView.addObject(gameDeprecated);
        modelAndView.addObject("currentUser", principal.getName());
        return modelAndView;
    }*/

    /*@PostMapping("/start")
    public ResponseEntity<?> start(@RequestBody StartRequest startRequest, Principal principal) throws UserNotFoundException, InvalidGameStateException {
        //log.info("start game request: {}", startRequest.toString());
        log.info("start game request difficulty: {}", startRequest.getDifficulty());

        if (principal == null) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        } else {
            Game game = gameService.createGame(principal.getName(), startRequest.getDifficulty());
            return ResponseEntity.ok(game);
        }
    }*/

    @PostMapping("/start")
    public ModelAndView start(@ModelAttribute("startRequest") StartRequest startRequest, Principal principal)
            throws UserNotFoundException, InvalidGameStateException {

        log.info("start game request difficulty: {}", startRequest.getDifficulty());

        if (principal == null) {
            ModelAndView modelAndView = new ModelAndView("login");
            return modelAndView;
        } else {

            try {
                Game game = gameService.createGame(principal.getName(), startRequest.getDifficulty());
                ModelAndView modelAndView = new ModelAndView("/game");
                modelAndView.addObject("game", game);
                modelAndView.addObject("user", principal.getName());
                modelAndView.addObject("userRole", PlayerRole.PAINTER.toString());
                modelAndView.addObject("chat", gameService.loadChat(game.getGameUUID()));
                return modelAndView;

            } catch (DictionaryException e) {
                e.printStackTrace();
                ModelAndView modelAndView = new ModelAndView("error");
                return modelAndView;
            } catch (GameNotFoundException e) {
                e.printStackTrace();
                ModelAndView modelAndView = new ModelAndView("error");
                return modelAndView;
            }
        }
    }

    @PostMapping("/connect")
    public ModelAndView connect(@ModelAttribute("connectRequest") ConnectRequest connectRequest, Principal principal)
            throws InvalidGameStateException, UserNotFoundException {
        log.info("connect game request: {}", connectRequest);

        if(connectRequest.getPlayerRole() == null) {
            connectRequest.setPlayerRole(PlayerRole.GUESSER);
        }

        if (principal == null) {
            ModelAndView modelAndView = new ModelAndView("login");
            return modelAndView;
        } else {
            try {
            Game game = gameService.connectByUUID(
                    connectRequest.getGameUUID(),
                    principal.getName(),
                    connectRequest.getPlayerRole()
            );
            ModelAndView modelAndView = new ModelAndView("/game");
            modelAndView.addObject("game", game);
            modelAndView.addObject("user", principal.getName());
            modelAndView.addObject("chat", gameService.loadChat(game.getGameUUID()));

            WebsocketStatusMessage message = new WebsocketStatusMessage();
            message.setStatus("connect");
            message.setLogin(principal.getName());
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + connectRequest.getGameUUID(), message);

            return modelAndView;

        } catch (GameNotFoundException e) {
            e.printStackTrace();
            ModelAndView modelAndView = new ModelAndView("error");
            return modelAndView;
        }
        }
    }

    /*@PostMapping("/connect")
    public ResponseEntity<?> connect(@RequestBody ConnectRequest connectRequest, Principal principal) throws InvalidGameStateException, GameNotFoundException, UserNotFoundException {
        log.info("connect game request: {}", connectRequest);

        if (principal == null) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        } else {
            GameDeprecated gameDeprecated = gameService.connectByUUID(
                    connectRequest.getGameUUID(),
                    principal.getName(),
                    connectRequest.getPlayerRole()
            );

            WebsocketStatusMessage message = new WebsocketStatusMessage();
            message.setStatus("connect");
            message.setLogin(principal.getName());
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + connectRequest.getGameUUID(), message);

            return ResponseEntity.ok(gameDeprecated);
        }
    }*/
}
