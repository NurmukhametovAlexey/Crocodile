package ru.nurmukhametovalexey.crocodile.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.model.GameStatus;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;
import ru.nurmukhametovalexey.crocodile.model.User;

import java.util.List;

@Slf4j
@Component
public class GameDAO {

    private final String fullGameInfoTableQuery =
            "select * from gameuser natural join \"User\" natural join game natural join dictionary ";

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public GameDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nullable
    public Game getGameByUUID(String gameUUID) {
        return jdbcTemplate.query(
                fullGameInfoTableQuery + "WHERE gameUUID=?",
                new GameMapper(),
                gameUUID
        ).stream()
                .findAny()
                .orElse(null);
    }

    @Nullable
    public List<Game> getAll() {
        return jdbcTemplate.query(
                fullGameInfoTableQuery,
                new GameMapper()
        );
    }

    @Nullable
    public List<Game> getGamesByStatus(GameStatus status) {
        return jdbcTemplate.query(
                fullGameInfoTableQuery + "WHERE status=?",
                new GameMapper(),
                status.toString()
        );
    }

    @Nullable
    public List<Game> getGamesByLogin(String login) {
        return jdbcTemplate.query(
                fullGameInfoTableQuery + "WHERE login=?",
                new GameMapper(),
                login
        );
    }

    @Nullable
    public List<Game> getGamesByLoginAndStatus(String login, GameStatus status) {
        return jdbcTemplate.query(
                fullGameInfoTableQuery + "WHERE login=? AND status=?::gamestatus",
                new GameMapper(),
                login, status.toString()
        );
    }

    public boolean save(Game game) {
        int rowsAffected = jdbcTemplate.update(
                "INSERT INTO Game(gameUUID, word, timeStarted, timeFinished, status)" +
                        "VALUES (?, ?, ?, ?, ?)",
                game.getGameUUID(), game.getSecretWord().getWord(),
                game.getTimeStarted(), game.getTimeFinished(), game.getStatus().toString()
        );
        log.info("game save: {}, rows affected: {}", game, rowsAffected);

        if (game.getPainter() != null) {
            jdbcTemplate.update(
                    "INSERT INTO GameUser(gameUUID, userId, playerRole) VALUES (?, ?, ?)",
                    game.getGameUUID(), game.getPainter().getUserId(), PlayerRole.PAINTER.toString()
            );
        }
        log.info("gameUser painter save: {}", game.getPainter());

        for (User guesser: game.getGuessers()) {
            jdbcTemplate.update(
                    "INSERT INTO GameUser(gameUUID, usderId, playerRole) VALUES (?, ?, ?)",
                    game.getGameUUID(), guesser.getUserId(), PlayerRole.GUESSER.toString()
            );
            log.info("gameUser drawer save: {}", guesser);
        }

        return rowsAffected!=0;
    }

    public boolean update(Game game) {
        int rowsAffected = jdbcTemplate.update(
                "UPDATE Game SET word=?, timeStarted=?, timeFinished=?, status=?" +
                        "WHERE gameUUID=?",
                game.getSecretWord().getWord(), game.getTimeStarted(),
                game.getTimeFinished(), game.getStatus().toString(),
                game.getGameUUID()
        );
        log.info("game update: {}, rows affected: {}", game, rowsAffected);

        if (game.getPainter() != null) {
            jdbcTemplate.update(
                    "INSERT INTO GameUser(gameUUID, userId, playerRole) VALUES (?, ?, ?)" +
                            "ON CONFLICT (gameUUID, userId) DO NOTHING",
                    game.getGameUUID(), game.getPainter().getUserId(), PlayerRole.PAINTER.toString()
            );
        }
        log.info("gameUser painter update: {}", game.getPainter());

        for (User guesser: game.getGuessers()) {
            jdbcTemplate.update(
                    "INSERT INTO GameUser(gameUUID, userId, playerRole) VALUES (?, ?, ?)" +
                            "ON CONFLICT (gameUUID, userId) DO NOTHING",
                    game.getGameUUID(), guesser.getUserId(), PlayerRole.GUESSER.toString()
            );
            log.info("gameUser guesser update: {}", guesser);
        }
        return rowsAffected!=0;
    }

}
