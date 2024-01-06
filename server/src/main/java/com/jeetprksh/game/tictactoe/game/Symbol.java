package com.jeetprksh.game.tictactoe.game;

public enum Symbol {
  ZERO('0'),
  CROSS('X');

  private final char symbol;

  Symbol(char symbol) {
    this.symbol = symbol;
  }

  public char getSymbol() {
    return symbol;
  }
}
