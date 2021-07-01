package ru.nurmukhametovalexey.crocodile.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nurmukhametovalexey.crocodile.exception.DictionaryException;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.exception.UserNotFoundException;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Getter
@Setter
@AllArgsConstructor
@Service
public class GameService {
    private final DaoService daoService;

    public Game createGame(String creatorLogin, Integer difficulty)
            throws UserNotFoundException, DictionaryException {

        if(creatorLogin == null || difficulty == null) {
            throw new IllegalArgumentException("login and difficulty cannot be null!");
        }
        else if(daoService.getUserDAO().getUserByLogin(creatorLogin) == null) {
            throw new UserNotFoundException("User with name " + creatorLogin + " doesnt exist!");
        }

        Dictionary dictionary = daoService.getDictionaryDAO().getRandomWordByDifficulty(difficulty);

        if(dictionary == null) {
            throw new DictionaryException("No words with difficulty " + difficulty);
        }

        Game game = new Game();
        game.setGameUUID(UUID.randomUUID().toString());
        game.setWord(dictionary.getWord());
        game.setStatus(GameStatus.NEW);
        game.setTimeStarted(LocalDateTime.now());
        daoService.getGameDAO().save(game);

        GameUser gameUser = new GameUser();
        gameUser.setLogin(creatorLogin);
        gameUser.setGameUUID(game.getGameUUID());
        gameUser.setPlayerRole(PlayerRole.PAINTER);
        daoService.getGameUserDAO().save(gameUser);

        Chat chat = new Chat();
        chat.setTimeSent(LocalDateTime.now());
        chat.setMessage("joined as painter");
        chat.setGameUUID(game.getGameUUID());
        chat.setLogin(creatorLogin);
        daoService.getChatDAO().save(chat);

        return game;
    }

    public Game connectByUUID(String gameUUID, String newPlayerLogin, PlayerRole playerRole)
            throws GameNotFoundException, InvalidGameStateException, UserNotFoundException {

        if (daoService.getUserDAO().getUserByLogin(newPlayerLogin) == null) {
            throw new UserNotFoundException("User with name " + newPlayerLogin + " doesnt exist!");
        }

        Game game = daoService.getGameDAO().getGameByUUID(gameUUID);
        if (game == null) {
            throw new GameNotFoundException("Game with uuid not found: " + gameUUID);
        }
        else if (game.getStatus() == GameStatus.IN_PROGRESS) {
            throw new InvalidGameStateException("Game is already in progress: " + gameUUID);
        }
        else if (game.getStatus() == GameStatus.FINISHED || game.getStatus() == GameStatus.CANCELLED) {
            throw new InvalidGameStateException("Game is already finished: " + gameUUID);
        }
        else if (playerRole == PlayerRole.PAINTER &&
                daoService.getGameUserDAO().getGameUserByGameUuidAndRole(gameUUID, PlayerRole.PAINTER) != null) {
            throw new InvalidGameStateException("This game already has a painter!");
        }

        if (daoService.getGameUserDAO().getByGameUuidAndLogin(gameUUID, newPlayerLogin) == null) {
            log.info("Player reconnecting... " + newPlayerLogin);
            GameUser gameUser = new GameUser();
            gameUser.setLogin(newPlayerLogin);
            gameUser.setGameUUID(gameUUID);
            gameUser.setPlayerRole(playerRole);

            daoService.getGameUserDAO().save(gameUser);
        }

        Chat chat = new Chat();
        chat.setTimeSent(LocalDateTime.now());
        chat.setMessage("joined as guesser");
        chat.setGameUUID(game.getGameUUID());
        chat.setLogin(newPlayerLogin);
        daoService.getChatDAO().save(chat);

        return game;
    }

    public Game gamePlay(Chat message) throws GameNotFoundException, InvalidGameStateException {

        Game game = daoService.getGameDAO().getGameByUUID(message.getGameUUID());
        if(game == null) {
            throw new GameNotFoundException("Game not found: " + message.getGameUUID());
        }
        else if (game.getStatus() == GameStatus.FINISHED || game.getStatus() == GameStatus.CANCELLED) {
            throw new InvalidGameStateException("Game is finished or cancelled: " + message.getGameUUID());
        }

        if(message.getMessage().equalsIgnoreCase(game.getWord()) && game.getStatus() == GameStatus.IN_PROGRESS) {
            log.info("PLAYERS WON!!!");

            Chat chat = new Chat();
            chat.setTimeSent(LocalDateTime.now());
            chat.setMessage("wins!");
            chat.setGameUUID(game.getGameUUID());
            chat.setLogin(message.getLogin());
            daoService.getChatDAO().save(chat);

            game.setStatus(GameStatus.FINISHED);
            game.setTimeFinished(LocalDateTime.now());
            daoService.getGameDAO().update(game);

        }
        return game;
    }

    public List<String> loadChat(String gameUUID) throws GameNotFoundException {

        List<Chat> chat = daoService.getChatDAO().getChatByGameUUID(gameUUID);
        if(chat == null) {
            throw new GameNotFoundException("No chat for game, probably the game doesn`t exist! " + gameUUID);
        }

        return chat.stream()
                .map(this::chatMessageToString)
                .collect(Collectors.toList());
    }

    public String chatMessageToString(Chat message) {
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return message.getTimeSent().format(formatter) + "| " + message.getLogin()
                + ": " + message.getMessage() + "";
    }

    public Map<String, Integer> increaseWinnersScore(String gameuuid, String guesserLogin) throws InvalidGameStateException, GameNotFoundException, DictionaryException {
        Game game = daoService.getGameDAO().getGameByUUID(gameuuid);
        GameUser gamePainter = daoService.getGameUserDAO().getGameUserByGameUuidAndRole(
                game.getGameUUID(), PlayerRole.PAINTER).stream()
                .findAny().orElseThrow(() -> new InvalidGameStateException("Game doesn`t have a painter!"));

        GameUser gameGuesser = daoService.getGameUserDAO().getByGameUuidAndLogin(game.getGameUUID(), guesserLogin);
        if (gameGuesser == null) {
            throw new InvalidGameStateException("Game doesn`t have a winning guesser!");
        }

        Map<String, Integer> roleBountyMap = new HashMap<>();

        User painter = daoService.getUserDAO().getUserByLogin(gamePainter.getLogin());
        Integer painterBounty = daoService.getBountyByGameUUIDAndRole(gameuuid, PlayerRole.PAINTER);
        painter.increaseScore(painterBounty);
        log.info("Painter: {} has score {}",painter,painter.getScore());
        daoService.getUserDAO().update(painter);
        roleBountyMap.put("Painter " + painter.getLogin(), painterBounty);

        User guesser = daoService.getUserDAO().getUserByLogin(gameGuesser.getLogin());
        Integer guesserBounty = daoService.getBountyByGameUUIDAndRole(gameuuid, PlayerRole.GUESSER);
        guesser.increaseScore(guesserBounty);
        log.info("Guesser: {} has score {}",guesser,guesser.getScore());
        daoService.getUserDAO().update(guesser);
        roleBountyMap.put("Guesser " + guesser.getLogin(), guesserBounty);

        return roleBountyMap;
    }
}
