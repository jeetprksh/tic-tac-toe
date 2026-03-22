package com.jeetprksh.game.tictactoe.message;

import java.util.List;

public record GameInfo(int gameId, List<PlayerInfo> players) {
}
