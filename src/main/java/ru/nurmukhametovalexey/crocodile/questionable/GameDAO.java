/*
package ru.nurmukhametovalexey.crocodile.questionable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
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
                "select * from GameUser natural join \"User\" natural join Game WHERE gameUUID=?",
                new GameWithPlayersMapper(),
                gameUUID
        ).stream()
                .findAny()
                .orElse(null);
    }

    @Nullable
    public List<Game> getAll() {
        return jdbcTemplate.query(
                "select * from GameUser natural join \"User\" natural join Game",
                new GameWithPlayersMapper()
        );
    }

    @Nullable
    public List<Game> getGamesByStatus(GameStatus status) {
        return jdbcTemplate.query(
                "select * from GameUser natural join \"User\" natural join Game WHERE status=?",
                new GameWithPlayersMapper(),
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
        }

        return rowsAffected!=0;
    }
}
*/
