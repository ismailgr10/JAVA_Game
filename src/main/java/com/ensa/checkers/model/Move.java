package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un coup complet : le déplacement d'une pièce de {@code depart} vers {@code arrivee}.
 *
 * Un coup peut aussi :
 *   - capturer une ou plusieurs pièces adverses (liste {@code positionsCapturees}),
 *     ce qui permet de gérer les prises multiples en un seul coup ;
 *   - aboutir à une promotion (un pion qui atteint le bord adverse devient dame).
 */
public class Move {

    private final Position depart;                                  // case de départ
    private final Position arrivee;                                 // case d'arrivée
    private final List<Position> positionsCapturees = new ArrayList<>();  // pièces mangées
    private final boolean estPromotion;                            // true si le coup promeut le pion

    /** Coup simple : un déplacement sans capture ni promotion. */
    public Move(Position depart, Position arrivee) {
        this(depart, arrivee, false);
    }

    /** Coup pouvant aboutir à une promotion. */
    public Move(Position depart, Position arrivee, boolean estPromotion) {
        this.depart = depart;
        this.arrivee = arrivee;
        this.estPromotion = estPromotion;
    }

    /** Ajoute la case d'une pièce capturée pendant ce coup (utilisé pour les prises multiples). */
    public void ajouterCapturee(Position pos) {
        positionsCapturees.add(pos);
    }

    public Position getDepart()                  { return depart; }
    public Position getArrivee()                 { return arrivee; }
    public List<Position> getPositionsCapturees() { return positionsCapturees; }
    public boolean estCapture()                  { return !positionsCapturees.isEmpty(); }
    public boolean estPromotion()                { return estPromotion; }

    @Override public String toString() {
        return depart + " -> " + arrivee
            + (estCapture() ? " capture " + positionsCapturees : "")
            + (estPromotion ? " [PROMOTION]" : "");
    }
}
