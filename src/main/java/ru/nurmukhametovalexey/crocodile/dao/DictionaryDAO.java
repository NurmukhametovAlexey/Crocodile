package ru.nurmukhametovalexey.crocodile.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.nurmukhametovalexey.crocodile.model.Dictionary;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class DictionaryDAO {

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public DictionaryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Dictionary> getAll() {
        return  jdbcTemplate.query(
                "SELECT * FROM Dictionary",
                new BeanPropertyRowMapper<>(Dictionary.class)
        );
    }

    public List<Dictionary> getWordsByDifficulty(int difficulty) {
        return  jdbcTemplate.query(
                "SELECT * FROM Dictionary WHERE difficulty=?",
                new BeanPropertyRowMapper<>(Dictionary.class),
                difficulty
        );
    }

    @Nullable
    public Dictionary getRandomWordByDifficulty(int difficulty) {
       List<Dictionary> dictionaryList = getWordsByDifficulty(difficulty);
       try {
           return dictionaryList.get(ThreadLocalRandom.current().nextInt(0, dictionaryList.size()));
       } catch (IllegalArgumentException e) {
           return null;
       }

    }

    @Nullable
    public Integer getDifficultyByWord(String word) {
        Dictionary dictionary = jdbcTemplate.query(
                "SELECT * FROM Dictionary WHERE word=?",
                new BeanPropertyRowMapper<>(Dictionary.class),
                word
        ).stream().findAny().orElse(null);

        if (dictionary == null) {
            return null;
        }

        return dictionary.getDifficulty();
    }
}
