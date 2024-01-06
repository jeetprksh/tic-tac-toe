package com.jeetprksh.game.tictactoe;

import com.google.gson.Gson;
import com.jeetprksh.game.tictactoe.game.GameMessage;
import com.jeetprksh.game.tictactoe.game.Player;
import com.jeetprksh.game.tictactoe.game.TicTacToe;
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
    logger.info("Received game event: " + payload);
    GameMessage gameMessage = new Gson().fromJson(payload, GameMessage.class);
    try {
      if (gameMessage.getType().equals(GameEvent.MOVE_ATTEMPT.getValue())) {
        logger.info("Received Move event from " + playerSessions.get(session).getId());
        String[] data = gameMessage.getMessage().split("_");
        int x = Integer.parseInt(data[0]);
        int y = Integer.parseInt(data[1]);
        boolean isWinningMove = game.move(x, y, playerSessions.get(session));

        String moveInfo = gameMessage.getMessage() + "_" + playerSessions.get(session).getId() + "_" + playerSessions.get(session).getSymbol();
        GameMessage playerMoveMessage = new GameMessage(GameEvent.PLAYER_MOVE.getValue(), moveInfo);
        String playerMoveMessageJson = new Gson().toJson(playerMoveMessage);
        for (WebSocketSession s : playerSessions.keySet()) {
          s.sendMessage(new TextMessage(playerMoveMessageJson.getBytes(StandardCharsets.UTF_8)));
        }

        if (isWinningMove) {
          String winInfo = GameEvent.RESULT.getValue() + "_WIN_" + playerSessions.get(session).getId();
          GameMessage resultMessage = new GameMessage(GameEvent.RESULT.getValue(), winInfo);
          String resultMessageJson = new Gson().toJson(resultMessage);
          for (WebSocketSession s : playerSessions.keySet()) {
            s.sendMessage(new TextMessage(resultMessageJson.getBytes(StandardCharsets.UTF_8)));
          }
        }
      }
    } catch (Exception ex) {
      GameMessage errorMessage = new GameMessage(GameEvent.GAME_ERROR.getValue(), ex.getLocalizedMessage());
      String gameMessageJson = new Gson().toJson(errorMessage);
      session.sendMessage(new TextMessage(gameMessageJson.getBytes(StandardCharsets.UTF_8)));
      logger.info("Error: " + ex.getLocalizedMessage());
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    try {
      Player player = game.createPlayer();
      playerSessions.put(session, player);
      GameMessage gameMessage = new GameMessage(GameEvent.ONLINE_ACK.getValue(), player.getId() + "_" + player.getSymbol());
      String gameMessageJson = new Gson().toJson(gameMessage);
      session.sendMessage(new TextMessage(gameMessageJson.getBytes(StandardCharsets.UTF_8)));
      logger.info("Added player " + player.getId() + ". Overall number of players: " + playerSessions.size());
    } catch (Exception ex) {
      GameMessage gameMessage = new GameMessage(GameEvent.GAME_ERROR.getValue(), ex.getLocalizedMessage());
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
