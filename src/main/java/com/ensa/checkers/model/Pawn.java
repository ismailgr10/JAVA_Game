package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Pion : la pièce de base.
 *
 * Règles de déplacement retenues dans ce projet :
 *   - il avance d'une case en diagonale vers l'avant (les Blancs montent, les Noirs descendent) ;
 *   - il capture en sautant par-dessus une pièce adverse vers une case vide située juste derrière ;
 *   - arrivé sur la dernière rangée adverse, il est promu en dame.
 */
public class Pawn extends Piece {

    public Pawn(PieceColor couleur, Position position) {
        super(couleur, position);
    }

    /**
     * Déplacements simples uniquement : une case en diagonale avant vers une case vide.
     * Les captures sont calculées à part par {@link #getCapturesImmediates}.
     */
    @Override
    public List<Move> getCoupsPossibles(Board plateau) {
        List<Move> coups = new ArrayList<>();
        Position pos = getPosition();
        int ligne = pos.getLigne();
        int colonne = pos.getColonne();
        int avant = (getCouleur() == PieceColor.WHITE) ? -1 : 1;   // sens d'avance selon la couleur

        for (int dc : new int[]{-1, 1}) {                 // diagonale gauche (-1) puis droite (+1)
            int nouvLigne = ligne + avant;
            int nouvColonne = colonne + dc;
            if (!Position.estValide(nouvLigne, nouvColonne)) continue;

            Position arrivee = new Position(nouvLigne, nouvColonne);
            if (plateau.getPiece(arrivee) == null)        // case libre → déplacement possible
                coups.add(new Move(pos, arrivee, estLigneDePromotion(nouvLigne)));
        }
        return coups;
    }

    /** True si la ligne donnée est la rangée de promotion pour cette couleur. */
    private boolean estLigneDePromotion(int ligne) {
        return (getCouleur() == PieceColor.WHITE && ligne == 0)
            || (getCouleur() == PieceColor.BLACK && ligne == 7);
    }

    @Override
    public boolean peutEtrePromu() { return true; }

    /**
     * Captures immédiates du pion : il saute par-dessus une pièce adverse adjacente
     * en diagonale avant, vers la case vide située juste derrière.
     */
    @Override
    public List<int[]> getCapturesImmediates(Board plateau, Position depart) {
        List<int[]> pas = new ArrayList<>();
        int ligne = depart.getLigne(), colonne = depart.getColonne();
        int avant = (getCouleur() == PieceColor.WHITE) ? -1 : 1;

        for (int dc : new int[]{-1, 1}) {
            int le = ligne + avant,     ce = colonne + dc;        // case de l'ennemi à sauter
            int la = ligne + 2 * avant, ca = colonne + 2 * dc;    // case d'atterrissage (2 cases plus loin)
            if (!Position.estValide(le, ce) || !Position.estValide(la, ca)) continue;

            Piece cible = plateau.getPiece(new Position(le, ce));
            // Capture valide si : pièce adverse à sauter ET case d'atterrissage libre
            if (cible != null && cible.getCouleur() != getCouleur()
                    && plateau.getPiece(new Position(la, ca)) == null)
                pas.add(new int[]{le, ce, la, ca});
        }
        return pas;
    }
}
