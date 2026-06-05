package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Dame volante (règle espagnole) :
 * se déplace et capture sur toute distance diagonale.
 */
public class King extends Piece {

    public King(PieceColor color, Position position) {
        super(color, position);
    }

    /** Déplacements simples uniquement (cases vides sur les diagonales).
     *  Les captures sont produites séparément par {@link #getCaptures}. */
    @Override
    public List<Move> getPossibleMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int row = getPosition().getRow();
        int col = getPosition().getCol();

        for (int dr : new int[]{-1, 1}) {
            for (int dc : new int[]{-1, 1}) {
                int r = row + dr, c = col + dc;
                while (Position.isValid(r, c) && board.getPieceAt(new Position(r, c)) == null) {
                    moves.add(new Move(getPosition(), new Position(r, c)));
                    r += dr; c += dc;
                }
            }
        }
        return moves;
    }

    @Override
    public boolean canPromote() { return false; }

    @Override
    public List<int[]> getCaptures(Board board, Position from) {
        List<int[]> steps = new ArrayList<>();
        int row = from.getRow(), col = from.getCol();

        for (int dr : new int[]{-1, 1}) {
            for (int dc : new int[]{-1, 1}) {
                int[] enemy = firstEnemy(board, row, col, dr, dc);
                if (enemy == null) continue;
                int lr = enemy[0] + dr, lc = enemy[1] + dc;
                while (Position.isValid(lr, lc) && board.getPieceAt(new Position(lr, lc)) == null) {
                    steps.add(new int[]{enemy[0], enemy[1], lr, lc});
                    lr += dr; lc += dc;
                }
            }
        }
        return steps;
    }

    /** Premier ennemi sur une diagonale, ou null si aucun (ou allié bloquant). */
    private int[] firstEnemy(Board board, int row, int col, int dr, int dc) {
        int r = row + dr, c = col + dc;
        while (Position.isValid(r, c)) {
            Piece occ = board.getPieceAt(new Position(r, c));
            if (occ != null)
                return (occ.getColor() != getColor()) ? new int[]{r, c} : null;
            r += dr; c += dc;
        }
        return null;
    }
}
