package com.ensa.checkers.model;

/**
 * État d'une partie à un instant donné.
 * Permet à {@link Game} de savoir si le jeu continue ou comment il s'est terminé.
 */
public enum GameState {
    EN_COURS,        // la partie n'est pas finie
    VICTOIRE_BLANC,  // les Blancs ont gagné
    VICTOIRE_NOIR,   // les Noirs ont gagné
    NUL              // match nul (trop de coups sans capture ni promotion)
}
