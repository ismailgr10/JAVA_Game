package com.ensa.checkers.model.ai;

import com.ensa.checkers.model.Board;
import com.ensa.checkers.model.King;
import com.ensa.checkers.model.Piece;
import com.ensa.checkers.model.PieceColor;

/**
 * Évalue un plateau de jeu et retourne un score numérique.
 * Score positif  → bon pour `color`.
 * Score négatif  → mauvais pour `color`.
 * Utilisé par MinimaxAI pour choisir le meilleur coup.
 */
public class BoardEvaluator {

    /**
     * Calcule le score du plateau pour la couleur donnée.
     * Formule : (valeur de mes pièces) - (valeur des pièces adverses)
     */
    public static int evaluate(Board board, PieceColor color) {
        int monScore        = 0;
        int scoreAdversaire = 0;

        for (Piece p : board.getAllPieces()) {
            int valeur = (p instanceof King) ? 30 : 10; // dame = 30, pion = 10

            if (p.getColor() == color)
                monScore += valeur;
            else
                scoreAdversaire += valeur;
        }

        return monScore - scoreAdversaire;
    }
}
