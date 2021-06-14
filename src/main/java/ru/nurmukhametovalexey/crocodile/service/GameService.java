package ru.nurmukhametovalexey.crocodile.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nurmukhametovalexey.crocodile.Exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.Exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.dao.GameDAO;
import ru.nurmukhametovalexey.crocodile.dao.GameUserDAO;
import ru.nurmukhametovalexey.crocodile.dao.SecretWordDAO;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class GameService {

    private  final SecretWordDAO secretWordDAO;
    private final GameDAO gameDAO;
    private final GameUserDAO gameUserDAO;

    @Autowired
    public GameService(SecretWordDAO secretWordDAO, GameDAO gameDAO, GameUserDAO gameUserDAO) {
        this.secretWordDAO = secretWordDAO;
        this.gameDAO = gameDAO;
        this.gameUserDAO = gameUserDAO;
    }

    public Game createGame(User creator, int difficulty) {
        Game game = new Game();
        game.setGameUUID(UUID.randomUUID().toString());
        game.setStatus(GameStatus.NEW);
        game.setSecretWord(secretWordDAO.getRandomWordByDifficulty(difficulty).getWord());
        game.setTimeStarted(LocalDateTime.now());
        gameDAO.save(game);
        log.info("gameDAO.save: {}", game);

        GameUser gameUser = new GameUser();
        gameUser.setGameUUID(game.getGameUUID());
        gameUser.setUserId(creator.getUserId());
        gameUser.setPlayerRole(PlayerRole.PAINTER);
        gameUserDAO.save(gameUser);
        log.info("gameUserDAO.save: {}", gameUser);

        return game;
    }

    public Game connectByUUID(User newPlayer, String gameUUID) throws GameNotFoundException, InvalidGameStateException {
        Game game = gameDAO.getGameByUUID(gameUUID);
        if (game == null) {
            throw new GameNotFoundException("Game with this UUID not found: " +gameUUID);
        }
        else if (game.getStatus()!=GameStatus.NEW && game.getStatus()!=GameStatus.IN_PROGRESS) {
            throw new InvalidGameStateException("Game is already finished: " + gameUUID);
        }
        GameUser gameUser = new GameUser();
        gameUser.setGameUUID(game.getGameUUID());
        gameUser.setUserId(newPlayer.getUserId());
        gameUser.setPlayerRole(PlayerRole.GUESSER);

        return game;
    }
}
