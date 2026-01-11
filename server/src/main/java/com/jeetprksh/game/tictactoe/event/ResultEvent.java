package com.jeetprksh.game.tictactoe.event;

public record ResultEvent(String resultType, int winningPlayer) implements Event {
}
