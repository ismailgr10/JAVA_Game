package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Plateau de jeu : une grille 8x8 qui contient les pièces.
 *
 * Chaque case de la grille contient soit une {@link Piece}, soit null (case vide).
 * En plus de placer/déplacer les pièces, cette classe sait :
 *   - appliquer un coup (et éventuellement l'annuler, pour que l'IA explore vite) ;
 *   - se copier (utile pour simuler des coups sans toucher au vrai plateau).
 */
public class Board {

    private static final int TAILLE = 8;
    private Piece[][] grille;   // grille[ligne][colonne] : la pièce sur la case, ou null

    public Board() {
        grille = new Piece[TAILLE][TAILLE];
    }

    /** Place les pièces dans leur position de départ : Noirs en haut, Blancs en bas. */
    public void initialiser() {
        // Les 3 premières rangées (en haut) reçoivent les pions noirs...
        for (int ligne = 0; ligne < 3; ligne++)
            for (int colonne = 0; colonne < TAILLE; colonne++)
                if ((ligne + colonne) % 2 == 1)                 // uniquement sur les cases sombres
                    grille[ligne][colonne] = new Pawn(PieceColor.BLACK, new Position(ligne, colonne));

        // ... et les 3 dernières rangées (en bas) les pions blancs.
        for (int ligne = 5; ligne < TAILLE; ligne++)
            for (int colonne = 0; colonne < TAILLE; colonne++)
                if ((ligne + colonne) % 2 == 1)
                    grille[ligne][colonne] = new Pawn(PieceColor.WHITE, new Position(ligne, colonne));
    }

    /** Retourne la pièce présente sur une case (ou null si la case est vide). */
    public Piece getPiece(Position position) {
        return grille[position.getLigne()][position.getColonne()];
    }

    /** Applique un coup de façon définitive (sans possibilité d'annulation). */
    public void appliquerCoup(Move coup) {
        appliquerCoup(coup, false);
    }

    /**
     * Applique un coup. Si {@code memoriser} est vrai, retourne un jeton qui permet
     * de revenir exactement en arrière via {@link #annulerCoup(MoveUndo)}.
     *
     * Ce mécanisme « jouer / annuler » est utilisé par l'IA et la génération des coups
     * pour explorer des milliers de positions sans recopier le plateau à chaque fois.
     */
    public MoveUndo appliquerCoup(Move coup, boolean memoriser) {
        Position depart  = coup.getDepart();
        Position arrivee = coup.getArrivee();
        Piece piece      = grille[depart.getLigne()][depart.getColonne()];

        // 1. On retire les pièces capturées (en les mémorisant si on veut pouvoir annuler)
        List<Piece> piecesCapturees = memoriser ? new ArrayList<>() : null;
        for (Position capturee : coup.getPositionsCapturees()) {
            if (memoriser) piecesCapturees.add(grille[capturee.getLigne()][capturee.getColonne()]);
            grille[capturee.getLigne()][capturee.getColonne()] = null;
        }

        // 2. On déplace la pièce ; si le coup promeut, une dame remplace le pion à l'arrivée
        grille[depart.getLigne()][depart.getColonne()] = null;
        piece.setPosition(arrivee);
        grille[arrivee.getLigne()][arrivee.getColonne()] = coup.estPromotion()
            ? new King(piece.getCouleur(), arrivee) : piece;

        return memoriser ? new MoveUndo(coup, piece, piecesCapturees) : null;
    }

    /** Annule un coup appliqué avec {@code memoriser = true} et restaure l'état exact d'avant. */
    public void annulerCoup(MoveUndo u) {
        Position depart  = u.coup.getDepart();
        Position arrivee = u.coup.getArrivee();

        // On remet la pièce d'origine sur sa case de départ (ce qui annule aussi une promotion)
        grille[arrivee.getLigne()][arrivee.getColonne()] = null;
        u.pieceDeplacee.setPosition(depart);
        grille[depart.getLigne()][depart.getColonne()] = u.pieceDeplacee;

        // On replace chaque pièce capturée à sa place
        List<Position> capturees = u.coup.getPositionsCapturees();
        for (int i = 0; i < capturees.size(); i++) {
            Position c = capturees.get(i);
            grille[c.getLigne()][c.getColonne()] = u.piecesCapturees.get(i);
        }
    }

    /**
     * Jeton d'annulation : mémorise ce qu'il faut pour remettre le plateau comme avant
     * (la pièce déplacée d'origine et les pièces capturées, dans l'ordre).
     */
    public static final class MoveUndo {
        final Move coup;
        final Piece pieceDeplacee;
        final List<Piece> piecesCapturees;
        MoveUndo(Move coup, Piece pieceDeplacee, List<Piece> piecesCapturees) {
            this.coup = coup;
            this.pieceDeplacee = pieceDeplacee;
            this.piecesCapturees = piecesCapturees;
        }
    }

    /** Retourne la liste de toutes les pièces encore présentes sur le plateau. */
    public List<Piece> getToutesLesPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (int ligne = 0; ligne < TAILLE; ligne++)
            for (int colonne = 0; colonne < TAILLE; colonne++)
                if (grille[ligne][colonne] != null)
                    pieces.add(grille[ligne][colonne]);
        return pieces;
    }

    /** Crée une copie indépendante du plateau (pour simuler des coups sans modifier l'original). */
    public Board copier() {
        Board copie = new Board();
        for (int ligne = 0; ligne < TAILLE; ligne++)
            for (int colonne = 0; colonne < TAILLE; colonne++) {
                Piece piece = grille[ligne][colonne];
                if (piece != null) {
                    Position pos = new Position(ligne, colonne);
                    copie.grille[ligne][colonne] = (piece instanceof King)
                        ? new King(piece.getCouleur(), pos)
                        : new Pawn(piece.getCouleur(), pos);
                }
            }
        return copie;
    }
}
