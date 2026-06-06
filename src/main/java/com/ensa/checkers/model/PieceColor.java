package com.ensa.checkers.model;

/**
 * Couleur d'un joueur / d'une pièce : les Blancs ou les Noirs.
 */
public enum PieceColor {
    WHITE, BLACK;

    /** Retourne la couleur adverse (utile pour passer la main à l'autre joueur). */
    public PieceColor opposee() {
        return this == WHITE ? BLACK : WHITE;
    }
}
