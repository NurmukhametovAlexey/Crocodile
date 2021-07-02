package ru.nurmukhametovalexey.crocodile.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.nurmukhametovalexey.crocodile.model.Chat;

import java.util.List;

@Slf4j
@Component
public class ChatDAO {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public ChatDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nullable
    public List<Chat> getListByGameUUID(String gameUUID) {
        return jdbcTemplate.query(
                "SELECT * FROM chat WHERE gameUUID=? ORDER BY timeSent ASC",
                new BeanPropertyRowMapper<>(Chat.class),
                gameUUID
        );
    }

    @Nullable
    public Chat getLastMessageByGameUUID(String gameUUID) {
        return jdbcTemplate.query(
                "SELECT * FROM chat WHERE gameUUID=? ORDER BY timeSent DESC LIMIT 1",
                new BeanPropertyRowMapper<>(Chat.class),
                gameUUID).stream()
                .findAny()
                .orElse(null);
    }

    public boolean save(Chat chatMessage) {
        int rowsAffected = jdbcTemplate.update(
                "INSERT INTO Chat (message, login, gameUUID, timeSent)" +
                        "VALUES (?, ?, ?, ?)",
                chatMessage.getMessage(), chatMessage.getLogin(), chatMessage.getGameUUID(), chatMessage.getTimeSent()
        );
        log.info("chatMessage save: {}, rows affected: {}", chatMessage, rowsAffected);
        return rowsAffected!=0;
    }

}
