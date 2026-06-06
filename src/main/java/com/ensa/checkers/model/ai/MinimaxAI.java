package com.ensa.checkers.model.ai;

import com.ensa.checkers.model.Board;
import com.ensa.checkers.model.Game;
import com.ensa.checkers.model.GameRules;
import com.ensa.checkers.model.King;
import com.ensa.checkers.model.Move;
import com.ensa.checkers.model.Piece;
import com.ensa.checkers.model.PieceColor;

import java.util.List;

/**
 * Intelligence artificielle de l'adversaire, basée sur l'algorithme <b>Minimax</b>
 * avec <b>élagage alpha-bêta</b>.
 *
 * Idée du Minimax : l'IA explore les coups possibles à l'avance, sur plusieurs tours.
 * À son tour elle cherche à <i>maximiser</i> son score ; au tour de l'adversaire elle
 * suppose qu'il jouera le pire coup pour elle, donc elle <i>minimise</i>. En remontant
 * l'arbre des possibilités, elle choisit le coup menant à la meilleure situation.
 *
 * L'élagage alpha-bêta accélère la recherche : il abandonne les branches dont on sait
 * déjà qu'elles ne seront pas choisies, ce qui évite des calculs inutiles.
 *
 * {@code PROFONDEUR} = profondeur de réflexion (nombre de demi-coups anticipés).
 */
public class MinimaxAI {

    /** Profondeur de recherche : plus elle est grande, plus l'IA est forte mais lente. */
    private static final int PROFONDEUR = 4;

    /** Point d'entrée : retourne le meilleur coup pour le joueur courant de la partie. */
    public Move trouverMeilleurCoup(Game partie) {
        PieceColor couleur = partie.getJoueurCourant().getCouleur();
        List<Move> coups = GameRules.getCoupsLegaux(partie.getPlateau(), couleur);
        if (coups.isEmpty()) return null;

        // Un seul plateau de travail (copie privée à ce thread), exploré en appliquer/annuler.
        Board plateau = partie.getPlateau().copier();

        Move meilleur     = null;
        int  meilleurScore = Integer.MIN_VALUE;

        for (Move coup : coups) {
            Board.MoveUndo annulation = plateau.appliquerCoup(coup, true);
            int score = minimax(plateau, PROFONDEUR - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, couleur);
            plateau.annulerCoup(annulation);
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleur      = coup;
            }
        }
        return meilleur;
    }

    /**
     * Minimax avec élagage alpha-bêta, exploré en place (appliquer/annuler).
     * maximise=true  → tour de l'IA (on maximise le score).
     * maximise=false → tour de l'adversaire (on minimise le score).
     */
    private int minimax(Board plateau, int profondeur, int alpha, int beta,
                        boolean maximise, PieceColor couleurIA) {

        PieceColor courante = maximise ? couleurIA : couleurIA.opposee();
        List<Move> coups    = GameRules.getCoupsLegaux(plateau, courante);

        if (profondeur == 0 || coups.isEmpty())
            return evaluer(plateau, couleurIA);

        if (maximise) {
            int max = Integer.MIN_VALUE;
            for (Move m : coups) {
                Board.MoveUndo annulation = plateau.appliquerCoup(m, true);
                int val = minimax(plateau, profondeur - 1, alpha, beta, false, couleurIA);
                plateau.annulerCoup(annulation);
                if (val > max)   max   = val;
                if (max > alpha) alpha = max;
                if (beta <= alpha) break;   // coupure beta
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (Move m : coups) {
                Board.MoveUndo annulation = plateau.appliquerCoup(m, true);
                int val = minimax(plateau, profondeur - 1, alpha, beta, true, couleurIA);
                plateau.annulerCoup(annulation);
                if (val < min)  min  = val;
                if (min < beta) beta = min;
                if (beta <= alpha) break;   // coupure alpha
            }
            return min;
        }
    }

    /**
     * Évalue un plateau pour `couleur`. Score positif = favorable à `couleur`.
     * Combine le matériel (dame=30, pion=10), l'avancement des pions vers la
     * promotion (0..7) et un léger bonus de contrôle du centre.
     */
    private static int evaluer(Board plateau, PieceColor couleur) {
        int score = 0;

        for (Piece p : plateau.getToutesLesPieces()) {
            boolean estDame = p instanceof King;
            int v = estDame ? 30 : 10;

            if (!estDame) {
                int ligne = p.getPosition().getLigne();
                // distance parcourue vers la rangée de promotion (blanc monte, noir descend)
                v += (p.getCouleur() == PieceColor.WHITE) ? (7 - ligne) : ligne;
            }

            int colonne = p.getPosition().getColonne();
            if (colonne >= 2 && colonne <= 5) v += 1;   // contrôle des colonnes centrales

            score += (p.getCouleur() == couleur) ? v : -v;
        }
        return score;
    }
}
