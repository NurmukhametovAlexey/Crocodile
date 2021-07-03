package ru.nurmukhametovalexey.crocodile.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.nurmukhametovalexey.crocodile.model.GameUser;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;

import java.util.List;

@Slf4j
@Component
public class GameUserDAO {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public GameUserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GameUser> getListByGameUuid(String gameUUID) {
        return jdbcTemplate.query(
                "SELECT * FROM GameUser WHERE gameUUID=?",
                new BeanPropertyRowMapper<>(GameUser.class),
                gameUUID
        );
    }

    public List<GameUser> getByGameUuidAndRole(String gameUUID, PlayerRole playerRole) {
        return jdbcTemplate.query(
                "SELECT * FROM GameUser WHERE gameUUID=? AND playerRole=?::playerstatus",
                new BeanPropertyRowMapper<>(GameUser.class),
                gameUUID, playerRole.toString()
        );
    }

    public List<GameUser> getByLogin(String login) {
        return jdbcTemplate.query(
                "SELECT * FROM GameUser WHERE login=?",
                new BeanPropertyRowMapper<>(GameUser.class),
                login
        );
    }

    @Nullable
    public GameUser getByGameUuidAndLogin(String gameUUID, String login) {

        return jdbcTemplate.query(
               "SELECT * FROM GameUser WHERE gameUUID=? AND login=?",
               new BeanPropertyRowMapper<>(GameUser.class),
               gameUUID, login
       ).stream().findAny().orElse(null);
    }

    public boolean save(GameUser gameUser) {
        int rowsAffected = jdbcTemplate.update(
                "INSERT INTO GameUser(login, gameUUID, playerRole) VALUES (?, ?, ?::playerStatus)",
                gameUser.getLogin(), gameUser.getGameUUID(), gameUser.getPlayerRole().toString()
        );
        log.info("gameUser save: {}, rows affected: {}", gameUser, rowsAffected);

        return rowsAffected!=0;
    }

    public boolean update(GameUser gameUser) {
        int rowsAffected = jdbcTemplate.update(
                "UPDATE GameUser SET playerRole=?::playerStatus WHERE gameUUID=? AND login=?",
                gameUser.getPlayerRole().toString(), gameUser.getGameUUID(), gameUser.getLogin()
        );
        log.info("gameUser update: {}, rows affected: {}", gameUser, rowsAffected);

        return rowsAffected!=0;
    }

    public boolean delete(GameUser gameUser) {

        if(gameUser == null) {
            return false;
        }

        int rowsAffected = jdbcTemplate.update(
                "DELETE FROM GameUser WHERE gameUUID=? AND login=?",
                gameUser.getGameUUID(), gameUser.getLogin()
        );
        log.info("gameUser delete: {}, rows affected: {}", gameUser, rowsAffected);

        return rowsAffected!=0;
    }

}
