package com.jeetprksh.game.tictactoe.game;

public class GameMessage {

  private final String type;
  private final String message;

  public GameMessage(String type, String message) {
    this.type = type;
    this.message = message;
  }

  public String getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

}
