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
import ru.nurmukhametovalexey.crocodile.model.Chat;
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;
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

    @PostMapping("/start")
    public ModelAndView start(@ModelAttribute("startRequest") StartRequest startRequest, Principal principal)
            throws UserNotFoundException, InvalidGameStateException {

        log.info("start game request difficulty: {}", startRequest.getDifficulty());

        if (principal == null) {
            ModelAndView modelAndView = new ModelAndView("/login");
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
                ModelAndView modelAndView = new ModelAndView("/error");
                return modelAndView;
            } catch (GameNotFoundException e) {
                e.printStackTrace();
                ModelAndView modelAndView = new ModelAndView("/error");
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
            ModelAndView modelAndView = new ModelAndView("/login");
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

            Chat chatMessage = new Chat();
            chatMessage.setMessage("joined the chat");
            chatMessage.setLogin(principal.getName());
            chatMessage.setTimeSent(LocalDateTime.now());

            WebsocketChatMessage websocketChatMessage = new WebsocketChatMessage();
            websocketChatMessage.setMessage(gameService.chatMessageToString(chatMessage));
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + connectRequest.getGameUUID(), websocketChatMessage);

            return modelAndView;

        } catch (GameNotFoundException e) {
            e.printStackTrace();
            ModelAndView modelAndView = new ModelAndView("/error");
            return modelAndView;
        }
        }
    }

}
