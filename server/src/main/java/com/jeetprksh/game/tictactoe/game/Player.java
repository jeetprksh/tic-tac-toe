package com.jeetprksh.game.tictactoe.game;

import java.util.Random;

public class Player {

  private int id;
  private char symbol;
  private int gameId;

  public Player(int id, int gameId, char symbol) {
    this.id = id;
    this.gameId = gameId;
    this.symbol = symbol;
  }

  private Player(int id, char symbol) {
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

  public char getSymbol() {
    return symbol;
  }

  public void setSymbol(char symbol) {
    this.symbol = symbol;
  }

  public static Player createNonGamePlayer() {
    return new Player((new Random()).nextInt(1000), Symbol.X.getSymbol());
  }
}
