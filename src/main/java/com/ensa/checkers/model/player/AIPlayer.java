package com.ensa.checkers.model.player;

import com.ensa.checkers.model.Game;
import com.ensa.checkers.model.Move;
import com.ensa.checkers.model.PieceColor;
import com.ensa.checkers.model.ai.MinimaxAI;

/**
 * Joueur ordinateur : il choisit ses coups tout seul grâce à l'IA {@link MinimaxAI}.
 */
public class AIPlayer extends Player {

    private final MinimaxAI ia;

    public AIPlayer(String nom, PieceColor couleur) {
        super(nom, couleur);
        this.ia = new MinimaxAI();
    }

    @Override
    public boolean estHumain() { return false; }

    /** Demande à l'IA de calculer et de retourner le meilleur coup dans la position actuelle. */
    public Move choisirCoup(Game partie) {
        return ia.trouverMeilleurCoup(partie);
    }
}
