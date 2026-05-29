package com.ensa.checkers.view;

import com.ensa.checkers.model.Board;
import com.ensa.checkers.model.Piece;
import com.ensa.checkers.model.PieceColor;
import com.ensa.checkers.model.Position;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardView extends GridPane {

    private static final int SIZE       = 8;
    private static final int CELL_SIZE  = 72;
    private static final int PIECE_SIZE = 56;

    private final Image imageWhite     = new Image(getClass().getResourceAsStream("/images/white_pion.png"));
    private final Image imageDark      = new Image(getClass().getResourceAsStream("/images/dark_pion.png"));
    private final Image imageWhiteKing = new Image(getClass().getResourceAsStream("/images/white-king.png"));
    private final Image imageDarkKing  = new Image(getClass().getResourceAsStream("/images/dark-king.png"));

    private final StackPane[][]  cells    = new StackPane[SIZE][SIZE];
    private final BoardViewListener listener;

    private Position            selectedPos;
    private final Set<Position> movable  = new HashSet<>();
    private Position            dragFrom;

    // Capture warning (static red border + gradient)
    private Board               lastBoard;
    private final Set<Position> captureWarning = new HashSet<>();

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

        cell.setOnMousePressed(e -> dragFrom = pos);

        cell.setOnMouseReleased(e -> {
            if (dragFrom != null && !dragFrom.equals(pos))
                listener.onMoveDragged(dragFrom, pos);
            dragFrom = null;
        });

        cell.setOnMouseClicked(e -> listener.onCellClicked(pos));

        return cell;
    }

    // ----------------------------------------------------------------

    /** Redessine tout le plateau selon l'état du Board. */
    public void refresh(Board board) {
        this.lastBoard = board;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Position  pos  = new Position(row, col);
                StackPane cell = cells[row][col];
                cell.getChildren().clear();

                cell.getStyleClass().setAll(isLight(row, col) ? "board-square-light" : "board-square-dark");

                if (pos.equals(selectedPos))
                    cell.getStyleClass().add(isLight(row, col) ? "square-selected" : "square-selected-dark");

                if (movable.contains(pos)) {
                    cell.getStyleClass().add("square-movable");
                    cell.getChildren().add(buildMoveDot());
                }

                Piece piece = board.getPieceAt(pos);
                if (piece != null)
                    cell.getChildren().add(buildPiece(piece));

                if (captureWarning.contains(pos))
                    cell.getChildren().add(buildWarningOverlay());
            }
        }
    }

    /** Marque la case sélectionnée (appeler avant refresh). */
    public void highlightSelected(Position pos) { this.selectedPos = pos; }

    /** Marque les cases accessibles (appeler avant refresh). */
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
        boolean isKing  = !piece.canPromote();

        Circle shadow = new Circle(PIECE_SIZE / 2.0, Color.rgb(0, 0, 0, 0.35));
        shadow.setTranslateY(3);

        Image image = isKing
            ? (isWhite ? imageWhiteKing : imageDarkKing)
            : (isWhite ? imageWhite     : imageDark);

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(PIECE_SIZE);
        imageView.setFitHeight(PIECE_SIZE);
        imageView.setPreserveRatio(true);

        return new StackPane(shadow, imageView);
    }

    private Circle buildMoveDot() {
        return new Circle(10, Color.rgb(80, 200, 80, 0.70));
    }

    private Rectangle buildWarningOverlay() {
        RadialGradient gradient = new RadialGradient(
            0, 0, 0.5, 0.5, 0.55, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.TRANSPARENT),
            new Stop(1.0, Color.rgb(210, 20, 20, 0.70))
        );
        Rectangle r = new Rectangle(CELL_SIZE, CELL_SIZE);
        r.setFill(gradient);
        r.setStroke(Color.rgb(220, 20, 20, 0.95));
        r.setStrokeWidth(3.5);
        r.setStrokeType(StrokeType.INSIDE);
        r.setMouseTransparent(true);
        return r;
    }

    // ----------------------------------------------------------------  Capture warning

    public void flashCapturePieces(Set<Position> positions) {
        captureWarning.clear();
        captureWarning.addAll(positions);
        if (lastBoard != null) refresh(lastBoard);
    }

    public void stopCaptureWarning() {
        captureWarning.clear();
    }

    // ----------------------------------------------------------------

    private boolean isLight(int row, int col) {
        return (row + col) % 2 == 0;
    }
}
