package ru.nurmukhametovalexey.crocodile.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.nurmukhametovalexey.crocodile.model.GameHistory;
import ru.nurmukhametovalexey.crocodile.dao.*;
import ru.nurmukhametovalexey.crocodile.exception.DictionaryException;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@Service
public class DaoService {
    private final JdbcTemplate jdbcTemplate;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final GameUserDAO gameUserDAO;
    private  final DictionaryDAO dictionaryDAO;
    private final ChatDAO chatDAO;

    @Nullable
    public Integer getDifficultyByGameUUID(String gameUUID) throws
            GameNotFoundException, InvalidGameStateException, DictionaryException {
        Game game = gameDAO.getGameByUUID(gameUUID);

        if (game == null) {
            throw new GameNotFoundException("Game with uuid not found: " + gameUUID);
        }
        else if (game.getWord() == null) {
            throw new InvalidGameStateException("Game does not have secret word! uuid: " + gameUUID);
        }

        Integer difficulty = dictionaryDAO.getDifficultyByWord(game.getWord());

        if (difficulty == null) {
            throw new DictionaryException("Word not found: " + game.getWord());
        }

        return difficulty;
    }

    @Nullable
    public Integer getBountyByGameUUIDAndRole(String gameUUID, PlayerRole playerRole)
            throws InvalidGameStateException, GameNotFoundException, DictionaryException {
        Integer initialPoints = 1;
        if (playerRole == PlayerRole.GUESSER) {
            initialPoints *= gameUserDAO.getGameUserByGameUuidAndRole(gameUUID, PlayerRole.GUESSER).size();
        }
        Integer difficulty = getDifficultyByGameUUID(gameUUID);
        return initialPoints*difficulty;
    }

    @Nullable
    public String getActiveGameUuidByLogin(String login) {
        List<String> activeGameUUIDS = gameDAO.getActiveGames().stream()
                .map(game -> game.getGameUUID())
                .collect(Collectors.toList());
        log.info("active games: {}", activeGameUUIDS);
        List<String> userGameUUIDS = gameUserDAO.getGameUserByLogin(login).stream()
                .map(gameUser -> gameUser.getGameUUID())
                .collect(Collectors.toList());
        log.info("user games: {}", userGameUUIDS);


        return activeGameUUIDS.stream()
               .filter(gameUUID -> userGameUUIDS.contains(gameUUID))
                .findAny()
                .orElse(null);
    }

    public Chat saveChatMessage(String gameUUID, String login, String message) {
        Chat chatMessage = new Chat();
        chatMessage.setGameUUID(gameUUID);
        chatMessage.setLogin(login);
        chatMessage.setMessage(message);
        chatMessage.setTimeSent(LocalDateTime.now());
        chatDAO.save(chatMessage);
        return chatMessage;
    }

    @Nullable
    public List<GameHistory> getGameHistoryByLogin(String login) {
        List<GameHistory> gameHistoryList = jdbcTemplate.query(
                "SELECT * FROM GameUser NATURAL JOIN Game WHERE login=? ORDER BY timeStarted DESC",
                new GameHistoryMapper(), login
        );
        for (GameHistory gh: gameHistoryList) {
            Boolean win = gh.getPlayerRole().equals(PlayerRole.PAINTER)
                    ||chatDAO.getLastMessageByGameUUID(gh.getGameUUID()).getLogin().equals(login);
            gh.setWin(win);
            log.info("game history: {}", gh);
        }

        return gameHistoryList;
    }
}
