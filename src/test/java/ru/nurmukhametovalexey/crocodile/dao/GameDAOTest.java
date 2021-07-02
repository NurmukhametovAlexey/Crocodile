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
    private final GameDAO underTest;
    private final DateTimeFormatter formatter;

    @Autowired
    public GameDAOTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.underTest = new GameDAO(jdbcTemplate);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Test
    void getGameByUUID() {
        Game expected = new Game("uuid1","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        Game result = underTest.getByUUID("uuid1");
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getGameByUUID_IfNoGame_ShouldReturnNull() {
        Game result = underTest.getByUUID("no such uuid");
        assertThat(result).isNull();
    }

    @Test
    void getGamesByStatus() {
        List<Game> expected = Arrays.asList(
                new Game("uuid1","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW),
                new Game("uuid4","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW)
        );
        List<Game> result = underTest.getListByStatus(GameStatus.NEW);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getGamesByStatus_IfNoGame_ShouldReturnEmptyList() {
        List<Game> result = underTest.getListByStatus(GameStatus.IN_PROGRESS);
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getActiveGames() {
        List<Game> expected = Arrays.asList(
                new Game("uuid1","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW),
                new Game("uuid4","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW)
        );
        List<Game> result = underTest.getActiveGamesList();
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getActiveGames_IfNoGame_ShouldReturnEmptyList() {
        jdbcTemplate.update(
                "delete from game where status='NEW' or status='IN_PROGRESS'"
        );
        List<Game> result = underTest.getActiveGamesList();
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void save() {
        Game gameToSave = new Game("new game","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        Boolean result = underTest.save(gameToSave);
        assertThat(result).isTrue();
        Game savedGame = underTest.getByUUID(gameToSave.getGameUUID());
        assertThat(savedGame).usingRecursiveComparison().isEqualTo(gameToSave);
    }

    @Test
    void save_IfPrimaryKeyViolated_ShouldThrowDataAccessException() {
        Game gameToSave = new Game("uuid1","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        assertThatThrownBy(() -> underTest.save(gameToSave))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void save_IfForeignKeyViolated_ShouldThrowDataAccessException() {
        Game gameToSave = new Game("new uuid","no such word", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        assertThatThrownBy(() -> underTest.save(gameToSave))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void update() {
        Game gameToUpdate = new Game("uuid1","medium",null,null, GameStatus.FINISHED);
        Boolean result = underTest.update(gameToUpdate);
        assertThat(result).isTrue();
        Game updatedGame = underTest.getByUUID(gameToUpdate.getGameUUID());
        assertThat(updatedGame).usingRecursiveComparison().isEqualTo(gameToUpdate);
    }

    @Test
    void update_IfForeignKeyViolated_ShouldThrowDataAccessException() {
        Game gameToUpdate = new Game("uuid1","no such word", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        assertThatThrownBy(() -> underTest.update(gameToUpdate))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void update_IfNoGameToUpdate_ShouldNotAffectRows() {
        Game gameToUpdate = new Game("no such uuid","easy", LocalDateTime.parse("2021-07-01 10:10:10", formatter),null, GameStatus.NEW);
        Boolean result = underTest.update(gameToUpdate);
        assertThat(result).isFalse();
    }
}