package ru.nurmukhametovalexey.crocodile.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.nurmukhametovalexey.crocodile.dao.*;
import ru.nurmukhametovalexey.crocodile.exception.DictionaryException;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@Service
public class DaoService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final GameUserDAO gameUserDAO;
    private  final DictionaryDAO dictionaryDAO;
    private final ChatDAO chatDAO;

    @Nullable
    public Integer getDifficultyByGameUUID(String gameUUID) throws
            GameNotFoundException, InvalidGameStateException, DictionaryException {
        Game game = gameDAO.getGameByUUID(gameUUID);

        if (game == null) {
            throw new GameNotFoundException("Game with uuid not found: " + gameUUID);
        }
        else if (game.getWord() == null) {
            throw new InvalidGameStateException("Game does not have secret word! uuid: " + gameUUID);
        }

        Integer difficulty = dictionaryDAO.getDifficultyByWord(game.getWord());

        if (difficulty == null) {
            throw new DictionaryException("Word not found: " + game.getWord());
        }

        return difficulty;
    }

    @Nullable
    public Integer getBountyByGameUUIDAndRole(String gameUUID, PlayerRole playerRole)
            throws InvalidGameStateException, GameNotFoundException, DictionaryException {
        Integer initialPoints = 1;
        if (playerRole == PlayerRole.GUESSER) {
            initialPoints *= gameUserDAO.getGameUserByGameUuidAndRole(gameUUID, PlayerRole.GUESSER).size();
        }
        Integer difficulty = getDifficultyByGameUUID(gameUUID);
        return initialPoints*difficulty;
    }

    public Chat saveChatMessage(String gameUUID, String login, String message) {
        Chat chatMessage = new Chat();
        chatMessage.setGameUUID(gameUUID);
        chatMessage.setLogin(login);
        chatMessage.setMessage(message);
        chatMessage.setTimeSent(LocalDateTime.now());
        chatDAO.save(chatMessage);
        return chatMessage;
    }

}
