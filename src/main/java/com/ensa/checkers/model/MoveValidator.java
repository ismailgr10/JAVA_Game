package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

public class MoveValidator {

    /**
     * Returns true if the given move is among the legal moves for that color.
     * Mandatory-capture rule is enforced by GameRules.getLegalMoves.
     */
    public static boolean isValidMove(Board board, Move move, PieceColor color) {
        for (Move legal : GameRules.getLegalMoves(board, color))
            if (legal.getFrom().equals(move.getFrom()) && legal.getTo().equals(move.getTo()))
                return true;
        return false;
    }

    /** Returns all legal moves for a specific piece (respects mandatory-capture rule). */
    public static List<Move> getValidMovesFor(Piece piece, Board board) {
        List<Move> result = new ArrayList<>();
        for (Move legal : GameRules.getLegalMoves(board, piece.getColor()))
            if (legal.getFrom().equals(piece.getPosition()))
                result.add(legal);
        return result;
    }

    /** Returns only capture moves when a capture is mandatory, otherwise empty list. */
    public static List<Move> getMandatoryCaptures(Board board, PieceColor color) {
        List<Move> legal = GameRules.getLegalMoves(board, color);
        if (legal.isEmpty() || !legal.get(0).isCapture()) return new ArrayList<>();
        return legal;
    }
}
