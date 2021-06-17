package ru.nurmukhametovalexey.crocodile.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


@Slf4j
public class GameMapper implements ResultSetExtractor<List<Game>> {

    @Override
    public List<Game> extractData(ResultSet rs) throws SQLException, DataAccessException {

        List<Game> gameList = new ArrayList<>();

        while(rs.next()) {
            SecretWord secretWord = new SecretWord();
            secretWord.setWord(rs.getString("word"));
            secretWord.setDifficulty(rs.getInt("difficulty"));

            User player = new User();
            player.setUserId(rs.getInt("userId"));
            player.setLogin(rs.getString("login"));
            player.setPassword(rs.getString("password"));
            player.setEmail(rs.getString("email"));
            player.setRole(rs.getString("role"));
            player.setScore(rs.getInt("score"));
            player.setEnabled(rs.getBoolean("enabled"));

            PlayerRole playerRole = PlayerRole.valueOf(rs.getString("playerRole"));

            //log.info("Extracted playerRole: {} for player: {}", playerRole, player);

            String gameUUID = rs.getString("gameUUID");

            int gameListIndex = IntStream.range(0, gameList.size())
                    .filter(i -> gameList.get(i).getGameUUID().equals(gameUUID))
                    .findAny()
                    .orElse(-1);

            Game game;
            if (gameListIndex == -1) {
                game = new Game();
            } else {
                game = gameList.get(gameListIndex);
            }

            game.setGameUUID(gameUUID);
            game.setSecretWord(secretWord);
            game.setTimeStarted(rs.getObject("timeStarted", LocalDateTime.class));
            game.setTimeFinished(rs.getObject("timeFinished", LocalDateTime.class));
            game.setStatus(GameStatus.valueOf(rs.getString("status")));

            if (playerRole == PlayerRole.PAINTER || playerRole == PlayerRole.PAINTER_WINNER) {
                game.setPainter(player);
            } else {
                game.getGuessers().add(player);
            }

            if (gameListIndex == -1) {
                gameList.add(game);
            }
        }
        return gameList;
    }
}
