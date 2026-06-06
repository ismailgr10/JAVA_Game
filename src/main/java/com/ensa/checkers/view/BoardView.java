package com.ensa.checkers.view;

import com.ensa.checkers.model.Board;
import com.ensa.checkers.model.Piece;
import com.ensa.checkers.model.PieceColor;
import com.ensa.checkers.model.Position;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
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
import javafx.util.Duration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Vue du plateau : une grille 8x8 ({@link GridPane}) qui dessine les cases et les pièces.
 *
 * Cette classe est purement « affichage » : elle ne connaît pas les règles. Elle
 * dessine l'état du {@link Board} qu'on lui donne, gère les surbrillances (case
 * sélectionnée, coups possibles, alerte de capture) et l'animation de glissement.
 * Quand l'utilisateur clique ou glisse une pièce, elle prévient son contrôleur via
 * {@link BoardViewListener}.
 */
public class BoardView extends GridPane {

    private static final int TAILLE       = 8;    // nombre de cases par côté
    private static final int TAILLE_CASE  = 72;   // taille d'une case en pixels
    private static final int TAILLE_PIECE = 56;   // taille d'une pièce en pixels

    private final Image imageBlanc      = new Image(getClass().getResourceAsStream("/images/white_pion.png"));
    private final Image imageNoir       = new Image(getClass().getResourceAsStream("/images/dark_pion.png"));
    private final Image imageDameBlanche = new Image(getClass().getResourceAsStream("/images/white-king.png"));
    private final Image imageDameNoire  = new Image(getClass().getResourceAsStream("/images/dark-king.png"));

    private final StackPane[][]     cases    = new StackPane[TAILLE][TAILLE];
    private final BoardViewListener ecouteur;

    private Position            caseSelectionnee;
    private final Set<Position> casesJouables = new HashSet<>();
    private Position            departGlisser;

    // Alerte de capture obligatoire (bordure rouge sur les pièces concernées)
    private Board               dernierPlateau;
    private final Set<Position> alerteCapture = new HashSet<>();

    public BoardView(BoardViewListener ecouteur) {
        this.ecouteur = ecouteur;
        construireGrille();
    }

    // ----------------------------------------------------------------  Construction

    private void construireGrille() {
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                StackPane case_ = creerCase(ligne, colonne);
                cases[ligne][colonne] = case_;
                add(case_, colonne, ligne);
            }
        }
    }

    private StackPane creerCase(int ligne, int colonne) {
        StackPane case_ = new StackPane();
        case_.setPrefSize(TAILLE_CASE, TAILLE_CASE);
        case_.getStyleClass().add(estCaseClaire(ligne, colonne) ? "board-square-light" : "board-square-dark");

        final Position pos = new Position(ligne, colonne);

        case_.setOnMousePressed(e -> departGlisser = pos);

        case_.setOnMouseReleased(e -> {
            if (departGlisser != null && !departGlisser.equals(pos))
                ecouteur.onPieceGlissee(departGlisser, pos);
            departGlisser = null;
        });

        case_.setOnMouseClicked(e -> ecouteur.onCaseCliquee(pos));

        return case_;
    }

    // ----------------------------------------------------------------  Affichage

    /** Redessine tout le plateau selon l'état du Board. */
    public void rafraichir(Board plateau) {
        this.dernierPlateau = plateau;
        for (int ligne = 0; ligne < TAILLE; ligne++) {
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                Position  pos   = new Position(ligne, colonne);
                StackPane case_ = cases[ligne][colonne];
                case_.getChildren().clear();

                case_.getStyleClass().setAll(estCaseClaire(ligne, colonne) ? "board-square-light" : "board-square-dark");

                if (pos.equals(caseSelectionnee))
                    case_.getStyleClass().add(estCaseClaire(ligne, colonne) ? "square-selected" : "square-selected-dark");

                if (casesJouables.contains(pos)) {
                    case_.getStyleClass().add("square-movable");
                    case_.getChildren().add(construirePointCoup());
                }

                Piece piece = plateau.getPiece(pos);
                if (piece != null)
                    case_.getChildren().add(construirePiece(piece));

                if (alerteCapture.contains(pos))
                    case_.getChildren().add(construireAlerte());
            }
        }
    }

    /** Marque la case sélectionnée (appeler avant rafraichir). */
    public void marquerSelection(Position pos) { this.caseSelectionnee = pos; }

    /** Marque les cases accessibles (appeler avant rafraichir). */
    public void marquerCoups(List<Position> positions) {
        casesJouables.clear();
        casesJouables.addAll(positions);
    }

    /** Efface toutes les surbrillances (appeler avant rafraichir). */
    public void effacerMarques() {
        caseSelectionnee = null;
        casesJouables.clear();
    }

    // ----------------------------------------------------------------  Animation

    /**
     * Fait glisser la pièce qui vient d'arriver sur `arrivee` depuis son ancienne case `depart`.
     * À appeler après que le coup a déjà été appliqué et le plateau rafraîchi.
     */
    public void glisserPiece(Position depart, Position arrivee) {
        StackPane piece = noeudPieceSur(arrivee);
        if (piece == null) return;

        cases[arrivee.getLigne()][arrivee.getColonne()].toFront();   // glisse au-dessus des autres

        // On place visuellement la pièce sur son ancienne case, puis on la ramène à 0
        piece.setTranslateX((depart.getColonne() - arrivee.getColonne()) * TAILLE_CASE);
        piece.setTranslateY((depart.getLigne()   - arrivee.getLigne())   * TAILLE_CASE);

        TranslateTransition glissement = new TranslateTransition(Duration.millis(260), piece);
        glissement.setToX(0);
        glissement.setToY(0);
        glissement.setInterpolator(Interpolator.EASE_BOTH);   // départ/arrivée en douceur
        glissement.play();
    }

    /** Retourne le nœud graphique de la pièce présente sur une case (ou null). */
    private StackPane noeudPieceSur(Position pos) {
        StackPane case_ = cases[pos.getLigne()][pos.getColonne()];
        for (Node enfant : case_.getChildren())
            if (enfant instanceof StackPane)   // le pion est un StackPane (ombre + image)
                return (StackPane) enfant;
        return null;
    }

    // ----------------------------------------------------------------  Construction des éléments graphiques

    private StackPane construirePiece(Piece piece) {
        boolean estBlanc = piece.getCouleur() == PieceColor.WHITE;
        boolean estDame  = !piece.peutEtrePromu();

        Circle ombre = new Circle(TAILLE_PIECE / 2.0, Color.rgb(0, 0, 0, 0.35));
        ombre.setTranslateY(3);

        Image image = estDame
            ? (estBlanc ? imageDameBlanche : imageDameNoire)
            : (estBlanc ? imageBlanc       : imageNoir);

        ImageView vue = new ImageView(image);
        vue.setFitWidth(TAILLE_PIECE);
        vue.setFitHeight(TAILLE_PIECE);
        vue.setPreserveRatio(true);

        return new StackPane(ombre, vue);
    }

    private Circle construirePointCoup() {
        return new Circle(10, Color.rgb(80, 200, 80, 0.70));
    }

    private Rectangle construireAlerte() {
        RadialGradient degrade = new RadialGradient(
            0, 0, 0.5, 0.5, 0.55, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.TRANSPARENT),
            new Stop(1.0, Color.rgb(210, 20, 20, 0.70))
        );
        Rectangle r = new Rectangle(TAILLE_CASE, TAILLE_CASE);
        r.setFill(degrade);
        r.setStroke(Color.rgb(220, 20, 20, 0.95));
        r.setStrokeWidth(3.5);
        r.setStrokeType(StrokeType.INSIDE);
        r.setMouseTransparent(true);
        return r;
    }

    // ----------------------------------------------------------------  Alerte de capture obligatoire

    public void signalerCaptures(Set<Position> positions) {
        alerteCapture.clear();
        alerteCapture.addAll(positions);
        if (dernierPlateau != null) rafraichir(dernierPlateau);
    }

    public void arreterAlerteCapture() {
        alerteCapture.clear();
    }

    // ----------------------------------------------------------------

    private boolean estCaseClaire(int ligne, int colonne) {
        return (ligne + colonne) % 2 == 0;
    }
}
