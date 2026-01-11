package com.jeetprksh.game.tictactoe.game;

import com.jeetprksh.game.tictactoe.event.Event;

public record GameMessage<T extends Event>(String eventType, T data) {

}
