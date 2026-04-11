package com.jeetprksh.game.tictactoe.game;

public class Player {

  private final int id;
  private final int gameId;
  private final char symbol;

  public Player(int id, int gameId, char symbol) {
    this.id = id;
    this.gameId = gameId;
    this.symbol = symbol;
  }

  public int getId() {
    return id;
  }

  public int getGameId() {
    return gameId;
  }

  public char getSymbol() {
    return symbol;
  }
}
