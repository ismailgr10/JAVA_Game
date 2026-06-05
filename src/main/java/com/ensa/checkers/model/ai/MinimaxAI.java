package com.ensa.checkers.model.ai;

import com.ensa.checkers.model.Board;
import com.ensa.checkers.model.Game;
import com.ensa.checkers.model.GameRules;
import com.ensa.checkers.model.King;
import com.ensa.checkers.model.Move;
import com.ensa.checkers.model.Piece;
import com.ensa.checkers.model.PieceColor;

import java.util.List;

public class MinimaxAI {

    private static final int DEPTH = 4;

    /** Entry point: returns the best move for the current player in the given game. */
    public Move findBestMove(Game game) {
        PieceColor color = game.getCurrentPlayer().getColor();
        List<Move> moves = GameRules.getLegalMoves(game.getBoard(), color);
        if (moves.isEmpty()) return null;

        // Un seul plateau de travail (copie privée à ce thread), exploré en apply/undo.
        Board board = game.getBoard().copy();

        Move best      = null;
        int  bestScore = Integer.MIN_VALUE;

        for (Move move : moves) {
            Board.MoveUndo undo = board.applyMove(move, true);
            int score = minimax(board, DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, color);
            board.undoMove(undo);
            if (score > bestScore) {
                bestScore = score;
                best      = move;
            }
        }
        return best;
    }

    /**
     * Minimax avec élagage alpha-bêta, exploré en place (apply/undo).
     * maximizing=true  → tour de l'IA (maximise le score).
     * maximizing=false → tour de l'adversaire (minimise le score).
     */
    private int minimax(Board board, int depth, int alpha, int beta,
                        boolean maximizing, PieceColor aiColor) {

        PieceColor current = maximizing ? aiColor : aiColor.opposite();
        List<Move> moves   = GameRules.getLegalMoves(board, current);

        if (depth == 0 || moves.isEmpty())
            return evaluate(board, aiColor);

        if (maximizing) {
            int max = Integer.MIN_VALUE;
            for (Move m : moves) {
                Board.MoveUndo undo = board.applyMove(m, true);
                int val = minimax(board, depth - 1, alpha, beta, false, aiColor);
                board.undoMove(undo);
                if (val > max)   max   = val;
                if (max > alpha) alpha = max;
                if (beta <= alpha) break;   // beta cut-off
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (Move m : moves) {
                Board.MoveUndo undo = board.applyMove(m, true);
                int val = minimax(board, depth - 1, alpha, beta, true, aiColor);
                board.undoMove(undo);
                if (val < min)  min  = val;
                if (min < beta) beta = min;
                if (beta <= alpha) break;   // alpha cut-off
            }
            return min;
        }
    }

    /**
     * Évalue un plateau pour `color`. Score positif = favorable à `color`.
     * Combine le matériel (dame=30, pion=10), l'avancement des pions vers la
     * promotion (0..7) et un léger bonus de contrôle du centre.
     */
    private static int evaluate(Board board, PieceColor color) {
        int score = 0;

        for (Piece p : board.getAllPieces()) {
            boolean isKing = p instanceof King;
            int v = isKing ? 30 : 10;

            if (!isKing) {
                int row = p.getPosition().getRow();
                // distance parcourue vers la rangée de promotion (blanc monte, noir descend)
                v += (p.getColor() == PieceColor.WHITE) ? (7 - row) : row;
            }

            int col = p.getPosition().getCol();
            if (col >= 2 && col <= 5) v += 1;   // contrôle des colonnes centrales

            score += (p.getColor() == color) ? v : -v;
        }
        return score;
    }
}
