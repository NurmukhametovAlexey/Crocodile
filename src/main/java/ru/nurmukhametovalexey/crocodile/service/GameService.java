package ru.nurmukhametovalexey.crocodile.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nurmukhametovalexey.crocodile.Exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.Exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.dao.GameDAO;
import ru.nurmukhametovalexey.crocodile.dao.SecretWordDAO;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class GameService {

    private  final SecretWordDAO secretWordDAO;
    private final GameDAO gameDAO;

    @Autowired
    public GameService(SecretWordDAO secretWordDAO, GameDAO gameDAO) {
        this.secretWordDAO = secretWordDAO;
        this.gameDAO = gameDAO;
    }

    public Game createGame(User creator, int difficulty) {
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

    public Game connectByUUID(String gameUUID, User newPlayer, PlayerRole playerRole) throws GameNotFoundException, InvalidGameStateException {
        Game game = gameDAO.getGameByUUID(gameUUID);

        log.info("got game: {}", game.toString());

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
}
