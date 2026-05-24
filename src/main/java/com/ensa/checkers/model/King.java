package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public List<Move> getPossibleMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        Position pos = getPosition();
        int row = pos.getRow();
        int col = pos.getCol();

        // La dame peut aller dans les 4 directions diagonales
        for (int dr : new int[]{-1, 1}) {
            for (int dc : new int[]{-1, 1}) {
                int newRow = row + dr;
                int newCol = col + dc;
                if (!Position.isValid(newRow, newCol)) continue;

                Position adjacent = new Position(newRow, newCol);
                Piece occupant = board.getPieceAt(adjacent);

                if (occupant == null) {
                    // Case vide → déplacement simple
                    moves.add(new Move(pos, adjacent));
                } else if (occupant.getColor() != getColor()) {
                    // Pion ennemi → vérifier si on peut sauter par-dessus (capture)
                    int landRow = row + 2 * dr;
                    int landCol = col + 2 * dc;
                    if (Position.isValid(landRow, landCol)) {
                        Position land = new Position(landRow, landCol);
                        if (board.getPieceAt(land) == null) {
                            Move capture = new Move(pos, land);
                            capture.addCaptured(adjacent);
                            moves.add(capture);
                        }
                    }
                }
            }
        }
        return moves;
    }
}