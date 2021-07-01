package ru.nurmukhametovalexey.crocodile.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.model.GameStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJdbcTest
@Sql({"classpath:dictionary_schema.sql", "classpath:dictionary_test_data.sql",
        "classpath:game_schema.sql", "classpath:game_test_data.sql"})
class GameDAOTest {

    private final JdbcTemplate jdbcTemplate;
    private final GameDAO gameDAO;
    private final DateTimeFormatter formatter;

    @Autowired
    public GameDAOTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.gameDAO = new GameDAO(jdbcTemplate);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Test
    void getGameByUUID() {
        Game expected = new Game("uuid1","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        Game result = gameDAO.getGameByUUID("uuid1");
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getGameByUUID_IfNoGame_ShouldReturnNull() {
        Game result = gameDAO.getGameByUUID("no such uuid");
        assertThat(result).isNull();
    }

    @Test
    void getGamesByStatus() {
        List<Game> expected = Arrays.asList(
                new Game("uuid1","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW),
                new Game("uuid4","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW)
        );
        List<Game> result = gameDAO.getGamesByStatus(GameStatus.NEW);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getGamesByStatus_IfNoGame_ShouldReturnEmptyList() {
        List<Game> result = gameDAO.getGamesByStatus(GameStatus.IN_PROGRESS);
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getActiveGames() {
        List<Game> expected = Arrays.asList(
                new Game("uuid1","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW),
                new Game("uuid4","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW)
        );
        List<Game> result = gameDAO.getActiveGames();
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getActiveGames_IfNoGame_ShouldReturnEmptyList() {
        jdbcTemplate.update(
                "delete from game where status='NEW' or status='IN_PROGRESS'"
        );
        List<Game> result = gameDAO.getActiveGames();
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void save() {
        Game gameToSave = new Game("new game","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        Boolean result = gameDAO.save(gameToSave);
        assertThat(result).isTrue();
        Game savedGame = gameDAO.getGameByUUID(gameToSave.getGameUUID());
        assertThat(savedGame).usingRecursiveComparison().isEqualTo(gameToSave);
    }

    @Test
    void save_IfPrimaryKeyViolated_ShouldThrowDataAccessException() {
        Game gameToSave = new Game("uuid1","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        assertThatThrownBy(() -> gameDAO.save(gameToSave))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void save_IfForeignKeyViolated_ShouldThrowDataAccessException() {
        Game gameToSave = new Game("new uuid","no such word", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        assertThatThrownBy(() -> gameDAO.save(gameToSave))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void update() {
        Game gameToUpdate = new Game("uuid1","medium",null,null, GameStatus.FINISHED);
        Boolean result = gameDAO.update(gameToUpdate);
        assertThat(result).isTrue();
        Game updatedGame = gameDAO.getGameByUUID(gameToUpdate.getGameUUID());
        assertThat(updatedGame).usingRecursiveComparison().isEqualTo(gameToUpdate);
    }

    @Test
    void update_IfForeignKeyViolated_ShouldThrowDataAccessException() {
        Game gameToUpdate = new Game("uuid1","no such word", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        assertThatThrownBy(() -> gameDAO.update(gameToUpdate))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void update_IfNoGameToUpdate_ShouldNotAffectRows() {
        Game gameToUpdate = new Game("no such uuid","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        Boolean result = gameDAO.update(gameToUpdate);
        assertThat(result).isFalse();
    }
}