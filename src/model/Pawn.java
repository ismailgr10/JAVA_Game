package model;

import java.util.List;

public class Pawn extends Piece {

    public Pawn(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public List<Move> getPossibleMoves(Board board) {
        return null;
    }
}
