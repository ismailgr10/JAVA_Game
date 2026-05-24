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
        int forward = (getColor() == PieceColor.WHITE) ? -1 : 1; // blanc monte, noir descend

        int[] directions = {-1, 1}; // -1 gauche, +1 droite
        for (int dc : directions) {
            int newRow = row + forward;   // une ligne en avant
            int newCol = col + dc;        // une colonne a gauche ou a droite

            if (Position.isValid(newRow, newCol)) { // case dans le plateau --> on la traite
                Position to = new Position(newRow, newCol);

                if (board.getPieceAt(to) == null) {
                    ajouterDeplacement(moves, pos, to, newRow);   // case vide -->déplacement simple
                } else {
                    ajouterCapture(moves, board, pos, row, col, forward, dc);  // case occupée --> tenter une capture
                }
            }
        }
        return moves;
    }

    private void ajouterDeplacement(List<Move> moves, Position pos, Position to, int newRow) {
        moves.add(new Move(pos, to, isPromotionRow(newRow)));
        // creation d'un move de la position actuelle (pos) vers la case vide (to)
        // et la verification si le pion atteinds la ligne de dame
    }

    private void ajouterCapture(List<Move> moves, Board board, Position pos,
                                int row, int col, int forward, int dc) {
        int landRow = row + 2 * forward;  // 2 lignes en avant
        int landCol = col + 2 * dc;       // 2 colonnes sur le cote


        if (Position.isValid(landRow, landCol)) { // case d'atterrissage dans le plateau --> on la traite
            Position over = new Position(row + forward, col + dc); // case à sauter  (1ere case)
            Position land = new Position(landRow, landCol);        // case d'atterrissage  (2eme case)
            Piece target = board.getPieceAt(over);   // la piece presente sur over


            //verification de la piece adversaire autre couleur et land est vide
            if (target.getColor() != getColor() && board.getPieceAt(land) == null) {
                Move capture = new Move(pos, land, isPromotionRow(landRow));
                capture.addCaptured(over);
                moves.add(capture);
            }
        }
    }

    // si le pion atteint la ligne de promotion de l'adresse il devient une dame
    private boolean isPromotionRow(int row) {
        return (getColor() == PieceColor.WHITE && row == 0)
            || (getColor() == PieceColor.BLACK && row == 7);
    }
}
