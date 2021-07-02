package ru.nurmukhametovalexey.crocodile.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.nurmukhametovalexey.crocodile.model.Chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJdbcTest
@Sql({"classpath:user_schema.sql", "classpath:user_test_data.sql",
        "classpath:dictionary_schema.sql", "classpath:dictionary_test_data.sql",
        "classpath:game_schema.sql", "classpath:game_test_data.sql",
        "classpath:chat_schema.sql", "classpath:chat_test_data.sql"})
class ChatDAOTest {

    private final JdbcTemplate jdbcTemplate;
    private final ChatDAO underTest;
    private final DateTimeFormatter formatter;

    @Autowired
    public ChatDAOTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        underTest = new ChatDAO(jdbcTemplate);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Test
    void getChatByGameUUID() {
        List<Chat> expected = Arrays.asList(
                new Chat("message2","admin", "uuid2", LocalDateTime.parse("2021-07-01 10:12:10", formatter)),
                new Chat("message3","qwe", "uuid2", LocalDateTime.parse("2021-07-01 10:13:10", formatter))
        );
        List<Chat> result = underTest.getListByGameUUID("uuid2");
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getChatByGameUUID_IfNoChat_ShouldReturnEmptyList() {
        List<Chat> result = underTest.getListByGameUUID("no such uuid");
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getLastMessageByGameUUID() {
        Chat expected = new Chat("message3","qwe", "uuid2", LocalDateTime.parse("2021-07-01 10:13:10", formatter));
        Chat result = underTest.getLastMessageByGameUUID("uuid2");
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getLastMessageByGameUUID_IfNoChat_ShouldReturnNull() {
        Chat result = underTest.getLastMessageByGameUUID("no such uuid");
        assertThat(result).isNull();
    }

    @Test
    void save() {
        Chat chatToSave = new Chat("new message","qwe", "uuid2", LocalDateTime.parse("2021-07-01 10:14:10", formatter));
        Boolean result = underTest.save(chatToSave);
        assertThat(result).isTrue();
        Chat savedChat = underTest.getLastMessageByGameUUID(chatToSave.getGameUUID());
        assertThat(savedChat).usingRecursiveComparison().isEqualTo(chatToSave);
    }

    @Test
    void save_IfForeignKeyViolated_ShouldThrowDataAccessException() {
        Chat chatToSave = new Chat("new message","no such login", "uuid2", LocalDateTime.parse("2021-07-01 10:14:10", formatter));
        assertThatThrownBy(() -> underTest.save(chatToSave))
                .isInstanceOf(DataAccessException.class);

        Chat chatToSave2 = new Chat("new message","qwe", "no such uuid", LocalDateTime.parse("2021-07-01 10:14:10", formatter));
        assertThatThrownBy(() -> underTest.save(chatToSave2))
                .isInstanceOf(DataAccessException.class);
    }
}