package com.jeetprksh.game.tictactoe;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.jeetprksh.game.tictactoe.event.*;
import com.jeetprksh.game.tictactoe.game.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

  private final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

  private final TicTacToe game = new TicTacToe();

  private final Map<WebSocketSession, Player> playerSessions = new HashMap<>();

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    String payload = message.getPayload();
    logger.info("Received message: " + payload);
    try {
      String eventType = JsonParser.parseString(payload).getAsJsonObject().get("eventType").getAsString();
      if (eventType.equals(GameEvent.MOVE_ATTEMPT.getValue())) {
        GameMessage<MoveAttemptEvent> gameMessage = new Gson().fromJson(payload, new TypeToken<GameMessage<MoveAttemptEvent>>(){}.getType());
        handleMoveAttemptEvent(session, gameMessage);
      }
    } catch (Exception ex) {
      GameMessage<ErrorEvent> errorMessage = new GameMessage<>(GameEvent.GAME_ERROR.getValue(), new ErrorEvent(ex.getLocalizedMessage()));
      String gameMessageJson = new Gson().toJson(errorMessage);
      session.sendMessage(new TextMessage(gameMessageJson.getBytes(StandardCharsets.UTF_8)));
      logger.info("Error: " + ex.getLocalizedMessage());
    }
  }

  private void handleMoveAttemptEvent(WebSocketSession session, GameMessage<MoveAttemptEvent> gameMessage) throws Exception {
    Player player = playerSessions.get(session);
    logger.info("Received Move event from " + player.getId());
    MoveAttemptEvent event = gameMessage.data();
    boolean isWinningMove = game.move(event.x(), event.y(), player);

    PlayerMoveEvent playerMoveEvent = new PlayerMoveEvent(event.x(), event.y(), player.getId(), player.getSymbol());
    GameMessage<PlayerMoveEvent> playerMoveMessage = new GameMessage<>(GameEvent.PLAYER_MOVE.getValue(), playerMoveEvent);
    String playerMoveMessageJson = new Gson().toJson(playerMoveMessage);
    for (WebSocketSession s : playerSessions.keySet()) {
      s.sendMessage(new TextMessage(playerMoveMessageJson.getBytes(StandardCharsets.UTF_8)));
    }

    if (isWinningMove) {
      logger.info("Winning move by the player " + player.getId());
      ResultEvent resultEvent = new ResultEvent("WIN", player.getId());
      GameMessage<ResultEvent> resultMessage = new GameMessage<>(GameEvent.RESULT.getValue(), resultEvent);
      String resultMessageJson = new Gson().toJson(resultMessage);
      for (WebSocketSession s : playerSessions.keySet()) {
        s.sendMessage(new TextMessage(resultMessageJson.getBytes(StandardCharsets.UTF_8)));
      }
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    try {
      Player player = game.addPlayer();
      playerSessions.put(session, player);
      GameMessage<AckEvent> gameMessage = new GameMessage<>(GameEvent.ONLINE_ACK.getValue(), new AckEvent());
      String gameMessageJson = new Gson().toJson(gameMessage);
      session.sendMessage(new TextMessage(gameMessageJson.getBytes(StandardCharsets.UTF_8)));
      logger.info("Added player " + player.getId() + ". Overall number of players: " + playerSessions.size());
    } catch (Exception ex) {
      GameMessage<ErrorEvent> gameMessage = new GameMessage<>(GameEvent.GAME_ERROR.getValue(), new ErrorEvent(ex.getLocalizedMessage()));
      String gameMessageJson = new Gson().toJson(gameMessage);
      session.sendMessage(new TextMessage(gameMessageJson.getBytes(StandardCharsets.UTF_8)));
      session.close();
      logger.info("Can not add player " + ex.getLocalizedMessage());
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    playerSessions.remove(session);
    logger.info("Removed player session");
  }
}
