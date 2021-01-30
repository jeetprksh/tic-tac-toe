package com.jeetprksh.game.tictactoe.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class TicTacToe {

  private final int MAX_PLAYERS = 2;

  private final Cell[][] board = new Cell[3][3];
  private final List<Player> players = new ArrayList<>();
  private final Stack<Symbol> symbols = new Stack<>();

  public TicTacToe() {
    reset();
  }

  public boolean move(int x, int y, Player player) throws Exception {
    if (board[x][y] == null) {
      Cell cell = new Cell(player);
      board[x][y] = cell;
      return isWiningMove();
    } else {
      throw new Exception("Wrong Move");
    }
  }

  public void reset() {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        board[i][j] = null;
      }
    }

    symbols.empty();
    symbols.push(Symbol.CROSS);
    symbols.push(Symbol.ZERO);

    players.clear();
  }

  private boolean isWiningMove() {
    return false;
  }

  public Player createPlayer() throws Exception {
    if (players.size() < MAX_PLAYERS) {
      Random random = new Random();
      Player player = new Player(random.nextInt(100), symbols.pop().getSymbol());
      this.players.add(player);
      return player;
    } else {
      throw new Exception("Maximum allowed players have entered the game.");
    }
  }

  private static class Cell {
    public final Player player;

    public Cell(Player player) {
     this.player = player;
    }

    public Player getPlayer() {
      return player;
    }
  }

}
