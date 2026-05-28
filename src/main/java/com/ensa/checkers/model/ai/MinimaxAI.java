package com.ensa.checkers.model.ai;

import com.ensa.checkers.model.Board;
import com.ensa.checkers.model.Game;
import com.ensa.checkers.model.GameRules;
import com.ensa.checkers.model.Move;
import com.ensa.checkers.model.PieceColor;

import java.util.List;

public class MinimaxAI {

    private static final int DEPTH = 4;

    /** Entry point: returns the best move for the current player in the given game. */
    public Move findBestMove(Game game) {
        PieceColor color = game.getCurrentPlayer().getColor();
        List<Move> moves = GameRules.getLegalMoves(game.getBoard(), color);
        if (moves.isEmpty()) return null;

        Move best      = null;
        int  bestScore = Integer.MIN_VALUE;

        for (Move move : moves) {
            Board sim   = game.getBoard().copy();
            sim.applyMove(move);
            int score = minimax(sim, DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, color);
            if (score > bestScore) {
                bestScore = score;
                best      = move;
            }
        }
        return best;
    }

    /**
     * Minimax with alpha-beta pruning.
     * maximizing=true  → it's the AI's turn (maximise score).
     * maximizing=false → it's the opponent's turn (minimise score).
     */
    private int minimax(Board board, int depth, int alpha, int beta,
                        boolean maximizing, PieceColor aiColor) {

        PieceColor current = maximizing ? aiColor : aiColor.opposite();
        List<Move> moves   = GameRules.getLegalMoves(board, current);

        if (depth == 0 || moves.isEmpty())
            return BoardEvaluator.evaluate(board, aiColor);

        if (maximizing) {
            int max = Integer.MIN_VALUE;
            for (Move m : moves) {
                Board child = board.copy();
                child.applyMove(m);
                int val = minimax(child, depth - 1, alpha, beta, false, aiColor);
                if (val > max)   max   = val;
                if (max > alpha) alpha = max;
                if (beta <= alpha) break;   // beta cut-off
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (Move m : moves) {
                Board child = board.copy();
                child.applyMove(m);
                int val = minimax(child, depth - 1, alpha, beta, true, aiColor);
                if (val < min)  min  = val;
                if (min < beta) beta = min;
                if (beta <= alpha) break;   // alpha cut-off
            }
            return min;
        }
    }
}
