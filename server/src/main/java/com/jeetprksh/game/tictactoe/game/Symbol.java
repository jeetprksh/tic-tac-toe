package com.jeetprksh.game.tictactoe.game;

public enum Symbol {
  O('O'),
  X('X');

  private final char symbol;

  Symbol(char symbol) {
    this.symbol = symbol;
  }

  public char getSymbol() {
    return symbol;
  }

  public Symbol next() {
    return this == X ? O : X;
  }
}
