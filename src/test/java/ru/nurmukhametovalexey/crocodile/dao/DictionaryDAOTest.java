package ru.nurmukhametovalexey.crocodile.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.nurmukhametovalexey.crocodile.model.Dictionary;
import ru.nurmukhametovalexey.crocodile.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJdbcTest
@Sql({"classpath:dictionary_schema.sql", "classpath:dictionary_test_data.sql"})
class DictionaryDAOTest {

    private JdbcTemplate jdbcTemplate;
    private DictionaryDAO dictionaryDAO;

    @Autowired
    public DictionaryDAOTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        dictionaryDAO = new DictionaryDAO(jdbcTemplate);
    }

    @Test
    void getAll() {

        Dictionary[] expected = new Dictionary[] {
                new Dictionary("easy", 1), new Dictionary("medium", 2), new Dictionary("hard", 3)
        };

        List<Dictionary> result = dictionaryDAO.getAll();
        assertThat(result.toArray()).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    @Test
    void getAll_IfNoWords_ShouldReturnEmptyList() {
        jdbcTemplate.update("delete from dictionary");
        List<Dictionary> result = dictionaryDAO.getAll();
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getWordsByDifficulty() {
        Dictionary[] expected = new Dictionary[] {new Dictionary("easy", 1)};
        List<Dictionary> result = dictionaryDAO.getWordsByDifficulty(1);
        assertThat(result.toArray()).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    @Test
    void getWordsByDifficulty_IfNoWords_ShouldReturnEmptyList() {
        List<Dictionary> result = dictionaryDAO.getWordsByDifficulty(-1);
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getRandomWordByDifficulty() {
        Dictionary expected = new Dictionary("easy", 1);
        Dictionary result = dictionaryDAO.getRandomWordByDifficulty(1);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getRandomWordByDifficulty_IfNoWords_ShouldReturnNull() {
        Dictionary result = dictionaryDAO.getRandomWordByDifficulty(-1);
        assertThat(result).isNull();
    }

    @Test
    void getDifficultyByWord() {
        Integer expected = 1;
        Integer result = dictionaryDAO.getDifficultyByWord("easy");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getDifficultyByWord_IfNoWord_ShouldReturnNull() {
        Integer result = dictionaryDAO.getDifficultyByWord("no such word");
        assertThat(result).isNull();
    }
}