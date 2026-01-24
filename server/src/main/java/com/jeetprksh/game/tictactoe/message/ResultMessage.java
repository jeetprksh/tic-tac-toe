package com.jeetprksh.game.tictactoe.message;

public record ResultMessage(String resultType, int winningPlayer) implements Message {
}
