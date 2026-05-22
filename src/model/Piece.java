package model;

import java.util.List;

public abstract class Piece {
    private final PieceColor color;
    private Position position;

    public Piece(PieceColor color, Position position) {
        this.color = color;
        this.position = position;
    }

    public PieceColor getColor() { return color; }
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public abstract List<Move> getPossibleMoves(Board board);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + color + " @ " + position + "]";
    }
}
