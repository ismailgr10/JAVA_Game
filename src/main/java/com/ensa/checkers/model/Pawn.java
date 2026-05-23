package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public List<Move> getPossibleMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        Position pos = getPosition();
        int row = pos.getRow();
        int col = pos.getCol();
        int forward = (getColor() == PieceColor.WHITE) ? -1 : 1;

        for (int dc : new int[]{-1, 1}) {
            int newRow = row + forward;
            int newCol = col + dc;
            if (!Position.isValid(newRow, newCol)) continue;

            Position to = new Position(newRow, newCol);
            if (board.getPieceAt(to) == null) {
                moves.add(new Move(pos, to, new ArrayList<>(), isPromotionRow(newRow)));
            } else {
                int landRow = row + 2 * forward;
                int landCol = col + 2 * dc;
                if (!Position.isValid(landRow, landCol)) continue;
                Piece target = board.getPieceAt(to);
                Position land = new Position(landRow, landCol);
                if (target.getColor() != getColor() && board.getPieceAt(land) == null) {
                    List<Position> captured = new ArrayList<>();
                    captured.add(to);
                    moves.add(new Move(pos, land, captured, isPromotionRow(landRow)));
                }
            }
        }
        return moves;
    }

    private boolean isPromotionRow(int row) {
        return (getColor() == PieceColor.WHITE && row == 0)
            || (getColor() == PieceColor.BLACK && row == 7);
    }
}