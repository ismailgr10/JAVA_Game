package com.ensa.checkers.controller;

import com.ensa.checkers.model.Board;
import com.ensa.checkers.model.Game;
import com.ensa.checkers.model.GameRules;
import com.ensa.checkers.model.Move;
import com.ensa.checkers.model.MoveValidator;
import com.ensa.checkers.model.Piece;
import com.ensa.checkers.model.PieceColor;
import com.ensa.checkers.model.Position;
import com.ensa.checkers.model.dao.GameDAO;
import com.ensa.checkers.model.dao.PlayerDAO;
import com.ensa.checkers.model.dao.ScoreDAO;
import com.ensa.checkers.model.player.AIPlayer;
import com.ensa.checkers.model.player.HumanPlayer;
import com.ensa.checkers.model.player.Player;
import com.ensa.checkers.view.BoardView;
import com.ensa.checkers.view.BoardViewListener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

public class GameController implements BoardViewListener {

    @FXML private StackPane boardContainer;
    @FXML private Label     currentPlayerLabel;
    @FXML private Label     captureWhiteLabel;
    @FXML private Label     captureBlackLabel;
    @FXML private Label     timerLabel;
    @FXML private Button    abandonButton;

    private AppController appController;
    private Game          game;
    private BoardView     boardView;
    private String        mode;

    // Selection state
    private Position   selectedPos;
    private List<Move> validMovesForSelected;

    // Stats
    private int capturesWhite = 0;
    private int capturesBlack = 0;

    // Timer
    private Timeline timer;
    private int      elapsedSeconds = 0;

    // AI guard
    private boolean aiThinking = false;

    // ----------------------------------------------------------------

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void startGame(String player1Name, String player2Name, String mode) {
        this.mode = mode;

        Player white = new HumanPlayer(player1Name, PieceColor.WHITE);
        Player black = "HUMAIN_VS_IA".equals(mode)
                ? new AIPlayer(player2Name, PieceColor.BLACK)
                : new HumanPlayer(player2Name, PieceColor.BLACK);

        game      = new Game(white, black);
        boardView = new BoardView(this);
        boardContainer.getChildren().add(boardView);

        // Register players in DB (silently fails if DB is down)
        PlayerDAO.ajouterJoueur(player1Name);
        if (!("HUMAIN_VS_IA".equals(mode)))
            PlayerDAO.ajouterJoueur(player2Name);

        startTimer();
        updateUI();
    }

    // ----------------------------------------------------------------  Timer

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedSeconds++;
            timerLabel.setText(String.format("%02d:%02d",
                    elapsedSeconds / 60, elapsedSeconds % 60));
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    // ----------------------------------------------------------------  UI

    private void updateUI() {
        boardView.refresh(game.getBoard());

        Player current = game.getCurrentPlayer();
        String colorTag = current.getColor() == PieceColor.WHITE ? "BLANC" : "NOIR";
        currentPlayerLabel.setText(current.getName() + "\n" + colorTag);

        captureWhiteLabel.setText(String.valueOf(capturesWhite));
        captureBlackLabel.setText(String.valueOf(capturesBlack));
    }

    // ----------------------------------------------------------------  BoardViewListener

    @Override
    public void onCellClicked(Position pos) {
        if (aiThinking || game.isOver()) return;
        if (!game.getCurrentPlayer().isHuman()) return;

        Piece piece = game.getBoard().getPieceAt(pos);

        if (selectedPos == null) {
            // First click: try to select a piece
            if (piece != null && piece.getColor() == game.getCurrentPlayer().getColor()) {
                List<Move> moves = MoveValidator.getValidMovesFor(piece, game.getBoard());
                if (!moves.isEmpty()) {
                    selectedPos           = pos;
                    validMovesForSelected = moves;
                    boardView.highlightSelected(pos);
                    boardView.highlightMoves(
                            moves.stream().map(Move::getTo).collect(Collectors.toList()));
                    boardView.refresh(game.getBoard());
                }
            }
        } else {
            if (pos.equals(selectedPos)) {
                clearSelection();
            } else if (piece != null && piece.getColor() == game.getCurrentPlayer().getColor()) {
                // Switch selection to another friendly piece
                List<Move> moves = MoveValidator.getValidMovesFor(piece, game.getBoard());
                if (!moves.isEmpty()) {
                    selectedPos           = pos;
                    validMovesForSelected = moves;
                    boardView.highlightSelected(pos);
                    boardView.highlightMoves(
                            moves.stream().map(Move::getTo).collect(Collectors.toList()));
                    boardView.refresh(game.getBoard());
                } else {
                    clearSelection();
                }
            } else {
                // Try to move to clicked cell
                Move move = findLegalMove(selectedPos, pos);
                if (move != null) applyHumanMove(move);
                else clearSelection();
            }
        }
    }

    @Override
    public void onMoveDragged(Position from, Position to) {
        if (aiThinking || game.isOver()) return;
        if (!game.getCurrentPlayer().isHuman()) return;

        Piece piece = game.getBoard().getPieceAt(from);
        if (piece == null || piece.getColor() != game.getCurrentPlayer().getColor()) return;

        Move move = findLegalMove(from, to);
        if (move != null) {
            clearSelection();
            applyHumanMove(move);
        }
    }

    // ----------------------------------------------------------------  Move application

    private void applyHumanMove(Move move) {
        PieceColor mover = game.getCurrentPlayer().getColor();
        int captured     = move.getCapturedPositions().size();

        if (!game.tryPlay(move)) { clearSelection(); return; }

        if (mover == PieceColor.WHITE) capturesWhite += captured;
        else                           capturesBlack += captured;

        clearSelection();
        updateUI();

        if (game.isOver()) {
            handleEndGame();
        } else if (!game.getCurrentPlayer().isHuman()) {
            handleAITurn();
        }
    }

    public void handleAITurn() {
        if (!(game.getCurrentPlayer() instanceof AIPlayer ai)) return;

        aiThinking = true;
        abandonButton.setDisable(true);
        boardView.setDisable(true);

        Task<Move> task = new Task<>() {
            @Override protected Move call() { return ai.chooseMove(game); }
        };

        task.setOnSucceeded(e -> {
            Move move = task.getValue();
            if (move != null) {
                PieceColor mover = game.getCurrentPlayer().getColor();
                int captured     = move.getCapturedPositions().size();
                game.tryPlay(move);
                if (mover == PieceColor.WHITE) capturesWhite += captured;
                else                           capturesBlack += captured;
            }
            aiThinking = false;
            abandonButton.setDisable(false);
            boardView.setDisable(false);
            updateUI();
            if (game.isOver()) handleEndGame();
        });

        task.setOnFailed(e -> {
            aiThinking = false;
            abandonButton.setDisable(false);
            boardView.setDisable(false);
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    // ----------------------------------------------------------------  End game

    public void handleEndGame() {
        if (timer != null) timer.stop();

        Player winner     = game.getWinner();
        String winnerName = (winner != null) ? winner.getName() : "NUL";

        String p1 = game.getPlayer(0).getName();
        String p2 = game.getPlayer(1).getName();
        GameDAO.enregistrerPartie(p1, p2, winnerName, mode);

        ScoreDAO scoreDAO = new ScoreDAO();
        scoreDAO.mettreAJourScore(p1, winner != null && winner == game.getPlayer(0));
        if (game.getPlayer(1).isHuman())
            scoreDAO.mettreAJourScore(p2, winner != null && winner == game.getPlayer(1));

        appController.showEndGame(winnerName);
    }

    // ----------------------------------------------------------------  Buttons

    @FXML
    private void onAbandon() {
        if (timer != null) timer.stop();

        // The player who clicks Abandon loses — the other wins
        Player loser  = game.getCurrentPlayer();
        Player winner = (loser == game.getPlayer(0)) ? game.getPlayer(1) : game.getPlayer(0);

        String p1 = game.getPlayer(0).getName();
        String p2 = game.getPlayer(1).getName();
        GameDAO.enregistrerPartie(p1, p2, winner.getName(), mode);

        ScoreDAO scoreDAO = new ScoreDAO();
        scoreDAO.mettreAJourScore(p1, winner == game.getPlayer(0));
        if (game.getPlayer(1).isHuman())
            scoreDAO.mettreAJourScore(p2, winner == game.getPlayer(1));

        appController.showEndGame(winner.getName());
    }

    @FXML
    private void onRetourMenu() {
        if (timer != null) timer.stop();
        appController.showMenu();
    }

    // ----------------------------------------------------------------  Helpers

    private Move findLegalMove(Position from, Position to) {
        for (Move m : GameRules.getLegalMoves(game.getBoard(), game.getCurrentPlayer().getColor()))
            if (m.getFrom().equals(from) && m.getTo().equals(to)) return m;
        return null;
    }

    private void clearSelection() {
        selectedPos           = null;
        validMovesForSelected = null;
        boardView.clearHighlights();
        boardView.refresh(game.getBoard());
    }
}
