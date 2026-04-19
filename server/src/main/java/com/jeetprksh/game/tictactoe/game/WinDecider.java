package com.jeetprksh.game.tictactoe.game;

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
      if (isNotMatching(row, i, s)) return false;
    }
    return true;
  }

  private boolean checkColumn(int col, Symbol s) {
    for (int i = 0; i < 3; i++) {
      if (isNotMatching(i, col, s)) return false;
    }
    return true;
  }

  private boolean checkDiagonals(int row, int col, Symbol s) {
    boolean win = false;

    // Main diagonal (0,0), (1,1), (2,2)
    if (row == col) {
      win = (!isNotMatching(0, 0, s) &&
              !isNotMatching(1, 1, s) &&
              !isNotMatching(2, 2, s));
    }

    // Anti-diagonal (0,2), (1,1), (2,0)
    if (!win && row + col == 2) {
      win = (!isNotMatching(0, 2, s) &&
              !isNotMatching(1, 1, s) &&
              !isNotMatching(2, 0, s));
    }
    return win;
  }

  /**
   * Helper method to safely check if a cell matches the symbol.
   * Returns true if the cell is null, has no player, or has a different symbol.
   */
  private boolean isNotMatching(int r, int c, Symbol s) {
    return board[r][c] == null ||
            board[r][c].getPlayer() == null ||
            board[r][c].getPlayer().getSymbol() != s;
  }
}