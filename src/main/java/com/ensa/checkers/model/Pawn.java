package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(PieceColor color, Position position) {
        super(color, position);
    }

    /** Déplacements simples uniquement (une case en diagonale avant vers une case vide).
     *  Les captures sont produites séparément par {@link #getCaptures}. */
    @Override
    public List<Move> getPossibleMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        Position pos = getPosition();
        int row = pos.getRow();
        int col = pos.getCol();
        int forward = (getColor() == PieceColor.WHITE) ? -1 : 1; // blanc monte, noir descend

        for (int dc : new int[]{-1, 1}) {            // -1 gauche, +1 droite
            int newRow = row + forward;
            int newCol = col + dc;
            if (!Position.isValid(newRow, newCol)) continue;

            Position to = new Position(newRow, newCol);
            if (board.getPieceAt(to) == null)        // case vide → déplacement simple
                moves.add(new Move(pos, to, isPromotionRow(newRow)));
        }
        return moves;
    }

    // si le pion atteint la ligne de promotion de l'adresse il devient une dame
    private boolean isPromotionRow(int row) {
        return (getColor() == PieceColor.WHITE && row == 0)
            || (getColor() == PieceColor.BLACK && row == 7);
    }

    @Override
    public boolean canPromote() { return true; }

    @Override
    public List<int[]> getCaptures(Board board, Position from) {
        List<int[]> steps = new ArrayList<>();
        int row = from.getRow(), col = from.getCol();
        int fwd = (getColor() == PieceColor.WHITE) ? -1 : 1;

        for (int dc : new int[]{-1, 1}) {
            int er = row + fwd,     ec = col + dc;
            int lr = row + 2 * fwd, lc = col + 2 * dc;
            if (!Position.isValid(er, ec) || !Position.isValid(lr, lc)) continue;
            Piece target = board.getPieceAt(new Position(er, ec));
            if (target != null && target.getColor() != getColor()
                    && board.getPieceAt(new Position(lr, lc)) == null)
                steps.add(new int[]{er, ec, lr, lc});
        }
        return steps;
    }
}
