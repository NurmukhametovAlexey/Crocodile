package ru.nurmukhametovalexey.crocodile.dao;

import org.springframework.jdbc.core.RowMapper;
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.model.GameStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class GameMapper implements RowMapper<Game> {
    @Override
    public Game mapRow(ResultSet resultSet, int i) throws SQLException {
        Game game = new Game();

        game.setGameUUID(resultSet.getString("gameUUID"));
        game.setSecretWord(resultSet.getString("secretWord"));
        game.setTimeStarted(resultSet.getObject("timeStarted", LocalDateTime.class));
        game.setTimeFinished(resultSet.getObject("timeFinished", LocalDateTime.class));
        game.setStatus(GameStatus.valueOf(resultSet.getString("status")));

        return game;
    }

}
