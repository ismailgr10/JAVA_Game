package com.ensa.checkers.model;

import java.util.List;

public abstract class Piece {
    private final PieceColor color;
    private Position position;

    public Piece(PieceColor color, Position position) {
        this.color = color;
        this.position = position;
    }

    public PieceColor getColor()              { return color; }
    public Position getPosition()             { return position; }
    public void setPosition(Position position){ this.position = position; }

    public abstract List<Move> getPossibleMoves(Board board);

    /** True si cette pièce peut être promue (Pion → oui, Dame → non). */
    public abstract boolean canPromote();

    /**
     * Captures immédiates depuis `from` sous forme [er, ec, lr, lc].
     * Permet à GameRules de construire des chaînes sans connaître le type de pièce.
     */
    public abstract List<int[]> getCaptures(Board board, Position from);

    @Override public String toString() {
        return getClass().getSimpleName() + "[" + color + " @ " + position + "]";
    }
}