package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Règles du jeu de dames. Classe « boîte à outils » : uniquement des méthodes
 * statiques, aucun état propre. C'est elle qui décide quels coups sont légaux.
 *
 * Règle clé implémentée : la <b>prise obligatoire et maximale</b>. Si au moins une
 * capture est possible, le joueur DOIT capturer, et parmi toutes les prises il doit
 * choisir celle qui mange le plus de pièces.
 */
public class GameRules {

    /**
     * Retourne tous les coups légaux pour une couleur, en appliquant la prise
     * obligatoire et maximale.
     *
     * Logique : on cherche d'abord les captures ; s'il y en a, on ne garde que les
     * plus longues ; sinon (aucune capture) on renvoie les déplacements simples.
     */
    public static List<Move> getCoupsLegaux(Board plateau, PieceColor couleur) {

        // 1. Chercher toutes les chaînes de captures possibles pour cette couleur
        List<Move> captures = new ArrayList<>();
        for (Piece p : plateau.getToutesLesPieces())
            if (p.getCouleur() == couleur)
                chercherCaptures(plateau, p, p.getPosition(), p.getPosition(),
                                 new ArrayList<>(), new HashSet<>(), captures);

        // 2. S'il existe des captures → on impose la prise maximale (le plus grand nombre de pièces)
        if (!captures.isEmpty()) {
            int max = 0;
            for (Move m : captures)
                if (m.getPositionsCapturees().size() > max)
                    max = m.getPositionsCapturees().size();

            List<Move> maximum = new ArrayList<>();
            for (Move m : captures)
                if (m.getPositionsCapturees().size() == max)
                    maximum.add(m);
            return maximum;
        }

        // 3. Aucune capture → on renvoie les déplacements simples de toutes les pièces
        List<Move> simples = new ArrayList<>();
        for (Piece p : plateau.getToutesLesPieces())
            if (p.getCouleur() == couleur)
                simples.addAll(p.getCoupsPossibles(plateau));
        return simples;
    }

    /** True si le coup proposé fait bien partie des coups légaux de la couleur. */
    public static boolean estCoupValide(Board plateau, Move coup, PieceColor couleur) {
        for (Move legal : getCoupsLegaux(plateau, couleur))
            if (legal.getDepart().equals(coup.getDepart()) && legal.getArrivee().equals(coup.getArrivee()))
                return true;
        return false;
    }

    /** Coups légaux concernant une pièce précise (sert à surligner ses cases jouables). */
    public static List<Move> getCoupsValidesPour(Piece piece, Board plateau) {
        List<Move> resultat = new ArrayList<>();
        for (Move legal : getCoupsLegaux(plateau, piece.getCouleur()))
            if (legal.getDepart().equals(piece.getPosition()))
                resultat.add(legal);
        return resultat;
    }

    /** Liste des captures obligatoires d'une couleur (vide s'il n'y en a aucune). */
    public static List<Move> getCapturesObligatoires(Board plateau, PieceColor couleur) {
        List<Move> legaux = getCoupsLegaux(plateau, couleur);
        // getCoupsLegaux renvoie soit des captures, soit des déplacements : on teste le premier
        if (legaux.isEmpty() || !legaux.get(0).estCapture()) return new ArrayList<>();
        return legaux;
    }

    /** True si `couleur` a gagné, c'est-à-dire si l'adversaire n'a plus aucun coup légal. */
    public static boolean aGagne(Board plateau, PieceColor couleur) {
        return getCoupsLegaux(plateau, couleur.opposee()).isEmpty();
    }

    /**
     * Construit récursivement toutes les chaînes de captures.
     * `origine` = case de départ de la pièce (fixe), `depart` = case courante dans la chaîne.
     * À chaque étape : on capture → on avance sur le plateau → on recommence → on revient.
     * Quand plus aucune capture n'est possible → on enregistre la chaîne (de `origine` à `depart`).
     */
    private static void chercherCaptures(Board plateau, Piece piece, Position origine, Position depart,
                                         List<Position> capturees, Set<Position> visitees,
                                         List<Move> resultat) {
        List<int[]> prochaines = piece.getCapturesImmediates(plateau, depart);

        if (prochaines.isEmpty()) {
            // Fin de chaîne : assembler et enregistrer le coup complet
            if (!capturees.isEmpty()) {
                boolean promotion = piece.peutEtrePromu()
                    && ((piece.getCouleur() == PieceColor.WHITE && depart.getLigne() == 0)
                    ||  (piece.getCouleur() == PieceColor.BLACK && depart.getLigne() == 7));
                Move coup = new Move(origine, depart, promotion);
                for (Position pos : capturees)
                    coup.ajouterCapturee(pos);
                resultat.add(coup);
            }
            return;
        }

        for (int[] etape : prochaines) {
            Position ennemi  = new Position(etape[0], etape[1]); // pièce à capturer
            Position arrivee = new Position(etape[2], etape[3]); // case d'atterrissage
            if (visitees.contains(arrivee)) continue;

            List<Position> nouvellesCapturees = new ArrayList<>(capturees);
            nouvellesCapturees.add(ennemi);

            // Vérifier promotion sur la case d'arrivée
            boolean promotion = piece.peutEtrePromu()
                && ((piece.getCouleur() == PieceColor.WHITE && arrivee.getLigne() == 0)
                ||  (piece.getCouleur() == PieceColor.BLACK && arrivee.getLigne() == 7));

            // Promotion en cours de chaîne → on s'arrête (règle espagnole)
            if (promotion) {
                Move coup = new Move(origine, arrivee, true);
                for (Position pos : nouvellesCapturees)
                    coup.ajouterCapturee(pos);
                resultat.add(coup);
                continue;
            }

            // Avancer d'un pas sur le plateau, explorer la suite, puis revenir en arrière
            // (appliquer/annuler en place — évite de copier le plateau à chaque capture)
            Move pas = new Move(depart, arrivee);
            pas.ajouterCapturee(ennemi);
            Board.MoveUndo annulation = plateau.appliquerCoup(pas, true);

            Set<Position> nouvellesVisitees = new HashSet<>(visitees);
            nouvellesVisitees.add(arrivee);
            chercherCaptures(plateau, piece, origine, arrivee, nouvellesCapturees, nouvellesVisitees, resultat);

            plateau.annulerCoup(annulation);
        }
    }
}
