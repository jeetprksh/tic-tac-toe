package com.jeetprksh.game.tictactoe.message;

public enum MessageType {
  LIST_GAMES("LIST_GAMES"),
  AVAILABLE_GAMES("AVAILABLE_GAMES"),
  START_NEW("START_NEW"),
  NEW_GAME_STARTED("NEW_GAME_STARTED"),
  JOIN_GAME("JOIN_GAME"),
  JOINED_GAME("JOINED_GAME"),
  RESTART_GAME("RESTART_GAME"),
  GAME_RESTARTED("GAME_RESTARTED"),
  NEW_PLAYER("NEW_PLAYER"),
  NEW_PLAYER_CREATED("NEW_PLAYER_CREATED"),
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
