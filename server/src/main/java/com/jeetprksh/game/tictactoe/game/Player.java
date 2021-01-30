package com.jeetprksh.game.tictactoe.game;

public class Player {

  private final int id;
  private final char symbol;

  public Player(int id, char symbol) {
    this.id = id;
    this.symbol = symbol;
  }

  public int getId() {
    return id;
  }

  public char getSymbol() {
    return symbol;
  }
}
