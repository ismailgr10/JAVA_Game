package com.ensa.checkers.view;

import com.ensa.checkers.model.Board;
import com.ensa.checkers.model.King;
import com.ensa.checkers.model.Piece;
import com.ensa.checkers.model.PieceColor;
import com.ensa.checkers.model.Position;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardView extends GridPane {

    private static final int SIZE       = 8;
    private static final int CELL_SIZE  = 72;
    private static final int PIECE_SIZE = 48;

    private final StackPane[][]     cells    = new StackPane[SIZE][SIZE];
    private final BoardViewListener listener;

    private Position            selectedPos;
    private final Set<Position> movable  = new HashSet<>();
    private Position            dragFrom;

    public BoardView(BoardViewListener listener) {
        this.listener = listener;
        buildGrid();
    }

    // ----------------------------------------------------------------

    private void buildGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                StackPane cell = createCell(row, col);
                cells[row][col] = cell;
                add(cell, col, row);
            }
        }
    }

    private StackPane createCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.getStyleClass().add(isLight(row, col) ? "board-square-light" : "board-square-dark");

        final Position pos = new Position(row, col);

        // Drag : mémoriser la case de départ au clic
        cell.setOnMousePressed(e -> dragFrom = pos);

        // Drag : si relâché sur une case différente → déplacement par drag
        cell.setOnMouseReleased(e -> {
            if (dragFrom != null && !dragFrom.equals(pos))
                listener.onMoveDragged(dragFrom, pos);
            dragFrom = null;
        });

        // Clic simple → sélection ou déplacement
        cell.setOnMouseClicked(e -> listener.onCellClicked(pos));

        return cell;
    }

    // ----------------------------------------------------------------

    /** Redessine tout le plateau selon l'état du Board. */
    public void refresh(Board board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Position  pos  = new Position(row, col);
                StackPane cell = cells[row][col];
                cell.getChildren().clear();

                // Remettre la classe de base
                cell.getStyleClass().setAll(isLight(row, col) ? "board-square-light" : "board-square-dark");

                // Surlignage sélection
                if (pos.equals(selectedPos)) {
                    cell.getStyleClass().add(isLight(row, col) ? "square-selected" : "square-selected-dark");
                }

                // Surlignage cases accessibles
                if (movable.contains(pos)) {
                    cell.getStyleClass().add("square-movable");
                    cell.getChildren().add(buildMoveDot());
                }

                // Pion
                Piece piece = board.getPieceAt(pos);
                if (piece != null)
                    cell.getChildren().add(buildPiece(piece));
            }
        }
    }

    /** Marque la case sélectionnée (appeler avant refresh). */
    public void highlightSelected(Position pos) {
        this.selectedPos = pos;
    }

    /** Marque les cases où le pion sélectionné peut aller (appeler avant refresh). */
    public void highlightMoves(List<Position> positions) {
        movable.clear();
        movable.addAll(positions);
    }

    /** Efface toutes les surbrillances (appeler avant refresh). */
    public void clearHighlights() {
        selectedPos = null;
        movable.clear();
    }

    // ----------------------------------------------------------------

    private StackPane buildPiece(Piece piece) {
        boolean isWhite = piece.getColor() == PieceColor.WHITE;
        boolean isKing  = piece instanceof King;

        // Ombre portée
        Circle shadow = new Circle(PIECE_SIZE / 2.0, Color.rgb(0, 0, 0, 0.35));
        shadow.setTranslateY(3);

        // Corps du pion via CSS (gradient radial défini dans style.css)
        Region body = new Region();
        body.setPrefSize(PIECE_SIZE, PIECE_SIZE);
        body.setMaxSize(PIECE_SIZE, PIECE_SIZE);
        body.getStyleClass().add(isWhite ? "piece-white" : "piece-black");
        if (isKing) body.getStyleClass().add("piece-king");

        StackPane pion = new StackPane(shadow, body);

        // Couronne pour les dames
        if (isKing) {
            Label crown = new Label("♛");
            crown.setStyle("-fx-font-size: 16px; -fx-text-fill: "
                + (isWhite ? "#3E2A20;" : "#F2C400;"));
            pion.getChildren().add(crown);
        }

        return pion;
    }

    /** Petit point vert au centre des cases accessibles. */
    private Circle buildMoveDot() {
        return new Circle(10, Color.rgb(80, 200, 80, 0.70));
    }

    private boolean isLight(int row, int col) {
        return (row + col) % 2 == 0;
    }
}
