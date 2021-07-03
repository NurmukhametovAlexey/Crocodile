package ru.nurmukhametovalexey.crocodile.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.nurmukhametovalexey.crocodile.model.GameHistory;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Service
public class ComplexDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final GameUserDAO gameUserDAO;
    private  final DictionaryDAO dictionaryDAO;
    private final ChatDAO chatDAO;

    @Autowired
    public ComplexDao(JdbcTemplate jdbcTemplate, UserDAO userDAO, GameDAO gameDAO,
                      GameUserDAO gameUserDAO, DictionaryDAO dictionaryDAO, ChatDAO chatDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.gameUserDAO = gameUserDAO;
        this.dictionaryDAO = dictionaryDAO;
        this.chatDAO = chatDAO;
    }

    @Nullable
    public Integer getDifficultyByGameUUID(String gameUUID) {
        Game game = gameDAO.getByUUID(gameUUID);
        if (game == null) {
            return null;
        } else {
            return dictionaryDAO.getDifficultyByWord(game.getWord());
        }
    }

    @Nullable
    public Integer getBountyByGameUUIDAndRole(String gameUUID, PlayerRole playerRole) {
        try {
            Integer initialPoints = 1;
            if (playerRole == PlayerRole.GUESSER) {
                initialPoints *= gameUserDAO.getByGameUuidAndRole(gameUUID, PlayerRole.GUESSER).size();
            }
            Integer difficulty = getDifficultyByGameUUID(gameUUID);
            return initialPoints*difficulty;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Nullable
    public String getActiveGameUuidByLogin(String login) {
        List<String> activeGameUUIDS = gameDAO.getActiveGamesList().stream()
                .map(Game::getGameUUID)
                .collect(Collectors.toList());
        log.info("active games: {}", activeGameUUIDS);
        List<String> userGameUUIDS = gameUserDAO.getByLogin(login).stream()
                .map(GameUser::getGameUUID)
                .collect(Collectors.toList());
        log.info("user games: {}", userGameUUIDS);


        return activeGameUUIDS.stream()
               .filter(userGameUUIDS::contains)
                .findAny()
                .orElse(null);
    }

    public List<GameHistory> getGameHistoryByLogin(String login) {
        List<GameHistory> gameHistoryList = jdbcTemplate.query(
                "SELECT * FROM GameUser NATURAL JOIN Game WHERE login=? AND timefinished IS NOT NULL ORDER BY timeStarted DESC",
                new GameHistoryMapper(), login
        );
        for (GameHistory gh: gameHistoryList) {
            if(gh.getWin()) {
                Boolean win = gh.getPlayerRole().equals(PlayerRole.PAINTER)
                        ||chatDAO.getLastMessageByGameUUID(gh.getGameUUID()).getLogin().equals(login);
                gh.setWin(win);
            }
            log.info("game history: {}", gh);
        }

        return gameHistoryList;
    }
}
