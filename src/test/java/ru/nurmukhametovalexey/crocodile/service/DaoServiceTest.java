package ru.nurmukhametovalexey.crocodile.service;

import org.junit.jupiter.api.Test;
import ru.nurmukhametovalexey.crocodile.model.Chat;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DaoServiceTest {



    @Test
    void getUserByLogin() {
    }

    @Test
    void saveUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void testUpdateUser() {
    }

    @Test
    void getAllOrderByScoresDesc() {
    }

    @Test
    void getGameByUUID() {
    }

    @Test
    void getActiveGameUuidByLogin() {
    }

    @Test
    void saveGame() {
    }

    @Test
    void updateGame() {
    }

    @Test
    void getGameUserByGameUuidAndLogin() {
    }

    @Test
    void getGameUserByGameUuidAndRole() {
    }

    @Test
    void saveGameUser() {
    }

    @Test
    void deleteGameUser() {
    }

    @Test
    void getChatListByGameUUID() {
    }

    @Test
    void saveChatMessage() {

        /*@Test
        void saveChatMessage() {
            Chat result = underTest.saveChatMessage("uuid2", "admin", "hello");
            Chat savedChat = underTest.getChatDAO().getLastMessageByGameUUID("uuid2");
            assertThat(result).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(savedChat);
        }*/
    }

    @Test
    void getRandomWordByDifficulty() {
    }

    @Test
    void getGameHistoryByLogin() {
    }

    @Test
    void getBountyByGameUUIDAndRole() {
    }
}