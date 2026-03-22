package com.jeetprksh.game.tictactoe.message;

public record NewGameStartedMessage(GameInfo gameInfo) implements Message {
}
