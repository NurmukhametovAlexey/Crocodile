/*
package ru.nurmukhametovalexey.crocodile.questionable;

import org.springframework.jdbc.core.RowMapper;
import ru.nurmukhametovalexey.crocodile.model.GameStatus;
import ru.nurmukhametovalexey.crocodile.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameWithPlayersMapper implements RowMapper<Game> {
    @Override
    public Game mapRow(ResultSet resultSet, int i) throws SQLException {
        Game game = new Game();

        game.setGameUUID(resultSet.getString("gameUUID"));
        game.setSecretWord(resultSet.getString("secretWord"));
        game.setTimeStarted(resultSet.getTimestamp("timeStarted"));
        game.setTimeFinished(resultSet.getTimestamp("timeFinished"));
        game.setStatus(GameStatus.valueOf(resultSet.getString("status")));

        User player = new User();
        player.setLogin(resultSet.getString("login"));
        player.setPassword(resultSet.getString("password"));
        player.setEmail(resultSet.getString("email"));
        player.setRole(resultSet.getString("role"));
        player.setScore(resultSet.getInt("score"));

        String playerRole = resultSet.getString("playerrole");
        if (playerRole.equals("PAINTER") || playerRole.equals("PAINTER_WINNER")) {
            game.setPainter(player);
        } else {
            game.getGuessers().add(player);
        }

        return game;
    }

}
*/
