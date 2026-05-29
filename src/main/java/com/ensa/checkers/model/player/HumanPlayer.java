package com.ensa.checkers.model.player;

import com.ensa.checkers.model.PieceColor;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, PieceColor color) {
        super(name, color);
    }

    @Override
    public boolean isHuman() { return true; }
}
