package com.jeetprksh.game.tictactoe;

public enum GameEvent {
  MOVE_ATTEMPT("MOVE_ATTEMPT"),
  PLAYER_MOVE("PLAYER_MOVE"),
  ONLINE_ACK("ONLINE_ACK"),
  GAME_ERROR("GAME_ERROR"),
  RESULT("RESULT");

  private final String event;

  GameEvent(String event) {
    this.event = event;
  }

  public String getValue() {
    return event;
  }
}
