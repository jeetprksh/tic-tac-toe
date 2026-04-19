package com.jeetprksh.game.tictactoe.message;

public record JoinGameMessage(int gameId, int playerId) implements Message {
}
