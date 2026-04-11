package com.jeetprksh.game.tictactoe.message;

import com.jeetprksh.game.tictactoe.pojo.GameInfo;

public record GameJoinedMessage(GameInfo gameInfo) implements Message { }
