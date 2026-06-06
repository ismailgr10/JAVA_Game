package com.ensa.checkers.model;

import com.ensa.checkers.model.player.Player;

/**
 * Orchestrateur d'une partie : fait le lien entre le plateau, les deux joueurs
 * et les règles du jeu.
 *
 * C'est ici qu'on garde l'état courant (qui doit jouer, partie finie ou non,
 * gagnant éventuel) et qu'on applique un coup via {@link #jouerCoup(Move)} après
 * avoir vérifié sa validité avec {@link GameRules}.
 */
public class Game {

    /** Nombre de demi-coups sans capture ni promotion au-delà duquel la partie est déclarée nulle. */
    private static final int SEUIL_NULLE = 40;

    private final Board    plateau;                       // le plateau de la partie
    private final Player[] joueurs;                       // [0] = Blancs, [1] = Noirs
    private int            indexCourant = 0;              // index du joueur dont c'est le tour
    private GameState      etat         = GameState.EN_COURS;
    private int            coupsSansProgres = 0;          // coups d'affilée sans capture ni promotion

    /** Crée une partie : prépare le plateau et fixe l'ordre des joueurs (Blancs puis Noirs). */
    public Game(Player joueurBlanc, Player joueurNoir) {
        this.plateau = new Board();
        this.plateau.initialiser();
        this.joueurs = new Player[]{joueurBlanc, joueurNoir};
    }

    // ----------------------------------------------------------------  Accès à l'état

    public Player getJoueurCourant() { return joueurs[indexCourant]; }
    public Player getJoueur(int i)   { return joueurs[i]; }
    public Board  getPlateau()       { return plateau; }
    public boolean estTerminee()     { return etat != GameState.EN_COURS; }

    /** Retourne le gagnant, ou null si la partie est nulle ou encore en cours. */
    public Player getGagnant() {
        if (etat == GameState.VICTOIRE_BLANC) return joueurs[0];
        if (etat == GameState.VICTOIRE_NOIR)  return joueurs[1];
        return null;
    }

    // ----------------------------------------------------------------  Jouer un coup

    /**
     * Tente de jouer le coup demandé pour le joueur courant.
     * Retourne true si le coup était légal et a été appliqué, false sinon.
     *
     * Étapes : vérifier la validité → appliquer sur le plateau → mettre à jour
     * le compteur de nulle → déterminer si la partie est gagnée / nulle, sinon
     * passer la main à l'autre joueur.
     */
    public boolean jouerCoup(Move coup) {
        if (etat != GameState.EN_COURS) return false;

        PieceColor couleur = getJoueurCourant().getCouleur();
        if (!GameRules.estCoupValide(plateau, coup, couleur)) return false;

        plateau.appliquerCoup(coup);

        // Un coup « utile » (capture ou promotion) relance le compteur ; sinon il avance vers la nulle
        if (coup.estCapture() || coup.estPromotion()) coupsSansProgres = 0;
        else                                          coupsSansProgres++;

        if (GameRules.aGagne(plateau, couleur)) {
            // L'adversaire n'a plus aucun coup légal → le joueur courant gagne
            etat = (couleur == PieceColor.WHITE) ? GameState.VICTOIRE_BLANC : GameState.VICTOIRE_NOIR;
        } else if (coupsSansProgres >= SEUIL_NULLE) {
            etat = GameState.NUL;
        } else {
            indexCourant = 1 - indexCourant;   // au tour de l'autre joueur (0 ↔ 1)
        }
        return true;
    }
}
