package com.jeetprksh.game.tictactoe.message;

public record GameRestartedMessage(int gameId) implements Message {
}