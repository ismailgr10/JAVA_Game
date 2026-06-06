package com.ensa.checkers.model;

import java.util.List;

/**
 * Classe abstraite représentant une pièce du jeu (un pion ou une dame).
 *
 * Chaque pièce connaît sa couleur et sa position. Les sous-classes
 * {@link Pawn} (pion) et {@link King} (dame) définissent leurs propres règles
 * de déplacement et de capture grâce au polymorphisme : {@link GameRules}
 * manipule des {@code Piece} sans avoir à savoir s'il s'agit d'un pion ou d'une dame.
 */
public abstract class Piece {

    private final PieceColor couleur;   // couleur fixe de la pièce
    private Position position;          // case actuelle (change quand la pièce se déplace)

    public Piece(PieceColor couleur, Position position) {
        this.couleur = couleur;
        this.position = position;
    }

    public PieceColor getCouleur()             { return couleur; }
    public Position getPosition()              { return position; }
    public void setPosition(Position position) { this.position = position; }

    /** Liste des déplacements simples possibles (sans capture) depuis la position actuelle. */
    public abstract List<Move> getCoupsPossibles(Board plateau);

    /** True si la pièce peut être promue (un pion : oui ; une dame : non). */
    public abstract boolean peutEtrePromu();

    /**
     * Captures immédiates possibles depuis la case {@code depart}, sous la forme
     * d'un tableau [ligneEnnemi, colEnnemi, ligneArrivée, colArrivée].
     *
     * Ce format simple (un int[]) permet à {@link GameRules} de construire les
     * chaînes de prises multiples sans connaître le type concret de la pièce.
     */
    public abstract List<int[]> getCapturesImmediates(Board plateau, Position depart);

    @Override public String toString() {
        return getClass().getSimpleName() + "[" + couleur + " @ " + position + "]";
    }
}
