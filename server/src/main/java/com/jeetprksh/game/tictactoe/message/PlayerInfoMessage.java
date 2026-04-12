package com.jeetprksh.game.tictactoe.message;

import com.jeetprksh.game.tictactoe.pojo.PlayerInfo;

public record PlayerInfoMessage(PlayerInfo playerInfo) implements Message {
}
