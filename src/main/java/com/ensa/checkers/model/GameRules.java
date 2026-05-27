package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameRules {

    /** Retourne les coups légaux pour une couleur (prise obligatoire + maximale). */
    public static List<Move> getLegalMoves(Board board, PieceColor color) {

        // 1. Chercher toutes les captures possibles
        List<Move> captures = new ArrayList<>();
        for (Piece p : board.getAllPieces())
            if (p.getColor() == color)
                chercherCaptures(board, p, p.getPosition(), new ArrayList<>(), new HashSet<>(), captures);

        // 2. Si captures trouvées → garder uniquement celles qui prennent le maximum
        if (!captures.isEmpty()) {
            int max = 0;
            for (Move m : captures)
                if (m.getCapturedPositions().size() > max)
                    max = m.getCapturedPositions().size();

            List<Move> maximum = new ArrayList<>();
            for (Move m : captures)
                if (m.getCapturedPositions().size() == max)
                    maximum.add(m);
            return maximum;
        }

        // 3. Aucune capture → retourner les déplacements simples
        List<Move> simples = new ArrayList<>();
        for (Piece p : board.getAllPieces())
            if (p.getColor() == color)
                for (Move m : p.getPossibleMoves(board))
                    if (!m.isCapture())
                        simples.add(m);
        return simples;
    }

    /** True si color a gagné : l'adversaire n'a plus aucun coup légal. */
    public static boolean checkWinner(Board board, PieceColor color) {
        return getLegalMoves(board, color.opposite()).isEmpty();
    }

    /** True si le pion atteint la ligne de promotion via ce coup. */
    public static boolean shouldPromote(Piece piece, Move move) {
        return move.isPromotion();
    }

    /** True si au moins une capture est disponible pour cette couleur. */
    public static boolean isCaptureMandatory(Board board, PieceColor color) {
        for (Piece p : board.getAllPieces())
            if (p.getColor() == color)
                if (!p.getCaptures(board, p.getPosition()).isEmpty())
                    return true;
        return false;
    }

    /**
     * Construit récursivement toutes les chaînes de captures depuis `from`.
     * À chaque étape : on capture → on simule le plateau → on recommence.
     * Quand plus aucune capture n'est possible → on enregistre la chaîne.
     */
    public static void chercherCaptures(Board board, Piece piece, Position from,
                                        List<Position> capturees, Set<Position> visitees,
                                        List<Move> resultat) {
        List<int[]> prochaines = piece.getCaptures(board, from);

        if (prochaines.isEmpty()) {
            // Fin de chaîne : assembler et enregistrer le coup complet
            if (!capturees.isEmpty()) {
                boolean promotion = piece.canPromote()
                    && ((piece.getColor() == PieceColor.WHITE && from.getRow() == 0)
                    ||  (piece.getColor() == PieceColor.BLACK && from.getRow() == 7));
                Move coup = new Move(piece.getPosition(), from, promotion);
                for (Position pos : capturees)
                    coup.addCaptured(pos);
                resultat.add(coup);
            }
            return;
        }

        for (int[] step : prochaines) {
            Position ennemi  = new Position(step[0], step[1]); // pièce à capturer
            Position arrivee = new Position(step[2], step[3]); // case d'atterrissage
            if (visitees.contains(arrivee)) continue;

            List<Position> nouvellesCapturees = new ArrayList<>(capturees);
            nouvellesCapturees.add(ennemi);

            // Vérifier promotion sur la case d'arrivée
            boolean promotion = piece.canPromote()
                && ((piece.getColor() == PieceColor.WHITE && arrivee.getRow() == 0)
                ||  (piece.getColor() == PieceColor.BLACK && arrivee.getRow() == 7));

            // Promotion en cours de chaîne → on s'arrête (règle espagnole)
            if (promotion) {
                Move coup = new Move(piece.getPosition(), arrivee, true);
                for (Position pos : nouvellesCapturees)
                    coup.addCaptured(pos);
                resultat.add(coup);
                continue;
            }

            // Simuler le plateau après cette capture pour continuer la chaîne
            Board simule = board.copy();
            Move pas = new Move(from, arrivee);
            pas.addCaptured(ennemi);
            simule.applyMove(pas);

            Set<Position> nouvellesVisitees = new HashSet<>(visitees);
            nouvellesVisitees.add(arrivee);
            chercherCaptures(simule, piece, arrivee, nouvellesCapturees, nouvellesVisitees, resultat);
        }
    }
}
