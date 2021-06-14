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

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public GameDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nullable
    public Game getGameByUUID(String gameUUID) {
        return jdbcTemplate.query(
                "select * from Game WHERE gameUUID=?",
                new GameMapper(),
                gameUUID
        ).stream()
                .findAny()
                .orElse(null);
    }

    @Nullable
    public List<Game> getAll() {
        return jdbcTemplate.query(
                "select * from Game",
                new GameMapper()
        );
    }

    @Nullable
    public List<Game> getGamesByStatus(GameStatus status) {
        return jdbcTemplate.query(
                "select * from Game WHERE status=?",
                new GameMapper(),
                status.toString()
        );
    }

    public boolean save(Game game) {
        int rowsAffected = jdbcTemplate.update(
                "INSERT INTO Game(gameUUID, secretWord, timeStarted, timeFinished, status)" +
                        "VALUES (?, ?, ?, ?, ?)",
                game.getGameUUID(), game.getSecretWord(),
                game.getTimeStarted(), game.getTimeFinished(), game.getStatus().toString()
        );
        log.info("game save: {}, rows affected: {}", game, rowsAffected);

        return rowsAffected!=0;
    }

    public boolean update(Game game) {
        int rowsAffected = jdbcTemplate.update(
                "UPDATE Game SET gameUUID=?, secretWord=?, timeStarted=?, timeFinished=?, status=?",
                game.getGameUUID(), game.getSecretWord(),
                game.getTimeStarted(), game.getTimeFinished(), game.getStatus().toString()
        );
        log.info("game update: {}, rows affected: {}", game, rowsAffected);

        return rowsAffected!=0;
    }
}
