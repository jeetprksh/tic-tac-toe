package com.jeetprksh.game.tictactoe.message;

import com.jeetprksh.game.tictactoe.pojo.GameInfo;

import java.util.List;

public record AvailableGamesMessage(List<GameInfo> availableGames) implements Message {
}
