package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private static final int SIZE = 8;
    private Piece[][] grid;

    public Board() {
        grid = new Piece[SIZE][SIZE];
    }

    public void initialize() {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < SIZE; col++)
                if ((row + col) % 2 == 1)
                    grid[row][col] = new Pawn(PieceColor.BLACK, new Position(row, col));

        for (int row = 5; row < SIZE; row++)
            for (int col = 0; col < SIZE; col++)
                if ((row + col) % 2 == 1)
                    grid[row][col] = new Pawn(PieceColor.WHITE, new Position(row, col));
    }

    public Piece getPieceAt(Position position) {
        return grid[position.getRow()][position.getCol()];
    }

    public void applyMove(Move move) {
        applyMove(move, false);
    }

    /**
     * Applique un coup et, si {@code recordUndo} est vrai, retourne un jeton
     * permettant de l'annuler via {@link #undoMove(MoveUndo)}.
     * Utilisé par l'IA / la génération de coups pour explorer sans copier le plateau.
     */
    public MoveUndo applyMove(Move move, boolean recordUndo) {
        Position from = move.getFrom();
        Position to   = move.getTo();
        Piece piece   = grid[from.getRow()][from.getCol()];

        List<Piece> capturedPieces = recordUndo ? new ArrayList<>() : null;
        for (Position captured : move.getCapturedPositions()) {
            if (recordUndo) capturedPieces.add(grid[captured.getRow()][captured.getCol()]);
            grid[captured.getRow()][captured.getCol()] = null;
        }

        grid[from.getRow()][from.getCol()] = null;
        piece.setPosition(to);
        grid[to.getRow()][to.getCol()] = move.isPromotion()
            ? new King(piece.getColor(), to) : piece;

        return recordUndo ? new MoveUndo(move, piece, capturedPieces) : null;
    }

    /** Annule un coup appliqué avec {@code recordUndo = true}, restaurant l'état exact. */
    public void undoMove(MoveUndo u) {
        Position from = u.move.getFrom();
        Position to   = u.move.getTo();

        grid[to.getRow()][to.getCol()] = null;
        u.movedPiece.setPosition(from);            // remet le pion d'origine (annule la promotion)
        grid[from.getRow()][from.getCol()] = u.movedPiece;

        List<Position> caps = u.move.getCapturedPositions();
        for (int i = 0; i < caps.size(); i++) {
            Position c = caps.get(i);
            grid[c.getRow()][c.getCol()] = u.capturedPieces.get(i);
        }
    }

    /** Jeton d'annulation : pièce déplacée d'origine + pièces capturées (dans l'ordre). */
    public static final class MoveUndo {
        final Move move;
        final Piece movedPiece;
        final List<Piece> capturedPieces;
        MoveUndo(Move move, Piece movedPiece, List<Piece> capturedPieces) {
            this.move = move;
            this.movedPiece = movedPiece;
            this.capturedPieces = capturedPieces;
        }
    }

    public void removePiece(Position position) {
        grid[position.getRow()][position.getCol()] = null;
    }

    public void setPiece(Position position, Piece piece) {
        grid[position.getRow()][position.getCol()] = piece;
    }

    public List<Piece> getAllPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (int row = 0; row < SIZE; row++)
            for (int col = 0; col < SIZE; col++)
                if (grid[row][col] != null)
                    pieces.add(grid[row][col]);
        return pieces;
    }

    public Board copy() {
        Board copy = new Board();
        for (int row = 0; row < SIZE; row++)
            for (int col = 0; col < SIZE; col++) {
                Piece piece = grid[row][col];
                if (piece != null) {
                    Position pos = new Position(row, col);
                    copy.grid[row][col] = (piece instanceof King)
                        ? new King(piece.getColor(), pos)
                        : new Pawn(piece.getColor(), pos);
                }
            }
        return copy;
    }
}