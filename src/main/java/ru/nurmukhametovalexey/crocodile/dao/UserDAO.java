package ru.nurmukhametovalexey.crocodile.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.nurmukhametovalexey.crocodile.model.User;

import java.util.List;

@Slf4j
@Component
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nullable
    public List<User> getAll() {
        return  jdbcTemplate.query(
                "SELECT * FROM \"User\"",
                new BeanPropertyRowMapper<>(User.class)
        );
    }

    @Nullable
    public User getUserByLogin(String login) {
        return jdbcTemplate.query(
                "SELECT * FROM \"User\" WHERE login=?",
                new BeanPropertyRowMapper<>(User.class),
                login
        ).stream()
                .findAny()
                .orElse(null);
    }

    @Nullable
    public User getUserById(int id) {
        return jdbcTemplate.query(
                "SELECT * FROM \"User\" WHERE userId=?",
                new BeanPropertyRowMapper<>(User.class),
                id
        ).stream()
                .findAny()
                .orElse(null);
    }

    public boolean save(User user) {

        int rowsAffected = jdbcTemplate.update(
                "INSERT INTO \"User\"(login, password, email, role, enabled)" +
                        "VALUES (?, ?, ?, ?, ?)",
                user.getLogin(), user.getPassword(), user.getEmail(), user.getRole(), user.isEnabled()
        );
        log.info("user save: {}, rows affected: {}", user, rowsAffected);
        return rowsAffected!=0;
    }
}
