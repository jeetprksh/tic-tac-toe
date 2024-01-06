package com.jeetprksh.game.tictactoe.game;

import java.util.Objects;

class WinDecider {

  private final int[][] magicSquare = {{8,1,6},{3,5,7},{4,9,2}};
  private final Cell[][] board;

  WinDecider(Cell[][] board) {
    this.board = board;
  }

  public boolean isWiningMove(Player player) {
    return checkRows(player) || checkColumns(player) || checkDiagonal(player) || checkAntiDiagonal(player);
  }

  private boolean checkRows(Player player) {
    int sum = 0;
    boolean isWiningMove = false;
    for (int i = 0; i <= 2; i++) {
      for (int j = 0; j <= 2; j++) {
        if (Objects.nonNull(board[i][j]) && (board[i][j]).player.getId() == player.getId()) {
          sum = sum + magicSquare[i][j];
        }
        if (sum == 15) {
          isWiningMove = true;
          break;
        }
        sum = 0;
      }
      if (isWiningMove) {
        break;
      }
    }
    return isWiningMove;
  }

  private boolean checkColumns(Player player) {
    int sum = 0;
    boolean isWiningMove = false;
    for (int i = 0; i <= 2; i++) {
      for (int j = 0; j <= 2; j++) {
        if (Objects.nonNull(board[j][i]) && (board[j][i]).player.getId() == player.getId()) {
          sum = sum + magicSquare[j][i];
        }
        if (sum == 15) {
          isWiningMove = true;
          break;
        }
        sum = 0;
      }
      if (isWiningMove) {
        break;
      }
    }
    return isWiningMove;
  }

  private boolean checkDiagonal(Player player) {
    int sum = 0;
    if (Objects.nonNull(board[0][0]) && (board[0][0]).player.getId() == player.getId()) {
      sum = sum + magicSquare[0][0];
    }
    if (Objects.nonNull(board[1][1]) && (board[1][1]).player.getId() == player.getId()) {
      sum = sum + magicSquare[1][1];
    }
    if (Objects.nonNull(board[2][2]) && (board[2][2]).player.getId() == player.getId()) {
      sum = sum + magicSquare[2][2];
    }
    return sum == 15;
  }

  private boolean checkAntiDiagonal(Player player) {
    int sum = 0;
    if (Objects.nonNull(board[0][2]) && (board[0][2]).player.getId() == player.getId()) {
      sum = sum + magicSquare[0][2];
    }
    if (Objects.nonNull(board[1][1]) && (board[1][1]).player.getId() == player.getId()) {
      sum = sum + magicSquare[1][1];
    }
    if (Objects.nonNull(board[2][0]) && (board[2][0]).player.getId() == player.getId()) {
      sum = sum + magicSquare[2][0];
    }
    return sum == 15;
  }
}
