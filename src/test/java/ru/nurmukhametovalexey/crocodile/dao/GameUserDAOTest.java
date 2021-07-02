package ru.nurmukhametovalexey.crocodile.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.nurmukhametovalexey.crocodile.model.*;


import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJdbcTest
@Sql({"classpath:user_schema.sql", "classpath:user_test_data.sql",
        "classpath:dictionary_schema.sql", "classpath:dictionary_test_data.sql",
        "classpath:game_schema.sql", "classpath:game_test_data.sql",
        "classpath:gameuser_schema.sql", "classpath:gameuser_test_data.sql"})
class GameUserDAOTest {

    private final JdbcTemplate jdbcTemplate;
    private final GameUserDAO underTest;

    @Autowired
    public GameUserDAOTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        underTest = new GameUserDAO(jdbcTemplate);
    }

    @Test
    void getGameUserByGameUuidAndRole() {
        List<GameUser> expected = Arrays.asList(
                new GameUser("admin","uuid2", PlayerRole.GUESSER)
        );
        List<GameUser> result = underTest.getByGameUuidAndRole("uuid2", PlayerRole.GUESSER);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getGameUserByGameUuidAndRole_IfNoGameUser_ShouldReturnEmptyList() {
        List<GameUser> result = underTest.getByGameUuidAndRole("no such uuid", PlayerRole.GUESSER);
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getGameUserByLogin() {
        List<GameUser> expected = Arrays.asList(
                new GameUser("admin","uuid1", PlayerRole.PAINTER),
                new GameUser("admin","uuid2", PlayerRole.GUESSER)
        );
        List<GameUser> result = underTest.getByLogin("admin");
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getGameUserByLogin_IfNoGameUser_ShouldReturnEmptyList() {
        List<GameUser> result = underTest.getByLogin("no such login");
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getByGameUuidAndLogin() {
        GameUser expected = new GameUser("admin", "uuid1", PlayerRole.PAINTER);
        GameUser result = underTest.getByGameUuidAndLogin("uuid1", "admin");
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getByGameUuidAndLogin_IfNoGameUser_ShouldReturnNull() {
        GameUser result = underTest.getByGameUuidAndLogin("no such uuid", "no such login");
        assertThat(result).isNull();
    }

    @Test
    void save() {
        GameUser gameUserToSave = new GameUser("admin","uuid3", PlayerRole.PAINTER);
        Boolean result = underTest.save(gameUserToSave);
        assertThat(result).isTrue();
        GameUser savedGameUser = underTest.getByGameUuidAndLogin("uuid3", "admin");
        assertThat(savedGameUser).usingRecursiveComparison().isEqualTo(gameUserToSave);
    }

    @Test
    void save_IfPrimaryKeyViolated_ShouldThrowDataAccessException() {
        GameUser gameUserToSave = new GameUser("admin","uuid1", PlayerRole.GUESSER);
        assertThatThrownBy(() -> underTest.save(gameUserToSave))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void save_IfForeignKeyViolated_ShouldThrowDataAccessException() {
        GameUser gameUserToSave = new GameUser("no such login","no such uuid", PlayerRole.GUESSER);
        assertThatThrownBy(() -> underTest.save(gameUserToSave))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void update() {
        GameUser gameUserToUpdate = new GameUser("admin","uuid1", PlayerRole.GUESSER);
        Boolean result = underTest.update(gameUserToUpdate);
        assertThat(result).isTrue();
        GameUser updatedGameUser = underTest.getByGameUuidAndLogin("uuid1", "admin");
        assertThat(updatedGameUser).usingRecursiveComparison().isEqualTo(gameUserToUpdate);
    }

    @Test
    void update_IfNoGameUserToUpdate_ShouldNotAffectRows() {
        GameUser gameUserToUpdate = new GameUser("no such login","uuid1", PlayerRole.PAINTER);
        Boolean result = underTest.update(gameUserToUpdate);
        assertThat(result).isFalse();
    }

    @Test
    void delete() {
        GameUser gameUserToDelete = underTest.getByGameUuidAndLogin("uuid1", "admin");
        Boolean result = underTest.delete(gameUserToDelete);
        assertThat(result).isTrue();
        GameUser deletedGameUser = underTest.getByGameUuidAndLogin("uuid1", "admin");
        assertThat(deletedGameUser).isNull();
    }

    @Test
    void delete_IfNoGameUserToDelete_ShouldNotAffectRows() {
        GameUser gameUserToDelete = underTest.getByGameUuidAndLogin("no such login", "admin");
        Boolean result = underTest.delete(gameUserToDelete);
        assertThat(result).isFalse();
    }
}