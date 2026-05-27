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

    @Override
    public List<Move> getPossibleMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int row = getPosition().getRow();
        int col = getPosition().getCol();

        for (int dr : new int[]{-1, 1}) {
            for (int dc : new int[]{-1, 1}) {
                scanDiagonal(board, moves, row, col, dr, dc);
            }
        }
        return moves;
    }

    /** Parcourt une diagonale case par case et ajoute les déplacements / captures possibles. */
    private void scanDiagonal(Board board, List<Move> moves, int row, int col, int dr, int dc) {
        int r = row + dr, c = col + dc;

        while (Position.isValid(r, c)) {
            Piece occupant = board.getPieceAt(new Position(r, c));

            if (occupant == null) {
                // Case vide → déplacement simple
                moves.add(new Move(getPosition(), new Position(r, c)));
            } else {
                if (occupant.getColor() != getColor()) {
                    // Ennemi trouvé → ajouter toutes les cases d'atterrissage après lui
                    addLandingsAfterEnemy(board, moves, r, c, dr, dc);
                }
                return; // Bloqué, on arrête le scan
            }
            r += dr; c += dc;
        }
    }

    /** Ajoute les cases vides disponibles après un ennemi capturé. */
    private void addLandingsAfterEnemy(Board board, List<Move> moves, int er, int ec, int dr, int dc) {
        Position enemyPos = new Position(er, ec);
        int lr = er + dr, lc = ec + dc;

        while (Position.isValid(lr, lc)) {
            Position land = new Position(lr, lc);
            if (board.getPieceAt(land) != null) break;
            Move capture = new Move(getPosition(), land);
            capture.addCaptured(enemyPos);
            moves.add(capture);
            lr += dr; lc += dc;
        }
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
