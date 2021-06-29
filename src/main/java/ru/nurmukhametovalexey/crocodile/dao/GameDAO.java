package ru.nurmukhametovalexey.crocodile.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.nurmukhametovalexey.crocodile.model.*;

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
                "SELECT * FROM Game WHERE gameUUID=?",
                new BeanPropertyRowMapper<>(Game.class),
                gameUUID
        ).stream()
                .findAny()
                .orElse(null);
    }

    @Nullable
    public List<Game> getGamesByStatus(GameStatus status) {
        return jdbcTemplate.query(
                "SELECT * FROM Game WHERE status=?",
                new BeanPropertyRowMapper<>(Game.class),
                status.toString()
        );
    }

    @Nullable
    public List<Game> getActiveGames() {
        return jdbcTemplate.query(
                "SELECT * FROM Game WHERE status=?::gamestatus OR status=?::gamestatus",
                new BeanPropertyRowMapper<>(Game.class),
                GameStatus.NEW.toString(), GameStatus.IN_PROGRESS.toString()
        );
    }

    public boolean save(Game game) {
        int rowsAffected = jdbcTemplate.update(
                "INSERT INTO Game(gameUUID, word, timeStarted, timeFinished, status)" +
                        "VALUES (?, ?, ?, ?, ?)",
                game.getGameUUID(), game.getWord(),
                game.getTimeStarted(), game.getTimeFinished(), game.getStatus().toString()
        );
        log.info("game save: {}, rows affected: {}", game, rowsAffected);

        return rowsAffected!=0;
    }

    public boolean update(Game game) {
        int rowsAffected = jdbcTemplate.update(
                "UPDATE Game SET word=?, timeStarted=?, timeFinished=?, status=?" +
                        "WHERE gameUUID=?",
                game.getWord(), game.getTimeStarted(),
                game.getTimeFinished(), game.getStatus().toString(),
                game.getGameUUID()
        );
        log.info("game update: {}, rows affected: {}", game, rowsAffected);

        return rowsAffected!=0;
    }

}
