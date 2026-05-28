package com.ensa.checkers.model.player;

import com.ensa.checkers.model.Game;
import com.ensa.checkers.model.Move;
import com.ensa.checkers.model.PieceColor;
import com.ensa.checkers.model.ai.MinimaxAI;

public class AIPlayer extends Player {

    private final MinimaxAI ai;

    public AIPlayer(String name, PieceColor color) {
        super(name, color);
        this.ai = new MinimaxAI();
    }

    @Override
    public boolean isHuman() { return false; }

    public Move chooseMove(Game game) {
        return ai.findBestMove(game);
    }
}
