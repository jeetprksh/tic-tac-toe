package com.jeetprksh.game.tictactoe.game;

class Cell {
    public final Player player;

    public Cell(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}