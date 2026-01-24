package com.jeetprksh.game.tictactoe.message;

public record PlayerMoveMessage(int x, int y, int playerId, char playerSymbol) implements Message {
}
