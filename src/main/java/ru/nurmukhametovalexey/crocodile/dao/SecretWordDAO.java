package ru.nurmukhametovalexey.crocodile.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.nurmukhametovalexey.crocodile.model.SecretWord;
import ru.nurmukhametovalexey.crocodile.model.User;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class SecretWordDAO {

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public SecretWordDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SecretWord> getAll() {
        return  jdbcTemplate.query(
                "SELECT * FROM Dictionary",
                new BeanPropertyRowMapper<>(SecretWord.class)
        );
    }

    public List<SecretWord> getWordsByDifficulty(int difficulty) {
        return  jdbcTemplate.query(
                "SELECT * FROM Dictionary WHERE difficulty=?",
                new BeanPropertyRowMapper<>(SecretWord.class),
                difficulty
        );
    }

    public SecretWord getRandomWordByDifficulty(int difficulty) {
       List<SecretWord> secretWordList = getWordsByDifficulty(difficulty);
       return secretWordList.get(ThreadLocalRandom.current().nextInt(0,secretWordList.size()));
    }
}
