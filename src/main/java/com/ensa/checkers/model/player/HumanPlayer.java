package com.ensa.checkers.model.player;

import com.ensa.checkers.model.PieceColor;

/**
 * Joueur humain : il joue en cliquant sur le plateau.
 * Cette classe n'a pas de logique propre, elle indique juste qu'il s'agit d'un humain.
 */
public class HumanPlayer extends Player {

    public HumanPlayer(String nom, PieceColor couleur) {
        super(nom, couleur);
    }

    @Override
    public boolean estHumain() { return true; }
}
