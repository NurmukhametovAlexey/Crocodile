package ru.nurmukhametovalexey.crocodile.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nurmukhametovalexey.crocodile.controller.dto.WebsocketChatMessage;
import ru.nurmukhametovalexey.crocodile.dao.UserDAO;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.dao.GameDAO;
import ru.nurmukhametovalexey.crocodile.dao.SecretWordDAO;
import ru.nurmukhametovalexey.crocodile.exception.UserNotFoundException;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class GameService {

    private  final SecretWordDAO secretWordDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    @Autowired
    public GameService(SecretWordDAO secretWordDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.secretWordDAO = secretWordDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public Game createGame(String creatorLogin, int difficulty) throws UserNotFoundException, InvalidGameStateException {

        /*if(!gameDAO.getGamesByLoginAndStatus(creatorLogin, GameStatus.NEW).isEmpty()
        || !gameDAO.getGamesByLoginAndStatus(creatorLogin, GameStatus.IN_PROGRESS).isEmpty()) {
            throw new InvalidGameStateException("You are already in game");
        }*/

        User creator = userDAO.getUserByLogin(creatorLogin);
        if (creator == null) {
            throw new UserNotFoundException("User does not exist: " + creatorLogin);
        }

        SecretWord secretWord = new SecretWord();
        secretWord.setWord(secretWordDAO.getRandomWordByDifficulty(difficulty).getWord());
        secretWord.setDifficulty(difficulty);

        Game game = new Game();
        game.setGameUUID(UUID.randomUUID().toString());
        game.setSecretWord(secretWord);
        game.setStatus(GameStatus.NEW);
        game.setPainter(creator);
        game.setTimeStarted(LocalDateTime.now());
        gameDAO.save(game);
        log.info("gameDAO.save: {}", game);

        return game;
    }

    public Game connectByUUID(String gameUUID, String newPlayerLogin, PlayerRole playerRole) throws GameNotFoundException, InvalidGameStateException, UserNotFoundException {
        User newPlayer = userDAO.getUserByLogin(newPlayerLogin);
        if(newPlayer == null) {
            throw new UserNotFoundException("User does not exist: " + newPlayerLogin);
        }

        Game game = gameDAO.getGameByUUID(gameUUID);


        if (game == null) {
            throw new GameNotFoundException("Game with this UUID not found: " + gameUUID);
        }
        else if (game.getStatus()!=GameStatus.NEW && game.getStatus()!=GameStatus.IN_PROGRESS) {
            throw new InvalidGameStateException("Game is already finished: " + gameUUID);
        }
        else if (game.getPainter().getUserId().equals(newPlayer.getUserId()) ||
                game.getGuessers().stream().anyMatch(
                        guesser -> guesser.getUserId() == newPlayer.getUserId()
                )) {
            throw new InvalidGameStateException("Player is already connected to the game: " + gameUUID);
        }

        log.info("got game: {}", game.toString());

        if(playerRole == PlayerRole.PAINTER) {
            if(game.getPainter() != null) {
                throw new InvalidGameStateException("Game already has a painter: " + game);
            } else {
                game.setPainter(newPlayer);
            }
        }
        else if (playerRole == PlayerRole.GUESSER) {
            game.getGuessers().add(newPlayer);
        }

        gameDAO.update(game);

        return game;
    }

    public Game reconnect(String playerLogin) throws GameNotFoundException {
        Game game = gameDAO.getGamesByLoginAndStatus(playerLogin, GameStatus.NEW).stream()
                .findAny()
                .orElse(null);
        if (game == null) {
            game = gameDAO.getGamesByLoginAndStatus(playerLogin, GameStatus.IN_PROGRESS).stream()
                    .findAny()
                    .orElseThrow(() -> new GameNotFoundException("No game to reconnect to!"));
        }
        return game;
    }

    public Game gamePlay(ChatMessage message) throws GameNotFoundException, InvalidGameStateException {

        Game game = gameDAO.getGameByUUID(message.getGameUUID());
        if(game == null) {
            throw new GameNotFoundException("Game not found: " + message.getGameUUID());
        }
        else if (game.getStatus() == GameStatus.FINISHED || game.getStatus() == GameStatus.CANCELLED) {
            throw new InvalidGameStateException("Game is finished or cancelled: " + message.getGameUUID());
        }

        log.info("game.getSecretWord().getWord().toLowerCase(): {}", game.getSecretWord().getWord().toLowerCase());
        log.info("message.getMessage().toLowerCase(): {}", message.getMessage().toLowerCase());

        if(game.getSecretWord().getWord().toLowerCase().equals(message.getMessage().toLowerCase())) {
            log.info("PLAYEEERRS WON!!!!" +
                    "\tPAINTER LOGIN: " + game.getPainter().getLogin() +
                    " \tGUESSER LOGIN: " + message.getLogin());
            game.setStatus(GameStatus.FINISHED);
            game.setTimeFinished(LocalDateTime.now());
            gameDAO.update(game);

            User user = game.getPainter();
            user.increaseScore(10);
            log.info("Painter: {} has score {}",user,user.getScore());
            userDAO.update(user);

            user = userDAO.getUserByLogin(message.getLogin());
            user.increaseScore(10);
            log.info("Guesser: {} has score {}",user,user.getScore());
            userDAO.update(user);
        }
        return game;
    }
}
