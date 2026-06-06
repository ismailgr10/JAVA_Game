package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Dame (« dame volante », règle espagnole).
 *
 * Contrairement au pion, la dame se déplace et capture sur toute la longueur
 * d'une diagonale, dans les quatre directions. Pour capturer, elle peut s'arrêter
 * sur n'importe quelle case vide située derrière la pièce adverse sautée.
 */
public class King extends Piece {

    public King(PieceColor couleur, Position position) {
        super(couleur, position);
    }

    /**
     * Déplacements simples : on parcourt chaque diagonale tant que les cases sont vides.
     * Les captures sont calculées à part par {@link #getCapturesImmediates}.
     */
    @Override
    public List<Move> getCoupsPossibles(Board plateau) {
        List<Move> coups = new ArrayList<>();
        int ligne = getPosition().getLigne();
        int colonne = getPosition().getColonne();

        // dl/dc = direction diagonale : les 4 combinaisons couvrent les 4 diagonales
        for (int dl : new int[]{-1, 1}) {
            for (int dc : new int[]{-1, 1}) {
                int l = ligne + dl, c = colonne + dc;
                while (Position.estValide(l, c) && plateau.getPiece(new Position(l, c)) == null) {
                    coups.add(new Move(getPosition(), new Position(l, c)));
                    l += dl; c += dc;   // on continue d'avancer sur la même diagonale
                }
            }
        }
        return coups;
    }

    @Override
    public boolean peutEtrePromu() { return false; }   // une dame ne peut plus être promue

    /**
     * Captures de la dame : sur chaque diagonale, on cherche le premier ennemi,
     * puis on liste toutes les cases vides situées derrière lui (autant de cases
     * d'atterrissage possibles).
     */
    @Override
    public List<int[]> getCapturesImmediates(Board plateau, Position depart) {
        List<int[]> pas = new ArrayList<>();
        int ligne = depart.getLigne(), colonne = depart.getColonne();

        for (int dl : new int[]{-1, 1}) {
            for (int dc : new int[]{-1, 1}) {
                int[] ennemi = premierEnnemi(plateau, ligne, colonne, dl, dc);
                if (ennemi == null) continue;            // pas d'ennemi capturable sur cette diagonale

                // Toutes les cases vides derrière l'ennemi sont des arrivées possibles
                int la = ennemi[0] + dl, ca = ennemi[1] + dc;
                while (Position.estValide(la, ca) && plateau.getPiece(new Position(la, ca)) == null) {
                    pas.add(new int[]{ennemi[0], ennemi[1], la, ca});
                    la += dl; ca += dc;
                }
            }
        }
        return pas;
    }

    /**
     * Cherche la première pièce rencontrée sur une diagonale.
     * Retourne sa position si c'est un ennemi, ou null si c'est un allié (qui bloque)
     * ou si la diagonale est vide jusqu'au bord.
     */
    private int[] premierEnnemi(Board plateau, int ligne, int colonne, int dl, int dc) {
        int l = ligne + dl, c = colonne + dc;
        while (Position.estValide(l, c)) {
            Piece occupant = plateau.getPiece(new Position(l, c));
            if (occupant != null)
                return (occupant.getCouleur() != getCouleur()) ? new int[]{l, c} : null;
            l += dl; c += dc;
        }
        return null;
    }
}
