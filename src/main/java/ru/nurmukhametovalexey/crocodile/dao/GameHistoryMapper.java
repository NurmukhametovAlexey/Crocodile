package ru.nurmukhametovalexey.crocodile.dao;

import org.springframework.jdbc.core.RowMapper;
import ru.nurmukhametovalexey.crocodile.model.GameHistory;
import ru.nurmukhametovalexey.crocodile.model.GameStatus;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameHistoryMapper implements RowMapper {

    @Override
    public GameHistory mapRow(ResultSet resultSet, int i) throws SQLException {
        GameHistory gameHistory = new GameHistory();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
        gameHistory.setTimeStarted(resultSet.getObject("timeStarted", LocalDateTime.class).format(formatter));
        gameHistory.setTimeFinished(resultSet.getObject("timeFinished", LocalDateTime.class).format(formatter));
        gameHistory.setGameUUID(resultSet.getString("gameUUID"));
        gameHistory.setPlayerRole(PlayerRole.valueOf(resultSet.getString("playerRole")));
        gameHistory.setWin(GameStatus.valueOf(resultSet.getString("status")).equals(GameStatus.FINISHED));

        return gameHistory;
    }
}
