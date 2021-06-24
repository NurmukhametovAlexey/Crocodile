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
import ru.nurmukhametovalexey.crocodile.model.Game;
import ru.nurmukhametovalexey.crocodile.model.GameUser;
import ru.nurmukhametovalexey.crocodile.model.PlayerRole;
import ru.nurmukhametovalexey.crocodile.model.User;

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

}
