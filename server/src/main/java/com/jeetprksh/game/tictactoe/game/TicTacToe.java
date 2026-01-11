package com.jeetprksh.game.tictactoe.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Logger;

public class TicTacToe {

  private final Logger logger = Logger.getLogger(TicTacToe.class.getName());

  private final Cell[][] board = new Cell[3][3];
  private final List<Player> players = new ArrayList<>();
  private final Stack<Symbol> symbols = new Stack<>();
  private final WinDecider winDecider = new WinDecider(board);


  public TicTacToe() {
    logger.info("Initializing new game");
    reset();
  }

  public boolean move(int x, int y, Player player) throws Exception {
    if (board[x][y] == null) {
      Cell cell = new Cell(player);
      board[x][y] = cell;
      return winDecider.isWiningMove(player);
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

  public Player addPlayer() throws Exception {
    if (players.size() < 2) {
      Player player = new Player((new Random()).nextInt(100), symbols.pop().getSymbol());
      this.players.add(player);
      logger.info("Player added ");
      return player;
    } else {
      throw new Exception("Maximum allowed players have entered the game.");
    }
  }

}
