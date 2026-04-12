package com.jeetprksh.game.tictactoe.message;

import com.jeetprksh.game.tictactoe.game.Symbol;

public record PlayerMoveMessage(int x, int y, int playerId, Symbol playerSymbol) implements Message {
}
