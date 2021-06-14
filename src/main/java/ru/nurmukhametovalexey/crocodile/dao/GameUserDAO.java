package ru.nurmukhametovalexey.crocodile.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.nurmukhametovalexey.crocodile.model.GameUser;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;
import ru.nurmukhametovalexey.crocodile.model.User;

import java.lang.management.PlatformLoggingMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GameUserDAO {
    private final JdbcTemplate jdbcTemplate;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    @Autowired
    public GameUserDAO(JdbcTemplate jdbcTemplate,UserDAO userDAO, GameDAO gameDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    @Nullable
    public List<GameUser> getAll() {
        return  jdbcTemplate.query(
                "SELECT * FROM GameUser",
                new BeanPropertyRowMapper<>(GameUser.class)
        );
    }

    @Nullable
    public List<GameUser> getByGameUUID(String gameUUID) {
        return  jdbcTemplate.query(
                "SELECT * FROM GameUser WHERE gameUUID=?",
                new BeanPropertyRowMapper<>(GameUser.class),
                gameUUID
        );
    }

    @Nullable
    public Map<User, PlayerRole> getUsersByGameUUID(String gameUUID) {
        Map<User, PlayerRole> usersAndRoles = new HashMap<>();

        List<GameUser> gameUsers = getByGameUUID(gameUUID);
        for (GameUser gameUser: gameUsers) {
            User user = userDAO.getUserById(gameUser.getUserId());
            if (user != null) {
                usersAndRoles.put(user, gameUser.getPlayerRole());
            }
        }
        return usersAndRoles;
    }


    public boolean save(GameUser gameUser) {

        int rowsAffected = jdbcTemplate.update(
                "INSERT INTO GameUser(gameUUID, userId, playerRole)" +
                        "VALUES (?, ?, ?)",
                gameUser.getGameUUID(), gameUser.getUserId(), gameUser.getPlayerRole().toString()
        );
        log.info("gameUser save: {}, rows affected: {}", gameUser, rowsAffected);
        return rowsAffected!=0;
    }
}
