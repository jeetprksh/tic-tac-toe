package com.jeetprksh.game.tictactoe;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.jeetprksh.game.tictactoe.message.*;
import com.jeetprksh.game.tictactoe.game.*;
import com.jeetprksh.game.tictactoe.pojo.GameInfo;
import com.jeetprksh.game.tictactoe.pojo.PlayerInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

  private final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

  private final List<TicTacToe> games = new ArrayList<>();

  private final Map<Player, WebSocketSession> playerSessions = new ConcurrentHashMap<>();

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    String payload = message.getPayload();
    logger.info("Received message: " + payload);
    try {
      String messageType = JsonParser.parseString(payload).getAsJsonObject().get("messageType").getAsString();
      if (messageType.equals(MessageType.MOVE_ATTEMPT.getValue())) {
        GameMessage<MoveAttemptMessage> gameMessage = new Gson().fromJson(
                payload, new TypeToken<GameMessage<MoveAttemptMessage>>(){}.getType());
        handleMoveAttemptEvent(session, gameMessage);
      }
      if (messageType.equals(MessageType.NEW_PLAYER.getValue())) {
        createPlayer(session);
        sendAvailableGamesToSession(session);
      }
      if (messageType.equals(MessageType.LIST_GAMES.getValue())) {
        sendAvailableGamesToSession(session);
      }
      if (messageType.equals(MessageType.START_NEW.getValue())) {
        GameMessage<StartNewMessage> gameMessage = new Gson().fromJson(
                payload, new TypeToken<GameMessage<StartNewMessage>>(){}.getType());
        startNewGame(session, gameMessage);
      }
      if (messageType.equals(MessageType.JOIN_GAME.getValue())) {
        GameMessage<JoinGameMessage> joinMessage = new Gson().fromJson(
                payload, new TypeToken<GameMessage<JoinGameMessage>>(){}.getType());
        joinGame(session, joinMessage);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      GameMessage<ErrorMessage> errorMessage = new GameMessage<>(
              MessageType.GAME_ERROR.getValue(), new ErrorMessage(ex.getLocalizedMessage()));
      String gameMessageJson = new Gson().toJson(errorMessage);
      session.sendMessage(new TextMessage(gameMessageJson.getBytes(StandardCharsets.UTF_8)));
      logger.info("Error: " + ex.getLocalizedMessage());
    }
  }

  private void createPlayer(WebSocketSession session) throws IOException {
    Player player = Player.createNonGamePlayer();
    session.getAttributes().put("PLAYER_ID", player.getId());
    playerSessions.put(player, session);
    PlayerInfo playerInfo = new PlayerInfo(player.getId(), player.getGameId(), player.getSymbol());
    GameMessage<PlayerInfoMessage> playerInfoMessage =
            new GameMessage<>(MessageType.NEW_PLAYER_CREATED.getValue(), new PlayerInfoMessage(playerInfo));
    String gameMessageJson = new Gson().toJson(playerInfoMessage);
    session.sendMessage(new TextMessage(gameMessageJson.getBytes(StandardCharsets.UTF_8)));
    logger.info("Added player " + player.getId() + ". Overall number of players: " + playerSessions.size());
  }

  private void joinGame(WebSocketSession session, GameMessage<JoinGameMessage> joinMessage) throws Exception {
    Optional<TicTacToe> gameOptional =
            games.stream()
                    .filter(game -> game.getGameInfo().gameId() == joinMessage.data().gameId())
                    .findFirst();
    if (gameOptional.isPresent()) {
      TicTacToe ticTacToe = gameOptional.get();
      Optional<Player> playerOptional = playerSessions.keySet().stream().filter(p -> p.getId() == joinMessage.data().playerId()).findFirst();
      if (playerOptional.isPresent()) { ticTacToe.addPlayer(playerOptional.get()); }

      List<Player> players = playerSessions.keySet().stream().filter(p -> p.getGameId() == joinMessage.data().gameId()).toList();
      GameMessage<GameJoinedMessage> gameJoinedMessage =
              new GameMessage<>(MessageType.JOINED_GAME.getValue(), new GameJoinedMessage(ticTacToe.getGameInfo()));
      String gameMessageJson = new Gson().toJson(gameJoinedMessage);
      for (Player p : players) {
        WebSocketSession ws = playerSessions.get(p);
        ws.sendMessage(new TextMessage(gameMessageJson));
      }
    } else {
      GameMessage<ErrorMessage> errorMessage = new GameMessage<>(MessageType.GAME_ERROR.getValue(), new ErrorMessage("Invalid game id"));
      String gameMessageJson = new Gson().toJson(errorMessage);
      session.sendMessage(new TextMessage(gameMessageJson));
    }
  }

  private void startNewGame(WebSocketSession session, GameMessage<StartNewMessage> gameMessage) throws Exception {
    TicTacToe game = new TicTacToe();
    Optional<Player> playerOptional = playerSessions.keySet()
            .stream().filter(p -> p.getId() == gameMessage.data().playerId()).findFirst();
    if (playerOptional.isPresent()) { game.addPlayer(playerOptional.get()); }
    GameMessage<NewGameStartedMessage> gameStartedMessage =
            new GameMessage<>(MessageType.NEW_GAME_STARTED.getValue(), new NewGameStartedMessage(game.getGameInfo()));
    String gameMessageJson = new Gson().toJson(gameStartedMessage);
    this.games.add(game);
    session.sendMessage(new TextMessage(gameMessageJson));
  }

  private void sendAvailableGamesToSession(WebSocketSession session) throws Exception {
    List<GameInfo> gameInfos = games.stream().map(TicTacToe::getGameInfo).toList();
    AvailableGamesMessage availableGamesMessage = new AvailableGamesMessage(gameInfos);
    GameMessage<AvailableGamesMessage> gameMessage = new GameMessage<>(MessageType.AVAILABLE_GAMES.getValue(), availableGamesMessage);
    String gameMessageJson = new Gson().toJson(gameMessage);
    session.sendMessage(new TextMessage(gameMessageJson.getBytes(StandardCharsets.UTF_8)));
  }

  private void handleMoveAttemptEvent(
          WebSocketSession session, GameMessage<MoveAttemptMessage> gameMessage) throws Exception {
    Optional<TicTacToe> gameOptional =
            games.stream()
                    .filter(game -> game.getGameInfo().gameId() == gameMessage.data().gameId())
                    .findFirst();

    if (gameOptional.isPresent()) {
      TicTacToe ticTacToe = gameOptional.get();
      Player player = playerSessions.keySet()
              .stream().filter(p -> p.getId() == gameMessage.data().playerId()).findFirst().get();
      logger.info("Received Move event from " + player.getId());
      MoveAttemptMessage event = gameMessage.data();
      boolean isWinningMove = ticTacToe.move(event.x(), event.y(), player);

      PlayerMoveMessage playerMoveEvent = new PlayerMoveMessage(event.x(), event.y(), player.getId(), player.getSymbol());
      GameMessage<PlayerMoveMessage> playerMoveMessage = new GameMessage<>(MessageType.PLAYER_MOVE.getValue(), playerMoveEvent);
      List<Player> players = playerSessions.keySet().stream().filter(p -> p.getGameId() == gameMessage.data().gameId()).toList();
      String playerMoveMessageJson = new Gson().toJson(playerMoveMessage);
      String gameMessageJson = new Gson().toJson(playerMoveMessageJson);
      for (Player p : players) {
        WebSocketSession ws = playerSessions.get(p);
        ws.sendMessage(new TextMessage(gameMessageJson));
        if (isWinningMove) {
          logger.info("Winning move by the player " + player.getId());
          ResultMessage resultEvent = new ResultMessage("WIN", player.getId());
          GameMessage<ResultMessage> resultMessage = new GameMessage<>(MessageType.RESULT.getValue(), resultEvent);
          String resultMessageJson = new Gson().toJson(resultMessage);
          ws.sendMessage(new TextMessage(resultMessageJson.getBytes(StandardCharsets.UTF_8)));
        }
      }
    } else {
      GameMessage<ErrorMessage> errorMessage = new GameMessage<>(MessageType.GAME_ERROR.getValue(), new ErrorMessage("Invalid game id"));
      String gameMessageJson = new Gson().toJson(errorMessage);
      session.sendMessage(new TextMessage(gameMessageJson));
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    try {
      GameMessage<AckMessage> gameMessage = new GameMessage<>(MessageType.ONLINE_ACK.getValue(), new AckMessage());
      String gameMessageJson = new Gson().toJson(gameMessage);
      session.sendMessage(new TextMessage(gameMessageJson.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception ex) {
      ex.printStackTrace();
      GameMessage<ErrorMessage> gameMessage = new GameMessage<>(
              MessageType.GAME_ERROR.getValue(), new ErrorMessage(ex.getLocalizedMessage()));
      String gameMessageJson = new Gson().toJson(gameMessage);
      session.sendMessage(new TextMessage(gameMessageJson.getBytes(StandardCharsets.UTF_8)));
      logger.info("Can not add player " + ex.getLocalizedMessage());
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    int playerId = (int) session.getAttributes().get("PLAYER_ID");
    Optional<Player> playerOptional = playerSessions.keySet().stream().filter(p -> p.getId() == playerId).findFirst();
    playerOptional.ifPresent(playerSessions::remove);
    logger.info("Removed player session, playerId :: " + playerId);
  }
}
