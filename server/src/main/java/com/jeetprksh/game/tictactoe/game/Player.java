package com.jeetprksh.game.tictactoe.game;

import java.util.Random;

public class Player {

  private int id;
  private Symbol symbol;
  private int gameId;

  private Player(int id, Symbol symbol) {
    this.id = id;
    this.symbol = symbol;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public void setSymbol(Symbol symbol) {
    this.symbol = symbol;
  }

  public static Player createNonGamePlayer() {
    return new Player((new Random()).nextInt(1000), Symbol.X);
  }
}
