package com.jeetprksh.game.tictactoe.game;

import com.jeetprksh.game.tictactoe.message.Message;

public record GameMessage<T extends Message>(String messageType, T data) {

}
