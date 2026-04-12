package com.jeetprksh.game.tictactoe.game;

import java.util.Objects;

public class WinDecider {
  private final Cell[][] board;

  public WinDecider(Cell[][] board) {
    this.board = board;
  }

  public boolean isWiningMove(int row, int col, Symbol symbol) {
    return (checkRow(row, symbol) ||
            checkColumn(col, symbol) ||
            checkDiagonals(row, col, symbol));
  }

  private boolean checkRow(int row, Symbol s) {
    for (int i = 0; i < 3; i++) {
      if (board[row][i].getPlayer().getSymbol() != s) return false;
    }
    return true;
  }

  private boolean checkColumn(int col, Symbol s) {
    for (int i = 0; i < 3; i++) {
      if (board[i][col].getPlayer().getSymbol() != s) return false;
    }
    return true;
  }

  private boolean checkDiagonals(int row, int col, Symbol s) {
    boolean win = false;
    // Main diagonal (top-left to bottom-right)
    if (row == col) {
      win = (board[0][0].getPlayer().getSymbol() == s &&
              board[1][1].getPlayer().getSymbol() == s &&
              board[2][2].getPlayer().getSymbol() == s);
    }
    // Anti-diagonal (top-right to bottom-left)
    if (!win && row + col == 2) {
      win = (board[0][2].getPlayer().getSymbol() == s &&
              board[1][1].getPlayer().getSymbol() == s &&
              board[2][0].getPlayer().getSymbol() == s);
    }
    return win;
  }
}
