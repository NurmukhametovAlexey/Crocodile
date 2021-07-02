package ru.nurmukhametovalexey.crocodile.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nurmukhametovalexey.crocodile.dao.*;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@Service
public class DaoService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final GameUserDAO gameUserDAO;
    private final DictionaryDAO dictionaryDAO;
    private final ChatDAO chatDAO;
    private final ComplexDao complexDao;
    private final PasswordEncoder passwordEncoder;

    @Nullable
    public User getUserByLogin(String login) {
        return userDAO.getByLogin(login);
    }

    public User saveUser(String login, String password, String email, String role) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(role);
        user.setScore(0);
        user.setEnabled(true);
        userDAO.save(user);
        return user;
    }

    public User updateUser(String login, String newPassword, String newEmail) {

        User initialUser = userDAO.getByLogin(login);
        if (newPassword != null && !newPassword.isBlank()) {
            initialUser.setPassword(passwordEncoder.encode(newPassword));
        }
        if (newEmail != null && !newEmail.isBlank()) {
            initialUser.setEmail(newEmail);
        }
        userDAO.update(initialUser);
        return initialUser;
    }

    public boolean updateUser(User user) {
        return userDAO.update(user);
    }

    public List<User> getAllUsersOrderByScoresDesc() {
        return  userDAO.getAllOrderByScoresDesc();
    }

    @Nullable
    public Game getGameByUUID(String gameUUID) {
        return gameDAO.getByUUID(gameUUID);
    }

    @Nullable
    public String getActiveGameUuidByLogin(String login) {
        return complexDao.getActiveGameUuidByLogin(login);
    }

    public Game saveGame(String word) {
        Game game = new Game();
        game.setGameUUID(UUID.randomUUID().toString());
        game.setWord(word);
        game.setStatus(GameStatus.NEW);
        game.setTimeStarted(LocalDateTime.now());
        gameDAO.save(game);
        return game;
    }

    public boolean updateGame(Game game) {
        return gameDAO.update(game);
    }

    @Nullable
    public GameUser getGameUserByGameUuidAndLogin(String gameUUID, String login) {
        return gameUserDAO.getByGameUuidAndLogin(gameUUID, login);
    }

    public List<GameUser> getGameUserByGameUuidAndRole(String gameUUID, PlayerRole playerRole) {
        return gameUserDAO.getByGameUuidAndRole(gameUUID, playerRole);
    }

    public GameUser saveGameUser(String gameUUID, String login, PlayerRole role) {
        GameUser gameUser = new GameUser();
        gameUser.setLogin(login);
        gameUser.setGameUUID(gameUUID);
        gameUser.setPlayerRole(role);
        gameUserDAO.save(gameUser);
        return gameUser;
    }

    public boolean deleteGameUser(GameUser gameUser) {
        return gameUserDAO.delete(gameUser);
    }

    @Nullable
    public List<Chat> getChatListByGameUUID(String gameUUID) {
        return chatDAO.getListByGameUUID(gameUUID);
    }

    public Chat saveChatMessage(String gameUUID, String login, String message) {
        Chat chatMessage = new Chat();
        chatMessage.setGameUUID(gameUUID);
        chatMessage.setLogin(login);
        chatMessage.setMessage(message);
        chatMessage.setTimeSent (LocalDateTime.now());
        chatDAO.save(chatMessage);
        return chatMessage;
    }

    @Nullable
    public Dictionary getRandomWordByDifficulty(Integer difficulty) {
        return dictionaryDAO.getRandomWordByDifficulty(difficulty);
    }

    public List<GameHistory> getGameHistoryByLogin(String login) {
        return complexDao.getGameHistoryByLogin(login);
    }

    @Nullable
    public Integer getBountyByGameUUIDAndRole(String gameUUID, PlayerRole playerRole) {
        return complexDao.getBountyByGameUUIDAndRole(gameUUID, playerRole);
    }

}
