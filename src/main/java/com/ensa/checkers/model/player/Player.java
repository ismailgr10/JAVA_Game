package com.ensa.checkers.model.player;

import com.ensa.checkers.model.PieceColor;

public abstract class Player {
    protected final String name;
    protected final PieceColor color;

    public Player(String name, PieceColor color) {
        this.name  = name;
        this.color = color;
    }

    public String     getName()  { return name; }
    public PieceColor getColor() { return color; }
    public abstract boolean isHuman();
}
