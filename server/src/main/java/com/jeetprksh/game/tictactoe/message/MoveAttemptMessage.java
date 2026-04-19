package com.jeetprksh.game.tictactoe.message;

public record MoveAttemptMessage(int x, int y, int playerId, int gameId) implements Message {

}