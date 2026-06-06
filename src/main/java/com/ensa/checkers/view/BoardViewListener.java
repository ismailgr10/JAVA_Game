package com.ensa.checkers.view;

import com.ensa.checkers.model.Position;

/**
 * Contrat entre la vue du plateau ({@link BoardView}) et son contrôleur.
 *
 * La vue ne connaît pas les règles du jeu : quand l'utilisateur agit (clic ou
 * glisser-déposer d'une pièce), elle se contente de prévenir le contrôleur via
 * cette interface. C'est le contrôleur (qui implémente l'interface) qui décide
 * quoi faire. Cela respecte la séparation Vue / Contrôleur du MVC.
 */
public interface BoardViewListener {

    /** Appelée quand l'utilisateur clique sur une case. */
    void onCaseCliquee(Position pos);

    /** Appelée quand l'utilisateur fait glisser une pièce d'une case vers une autre. */
    void onPieceGlissee(Position depart, Position arrivee);
}
