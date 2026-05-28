package com.ensa.checkers.model;

import com.ensa.checkers.model.player.Player;

public class Game {

    private final Board    board;
    private final Player[] players;          // [0]=WHITE, [1]=BLACK
    private int            currentIndex = 0;
    private GameState      state        = GameState.EN_COURS;

    public Game(Player whitePlayer, Player blackPlayer) {
        this.board   = new Board();
        this.board.initialize();
        this.players = new Player[]{whitePlayer, blackPlayer};
    }

    // ----------------------------------------------------------------

    public Player getCurrentPlayer() { return players[currentIndex]; }
    public Player getPlayer(int i)   { return players[i]; }
    public Board  getBoard()         { return board; }
    public GameState getState()      { return state; }
    public boolean isOver()          { return state != GameState.EN_COURS; }

    /** Returns the winner, or null if the game is a draw / still in progress. */
    public Player getWinner() {
        return switch (state) {
            case VICTOIRE_BLANC -> players[0];
            case VICTOIRE_NOIR  -> players[1];
            default             -> null;
        };
    }

    // ----------------------------------------------------------------

    /**
     * Attempts to apply a move for the current player.
     * Returns true if the move was legal and applied, false otherwise.
     */
    public boolean tryPlay(Move move) {
        if (state != GameState.EN_COURS) return false;

        PieceColor color = getCurrentPlayer().getColor();
        if (!MoveValidator.isValidMove(board, move, color)) return false;

        board.applyMove(move);

        if (GameRules.checkWinner(board, color)) {
            state = (color == PieceColor.WHITE) ? GameState.VICTOIRE_BLANC : GameState.VICTOIRE_NOIR;
        } else {
            currentIndex = 1 - currentIndex;
        }
        return true;
    }
}
