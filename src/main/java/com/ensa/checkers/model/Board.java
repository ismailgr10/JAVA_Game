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
        Position from = move.getFrom();
        Position to   = move.getTo();
        Piece piece   = grid[from.getRow()][from.getCol()];

        for (Position captured : move.getCapturedPositions())
            grid[captured.getRow()][captured.getCol()] = null;

        grid[from.getRow()][from.getCol()] = null;
        piece.setPosition(to);
        grid[to.getRow()][to.getCol()] = move.isPromotion()
            ? new King(piece.getColor(), to) : piece;
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