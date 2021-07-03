package ru.nurmukhametovalexey.crocodile.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.nurmukhametovalexey.crocodile.exception.DictionaryException;
import ru.nurmukhametovalexey.crocodile.exception.GameNotFoundException;
import ru.nurmukhametovalexey.crocodile.exception.InvalidGameStateException;
import ru.nurmukhametovalexey.crocodile.exception.UserNotFoundException;
import ru.nurmukhametovalexey.crocodile.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Getter
@Setter
@AllArgsConstructor
@Service
public class GameService {
    private final DaoService daoService;
    private final Environment env;

    public Game createGame(String creatorLogin, Integer difficulty)
            throws UserNotFoundException, DictionaryException {

        if(creatorLogin == null || difficulty == null) {
            throw new IllegalArgumentException("login and difficulty cannot be null!");
        }
        else if(daoService.getUserByLogin(creatorLogin) == null) {
            throw new UserNotFoundException("User with name " + creatorLogin + " doesnt exist!");
        }

        Dictionary dictionary = daoService.getRandomWordByDifficulty(difficulty);

        if(dictionary == null) {
            throw new DictionaryException("No words with difficulty " + difficulty);
        }

        Game game = daoService.saveGame(dictionary.getWord());
        daoService.saveGameUser(game.getGameUUID(), creatorLogin, PlayerRole.PAINTER);
        daoService.saveChatMessage(game.getGameUUID(), creatorLogin, "joined as painter!");

        return game;
    }

    public Game connectByUUID(String gameUUID, String newPlayerLogin, PlayerRole playerRole)
            throws GameNotFoundException, InvalidGameStateException, UserNotFoundException {

        if (daoService.getUserByLogin(newPlayerLogin) == null) {
            throw new UserNotFoundException("User with name " + newPlayerLogin + " doesnt exist!");
        }

        Game game = daoService.getGameByUUID(gameUUID);
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
                daoService.getGameUserByGameUuidAndRole(gameUUID, PlayerRole.PAINTER) != null) {
            throw new InvalidGameStateException("This game already has a painter!");
        }

        if (daoService.getGameUserByGameUuidAndLogin(gameUUID, newPlayerLogin) == null) {
            log.info("Player reconnecting... " + newPlayerLogin);
            daoService.saveGameUser(gameUUID, newPlayerLogin, playerRole);
        }

        String joinMessage = "joined as " + playerRole.toString().toLowerCase();
        daoService.saveChatMessage(gameUUID, newPlayerLogin, joinMessage);

        return game;
    }

    public Game gamePlay(Chat message) throws GameNotFoundException, InvalidGameStateException {

        Game game = daoService.getGameByUUID(message.getGameUUID());
        if(game == null) {
            throw new GameNotFoundException("Game not found: " + message.getGameUUID());
        }
        else if (game.getStatus() == GameStatus.FINISHED || game.getStatus() == GameStatus.CANCELLED) {
            throw new InvalidGameStateException("Game is finished or cancelled: " + message.getGameUUID());
        }

        if(message.getMessage().equalsIgnoreCase(game.getWord()) && game.getStatus() == GameStatus.IN_PROGRESS) {
            log.info("PLAYERS WON!!!");

            daoService.saveChatMessage(message.getGameUUID(), message.getLogin(), "wins!");

            game.setStatus(GameStatus.FINISHED);
            game.setTimeFinished(LocalDateTime.now());
            daoService.updateGame(game);

        }
        return game;
    }

    public List<String> loadChat(String gameUUID) throws GameNotFoundException {

        List<Chat> chat = daoService.getChatListByGameUUID(gameUUID);
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

    public Map<String, Integer> increaseWinnersScore(String gameUUID, String guesserLogin) throws InvalidGameStateException {
        Game game = daoService.getGameByUUID(gameUUID);
        GameUser gamePainter = daoService.getGameUserByGameUuidAndRole(gameUUID, PlayerRole.PAINTER).stream()
                .findAny().orElseThrow(() -> new InvalidGameStateException("Game doesn`t have a painter!"));

        GameUser gameGuesser = daoService.getGameUserByGameUuidAndLogin(game.getGameUUID(), guesserLogin);
        if (gameGuesser == null) {
            throw new InvalidGameStateException("Game doesn`t have a winning guesser!");
        }

        Map<String, Integer> roleBountyMap = new HashMap<>();

        User painter = daoService.getUserByLogin(gamePainter.getLogin());
        Integer painterBounty = daoService.getBountyByGameUUIDAndRole(gameUUID, PlayerRole.PAINTER);
        painter.increaseScore(painterBounty);
        log.info("Painter: {} has score {}",painter,painter.getScore());
        daoService.updateUser(painter);
        roleBountyMap.put("Painter " + painter.getLogin(), painterBounty);

        User guesser = daoService.getUserByLogin(gameGuesser.getLogin());
        Integer guesserBounty = daoService.getBountyByGameUUIDAndRole(gameUUID, PlayerRole.GUESSER);
        guesser.increaseScore(guesserBounty);
        log.info("Guesser: {} has score {}",guesser,guesser.getScore());
        daoService.updateUser(guesser);
        roleBountyMap.put("Guesser " + guesser.getLogin(), guesserBounty);

        return roleBountyMap;
    }

    public void uploadCanvasImage(String rawBase64Image, String gameUUID) throws IOException {
        String canvasStorageLocation = env.getProperty("canvas.storage-location");
        log.info("canvasStorageLocation is {}", canvasStorageLocation);

        String image = rawBase64Image.split(",")[1];
        byte[] img = Base64.getDecoder().decode(image);
        Files.write(Paths.get(canvasStorageLocation,gameUUID+".png"), img);
    }

    @Nullable
    public String downloadCanvasImage(String gameUUID) {
        log.info("downloading image: {}", gameUUID);
        String canvasStorageLocation = env.getProperty("canvas.storage-location");
        try {
            byte[] img = Files.readAllBytes(Paths.get(canvasStorageLocation,gameUUID+".png"));
            String base64Image = Base64.getEncoder().encodeToString(img);
            return base64Image;
        } catch (IOException e) {
            log.info("unable to open canvas image: {}", gameUUID);
            return null;
        }
    }

    public Map<String, Integer> getPlayersCount(String gameUUID) {
        Map<String, Integer> playerCountMap = new HashMap<>();
        String painter = PlayerRole.PAINTER.toString();
        String guesser = PlayerRole.GUESSER.toString();
        playerCountMap.put(painter, 0);
        playerCountMap.put(guesser, 0);

        daoService.getGameUserListByGameUuid(gameUUID).stream()
            .forEach(gameUser -> {
                if(gameUser.getPlayerRole().equals(PlayerRole.PAINTER)){
                    playerCountMap.replace(painter, playerCountMap.get(painter) + 1);
                } else {
                    playerCountMap.replace(guesser, playerCountMap.get(guesser) + 1);
                }
            });
        log.info("players count map: {}", playerCountMap);
        return  playerCountMap;
    }
}
