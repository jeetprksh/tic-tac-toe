package com.jeetprksh.game.tictactoe.message;

import java.util.List;

public record AvailableGamesMessage(List<GameInfo> availableGames) implements Message {
}
