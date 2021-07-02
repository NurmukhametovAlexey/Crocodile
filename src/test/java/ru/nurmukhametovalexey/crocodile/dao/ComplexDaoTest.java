package ru.nurmukhametovalexey.crocodile.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.nurmukhametovalexey.crocodile.model.Chat;
import ru.nurmukhametovalexey.crocodile.model.GameHistory;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJdbcTest
@Sql({"classpath:user_schema.sql", "classpath:user_test_data.sql",
        "classpath:dictionary_schema.sql", "classpath:dictionary_test_data.sql",
        "classpath:game_schema.sql", "classpath:game_test_data.sql",
        "classpath:gameuser_schema.sql", "classpath:gameuser_test_data.sql",
        "classpath:chat_schema.sql", "classpath:chat_test_data.sql"})
class ComplexDaoTest {

    private final JdbcTemplate jdbcTemplate;
    private final ComplexDao underTest;

    @Autowired
    public ComplexDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        underTest = new ComplexDao(jdbcTemplate, new UserDAO(jdbcTemplate), new GameDAO(jdbcTemplate),
                new GameUserDAO(jdbcTemplate), new DictionaryDAO(jdbcTemplate), new ChatDAO(jdbcTemplate));
    }

    @Test
    void getDifficultyByGameUUID() {
        Integer expected = 1;
        Integer result = underTest.getDifficultyByGameUUID("uuid1");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getDifficultyByGameUUID_IfNoGame_ShouldReturnNull() {
        Integer result = underTest.getDifficultyByGameUUID("no such uuid");
        assertThat(result).isNull();
    }

    @Test
    void getBountyByGameUUIDAndRole() {
        Integer result = underTest.getBountyByGameUUIDAndRole("uuid1", PlayerRole.PAINTER);
        assertThat(result).isEqualTo(1);

        Integer result2 = underTest.getBountyByGameUUIDAndRole("uuid2", PlayerRole.GUESSER);
        assertThat(result2).isEqualTo(2);
    }

    @Test
    void getBountyByGameUUIDAndRole_IfNoGame_ShouldReturnNull() {
        Integer result = underTest.getBountyByGameUUIDAndRole("no such uuid", PlayerRole.GUESSER);
        assertThat(result).isNull();
    }

    @Test
    void getActiveGameUuidByLogin() {
        String result = underTest.getActiveGameUuidByLogin("admin");
        assertThat(result).isEqualTo("uuid1");
    }

    @Test
    void getActiveGameUuidByLogin_IfNoGames_ShouldReturnNull() {
        String result = underTest.getActiveGameUuidByLogin("qwe");
        assertThat(result).isNull();
    }

    @Test
    void getActiveGameUuidByLogin_IfNoLogin_ShouldReturnNull() {
        String result = underTest.getActiveGameUuidByLogin("no such login");
        assertThat(result).isNull();
    }

    @Test
    void getGameHistoryByLogin() {
        List<GameHistory> result = underTest.getGameHistoryByLogin("qwe");
        List<GameHistory> expected = Arrays.asList(
                new GameHistory("01-07-21 10:10", "01-07-21 10:12", "uuid2", PlayerRole.PAINTER, true)
        );
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}