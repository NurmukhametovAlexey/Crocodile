package ru.nurmukhametovalexey.crocodile.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.nurmukhametovalexey.crocodile.model.Dictionary;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJdbcTest
@Sql({"classpath:dictionary_schema.sql", "classpath:dictionary_test_data.sql"})
class DictionaryDAOTest {

    private final JdbcTemplate jdbcTemplate;
    private final DictionaryDAO underTest;

    @Autowired
    public DictionaryDAOTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        underTest = new DictionaryDAO(jdbcTemplate);
    }

    @Test
    void getAll() {
        Dictionary[] expected = new Dictionary[] {
                new Dictionary("easy", 1), new Dictionary("medium", 2), new Dictionary("hard", 3)
        };
        List<Dictionary> result = underTest.getAll();
        assertThat(result.toArray()).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    @Test
    void getAll_IfNoWords_ShouldReturnEmptyList() {
        jdbcTemplate.update("delete from dictionary");
        List<Dictionary> result = underTest.getAll();
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getWordsByDifficulty() {
        Dictionary[] expected = new Dictionary[] {new Dictionary("easy", 1)};
        List<Dictionary> result = underTest.getListByDifficulty(1);
        assertThat(result.toArray()).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    @Test
    void getWordsByDifficulty_IfNoWords_ShouldReturnEmptyList() {
        List<Dictionary> result = underTest.getListByDifficulty(-1);
        assertThat(result.toArray()).isEmpty();
    }

    @Test
    void getRandomWordByDifficulty() {
        Dictionary expected = new Dictionary("easy", 1);
        Dictionary result = underTest.getRandomWordByDifficulty(1);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getRandomWordByDifficulty_IfNoWords_ShouldReturnNull() {
        Dictionary result = underTest.getRandomWordByDifficulty(-1);
        assertThat(result).isNull();
    }

    @Test
    void getDifficultyByWord() {
        Integer expected = 1;
        Integer result = underTest.getDifficultyByWord("easy");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getDifficultyByWord_IfNoWord_ShouldReturnNull() {
        Integer result = underTest.getDifficultyByWord("no such word");
        assertThat(result).isNull();
    }
}