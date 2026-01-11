package com.jeetprksh.game.tictactoe.event;

public record PlayerMoveEvent(int x, int y, int playerId, char playerSymbol) implements Event {
}
