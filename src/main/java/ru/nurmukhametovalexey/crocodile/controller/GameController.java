package ru.nurmukhametovalexey.crocodile.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.nurmukhametovalexey.crocodile.controller.dto.*;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.model.Chat;
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;
import ru.nurmukhametovalexey.crocodile.service.DaoService;
import ru.nurmukhametovalexey.crocodile.service.GameService;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/game")
@PropertySource("classpath:application.properties")
public class GameController {
    private final GameService gameService;
    private final DaoService daoService;
    private final SimpMessagingTemplate simpMessagingTemplate;



    @GetMapping("/{gameUUID}")
    public ModelAndView show(@PathVariable String gameUUID, Principal principal, RedirectAttributes attributes) {

        WebsocketCommandMessage websocketCommandMessage = new WebsocketCommandMessage();
        websocketCommandMessage.setCommand("upload canvas");
        log.info("sending upload canvas to: /topic/game-progress/{}", gameUUID);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameUUID,
                websocketCommandMessage);
        try {
            Game game = daoService.getGameByUUID(gameUUID);
            PlayerRole playerRole = daoService.getGameUserByGameUuidAndLogin(gameUUID, principal.getName()).getPlayerRole();

            ModelAndView modelAndView = new ModelAndView("/game");
            modelAndView.addObject("game", game);
            modelAndView.addObject("user", principal.getName());
            modelAndView.addObject("userRole", playerRole.toString());
            modelAndView.addObject("chat", gameService.loadChat(game.getGameUUID()));
            return modelAndView;
        }
        catch (GameNotFoundException e) {
            attributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/error");
        }
        catch (NullPointerException e) {
            attributes.addFlashAttribute("errorMessage", "You are not a player of this match!");
            return new ModelAndView("redirect:/error");
        }
    }

    @PostMapping("/start")
    public ModelAndView start(@ModelAttribute("startRequest") StartRequest startRequest,
                              Principal principal, RedirectAttributes attributes) {

            log.info("start game request difficulty: {}", startRequest.getDifficulty());

        try {
                Game game = gameService.createGame(principal.getName(), startRequest.getDifficulty());
            return new ModelAndView("redirect:/game/" + game.getGameUUID());

        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/error");
        }
    }


    @PostMapping("/connect")
    public ModelAndView connect(@ModelAttribute("connectRequest") ConnectRequest connectRequest,
                                Principal principal, RedirectAttributes attributes) {

        log.info("connect game request: {}", connectRequest);

        if (connectRequest.getPlayerRole() == null) {
            connectRequest.setPlayerRole(PlayerRole.GUESSER);
        }

        try {
        Game game = gameService.connectByUUID(
                connectRequest.getGameUUID(),
                principal.getName(),
                connectRequest.getPlayerRole()
        );
        ModelAndView modelAndView = new ModelAndView("redirect:/game/" + game.getGameUUID());

        Chat chatMessage = new Chat();
        chatMessage.setMessage("joined the chat");
        chatMessage.setLogin(principal.getName());
        chatMessage.setTimeSent(LocalDateTime.now());

        chatMessage = daoService.getChatListByGameUUID(game.getGameUUID()).stream()
                .filter(msg -> msg.getLogin().equals(principal.getName()))
                .findAny()
                .orElse(null);

        WebsocketChatMessage websocketChatMessage = new WebsocketChatMessage();
        websocketChatMessage.setMessage(gameService.chatMessageToString(chatMessage));
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + connectRequest.getGameUUID(),
                websocketChatMessage);

        return modelAndView;

        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/error");
        }

    }

    @PostMapping("/{gameUUID}/upload-canvas")
    public ResponseEntity uploadCanvas(@PathVariable String gameUUID, @RequestBody String rawBase64Image) {


        //log.info("upload canvas, image:  {}", rawBase64Image);
        try {
            gameService.uploadCanvasImage(rawBase64Image, gameUUID);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameUUID}/download-canvas")
    public @ResponseBody String downloadCanvas(@PathVariable String gameUUID) {
        String canvasImage = gameService.downloadCanvasImage(gameUUID);
        //log.info("canvas image: {}", canvasImage);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode imageJson = mapper.createObjectNode();
        imageJson.put("image", canvasImage);

        try {
            return mapper.writeValueAsString(imageJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
