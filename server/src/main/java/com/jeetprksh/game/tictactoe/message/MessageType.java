package com.jeetprksh.game.tictactoe.message;

public enum MessageType {
  AVAILABLE_GAMES("AVAILABLE_GAMES"),
  START_NEW("START_NEW"),
  PLAYER_JOINED("PLAYER_JOINED"),
  RESTART_GAME("RESTART_GAME"),
  MOVE_ATTEMPT("MOVE_ATTEMPT"),
  PLAYER_MOVE("PLAYER_MOVE"),
  ONLINE_ACK("ONLINE_ACK"),
  GAME_ERROR("GAME_ERROR"),
  RESULT("RESULT");

  private final String event;

  MessageType(String event) {
    this.event = event;
  }

  public String getValue() {
    return event;
  }
}
