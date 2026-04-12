package com.jeetprksh.game.tictactoe.game;

import com.jeetprksh.game.tictactoe.pojo.GameInfo;
import com.jeetprksh.game.tictactoe.pojo.PlayerInfo;

import java.util.*;
import java.util.logging.Logger;

public class TicTacToe {

  private final Logger logger = Logger.getLogger(TicTacToe.class.getName());

  private final int id = (new Random()).nextInt(100);
  private final Cell[][] board = new Cell[3][3];
  private final List<Player> players = new ArrayList<>();
  //private final Stack<Symbol> symbols = new Stack<>();
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

    players.clear();
  }

  public void addPlayer(Player player) throws Exception {
    if (players.size() < 2) {
      Optional<Player> playerOptional = players.stream().findFirst();
      // TODO simplify this code
      if (playerOptional.isPresent()) {
        Symbol symbol = Symbol.valueOf(String.valueOf(playerOptional.get().getSymbol()));
        if (symbol.equals(Symbol.X)) {
          player.setSymbol('O');
        } else {
          player.setSymbol('X');
        }
      }
      player.setGameId(this.id);
      this.players.add(player);
      logger.info("Player added");
    } else {
      throw new Exception("Maximum allowed players have entered the game.");
    }
  }

  public GameInfo getGameInfo() {
    return new GameInfo(this.id,
            this.players.stream().map(p -> new PlayerInfo(p.getId(), p.getGameId(), p.getSymbol())).toList());
  }

}
