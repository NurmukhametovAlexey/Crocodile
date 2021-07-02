package ru.nurmukhametovalexey.crocodile.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.nurmukhametovalexey.crocodile.dao.*;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DaoServiceTest {
    @Mock private UserDAO userDAO;
    @Mock private GameDAO gameDAO;
    @Mock private GameUserDAO gameUserDAO;
    @Mock private DictionaryDAO dictionaryDAO;
    @Mock private ChatDAO chatDAO;
    @Mock private ComplexDao complexDao;
    @Mock private PasswordEncoder passwordEncoder;

    private DaoService underTest;

    @BeforeEach
    void setUp() {
        underTest = new DaoService(userDAO, gameDAO, gameUserDAO, dictionaryDAO, chatDAO, complexDao, passwordEncoder);
    }

    @Test
    void getUserByLogin() {
        //given
        String login = "login";
        //when
        underTest.getUserByLogin(login);
        //then
        verify(userDAO).getByLogin(login);
    }

    @Test
    void saveUser() {
        //given
        String login = "login";
        String password = "password";
        String email = "email";
        String role = "role";
        String encodedPassword = "encoded password";
        User expected = new User(login, encodedPassword, email, role, 0, true);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        //when
        underTest.saveUser(login, password, email, role);
        //then
        verify(userDAO).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void updateUserWithStringArguments() {
        //given
        String newPassword = "not blank";
        String newEmail = "not blank";
        String encodedPassword = "encoded password 2";
        User user = new User("login", "encoded password", "email", "role", 0, true);
        User expected = new User("login", encodedPassword, newEmail, "role", 0, true);
        given(userDAO.getByLogin(user.getLogin())).willReturn(user);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        //when
        underTest.updateUser(user.getLogin(), newPassword, newEmail);
        //then
        verify(userDAO).getByLogin(user.getLogin());
        verify(passwordEncoder).encode(newPassword);
        verify(userDAO).update(userArgumentCaptor.capture());
        User updatedUser = userArgumentCaptor.getValue();
        assertThat(updatedUser).usingRecursiveComparison().isEqualTo(expected);

    }

    @Test
    void updateUserWithStringArguments_IfNoPasswordOrEmailGiven_ShouldNotChangeIt() {
        //given
        String blankPassword = "   ";
        String blankEmail = "   ";
        String encodedPassword = "encoded password 2";
        User user = new User("login", "encoded password", "email", "role", 0, true);
        User expected = new User("login", "encoded password", "email", "role", 0, true);
        given(userDAO.getByLogin(user.getLogin())).willReturn(user);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        //when
        underTest.updateUser(user.getLogin(), blankPassword, blankEmail);
        //then
        verify(userDAO).getByLogin(user.getLogin());
        verify(passwordEncoder, never()).encode(blankPassword);
        verify(userDAO).update(userArgumentCaptor.capture());
        User updatedUser = userArgumentCaptor.getValue();
        assertThat(updatedUser).usingRecursiveComparison().isEqualTo(expected);

    }


    @Test
    void updateUserWithUserArgument() {
        //given
        User user = new User("login", "encoded password", "email", "role", 0, true);
        //when
        underTest.updateUser(user);
        //then
        verify(userDAO).update(user);
    }

    @Test
    void getAllUsersOrderByScoresDesc() {
        //given
        //when
        underTest.getAllUsersOrderByScoresDesc();
        //then
        verify(userDAO).getAllOrderByScoresDesc();
    }

    @Test
    void getGameByUUID() {
        //given
        String uuid = "uuid";
        //when
        underTest.getGameByUUID(uuid);
        //then
        verify(gameDAO).getByUUID(uuid);
    }

    @Test
    void getActiveGameUuidByLogin() {
        //given
        String login = "login";
        //when
        underTest.getActiveGameUuidByLogin(login);
        //then
        verify(complexDao).getActiveGameUuidByLogin(login);
    }

    @Test
    void saveGame() {
        //given
        String word = "word";
        Integer uuidLength = 36;
        //when
        Game game = underTest.saveGame(word);
        //then
        verify(gameDAO).save(game);
        assertThat(game.getWord()).isEqualTo(word);
        assertThat(game.getStatus()).isEqualTo(GameStatus.NEW);
        assertThat(game.getGameUUID().length()).isEqualTo(uuidLength);
    }

    @Test
    void updateGame() {
        //given
        Game game = new Game("uuid", "word", null, null, GameStatus.IN_PROGRESS);
        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        given(gameDAO.update(game)).willReturn(true);
        //when
        boolean result = underTest.updateGame(game);
        //then
        verify(gameDAO).update(gameArgumentCaptor.capture());
        Game updatedGame = gameArgumentCaptor.getValue();
        assertThat(updatedGame).usingRecursiveComparison().isEqualTo(game);
        assertThat(result).isTrue();
    }

    @Test
    void getGameUserByGameUuidAndLogin() {
        //given
        String uuid = "uuid";
        String login = "login";
        //when
        underTest.getGameUserByGameUuidAndLogin(uuid, login);
        //then
        verify(gameUserDAO).getByGameUuidAndLogin(uuid, login);
    }

    @Test
    void getGameUserByGameUuidAndRole() {
        //given
        String uuid = "uuid";
        PlayerRole role = PlayerRole.PAINTER;
        //when
        underTest.getGameUserByGameUuidAndRole(uuid, role);
        //then
        verify(gameUserDAO).getByGameUuidAndRole(uuid, role);
    }

    @Test
    void saveGameUser() {
        //given
        GameUser gameUser = new GameUser("uuid", "login", PlayerRole.PAINTER);
        ArgumentCaptor<GameUser> gameUserArgumentCaptor = ArgumentCaptor.forClass(GameUser.class);
        //when
        underTest.saveGameUser(gameUser.getGameUUID(), gameUser.getLogin(), gameUser.getPlayerRole());
        //then
        verify(gameUserDAO).save(gameUserArgumentCaptor.capture());
        GameUser savedUser = gameUserArgumentCaptor.getValue();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(gameUser);
    }

    @Test
    void deleteGameUser() {
        //given
        GameUser gameUser = new GameUser("uuid", "login", PlayerRole.PAINTER);
        //when
        underTest.deleteGameUser(gameUser);
        //then
        verify(gameUserDAO).delete(gameUser);
    }

    @Test
    void getChatListByGameUUID() {
        //given
        String gameUUID = "uuid";
        //when
        underTest.getChatListByGameUUID(gameUUID);
        //then
        verify(chatDAO).getListByGameUUID(gameUUID);
    }

    @Test
    void saveChatMessage() {
        //given
        Chat chat = new Chat("message", "login", "uuid", null);
        //when
        Chat savedChat = underTest.saveChatMessage(chat.getGameUUID(), chat.getLogin(), chat.getMessage());
        //then
        verify(chatDAO).save(savedChat);
        assertThat(savedChat).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(chat);
    }

    @Test
    void getRandomWordByDifficulty() {
        //given
        Integer difficulty = 1;
        //when
        underTest.getRandomWordByDifficulty(difficulty);
        //then
        verify(dictionaryDAO).getRandomWordByDifficulty(difficulty);
    }

    @Test
    void getGameHistoryByLogin() {
        //given
        String login = "login";
        //when
        underTest.getGameHistoryByLogin(login);
        //then
        verify(complexDao).getGameHistoryByLogin(login);
    }

    @Test
    void getBountyByGameUUIDAndRole() {
        //given
        String uuid = "uuid";
        PlayerRole role = PlayerRole.PAINTER;
        //when
        underTest.getBountyByGameUUIDAndRole(uuid, role);
        //then
        verify(complexDao).getBountyByGameUUIDAndRole(uuid, role);
    }
}