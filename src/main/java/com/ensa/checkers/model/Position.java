package com.ensa.checkers.model;

import java.util.Objects;

/**
 * Représente une case du plateau, repérée par sa ligne et sa colonne (0 à 7).
 *
 * Une Position est immuable : une fois créée, ses coordonnées ne changent jamais.
 * On redéfinit equals() et hashCode() pour pouvoir comparer deux cases et les
 * utiliser comme clés dans des Set / HashMap.
 */
public class Position {

    private final int ligne;     // 0 = haut du plateau, 7 = bas
    private final int colonne;   // 0 = gauche, 7 = droite

    /** Crée une case. Lève une exception si les coordonnées sortent du plateau 8x8. */
    public Position(int ligne, int colonne) {
        if (ligne < 0 || ligne > 7 || colonne < 0 || colonne > 7)
            throw new IllegalArgumentException("Position hors du plateau: (" + ligne + ", " + colonne + ")");
        this.ligne = ligne;
        this.colonne = colonne;
    }

    public int getLigne()   { return ligne; }
    public int getColonne() { return colonne; }

    /** Indique si les coordonnées (ligne, colonne) tombent bien à l'intérieur du plateau. */
    public static boolean estValide(int ligne, int colonne) {
        return ligne >= 0 && ligne <= 7 && colonne >= 0 && colonne <= 7;
    }

    /** Deux positions sont égales si elles désignent la même case (même ligne ET même colonne). */
    @Override public boolean equals(Object o) {
        if (!(o instanceof Position)) return false;
        Position autre = (Position) o;
        return ligne == autre.ligne && colonne == autre.colonne;
    }

    /** hashCode cohérent avec equals : obligatoire dès qu'on redéfinit equals. */
    @Override public int hashCode() { return Objects.hash(ligne, colonne); }

    @Override public String toString() { return "(" + ligne + ", " + colonne + ")"; }
}
