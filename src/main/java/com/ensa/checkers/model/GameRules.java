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
                chercherCaptures(board, p, p.getPosition(), p.getPosition(),
                                 new ArrayList<>(), new HashSet<>(), captures);

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
        //    (getPossibleMoves ne génère plus que des déplacements non-capturants)
        List<Move> simples = new ArrayList<>();
        for (Piece p : board.getAllPieces())
            if (p.getColor() == color)
                simples.addAll(p.getPossibleMoves(board));
        return simples;
    }

    /** True si `move` figure parmi les coups légaux de `color` (prise obligatoire incluse). */
    public static boolean isValidMove(Board board, Move move, PieceColor color) {
        for (Move legal : getLegalMoves(board, color))
            if (legal.getFrom().equals(move.getFrom()) && legal.getTo().equals(move.getTo()))
                return true;
        return false;
    }

    /** Coups légaux pour une pièce donnée (respecte la prise obligatoire). */
    public static List<Move> getValidMovesFor(Piece piece, Board board) {
        List<Move> result = new ArrayList<>();
        for (Move legal : getLegalMoves(board, piece.getColor()))
            if (legal.getFrom().equals(piece.getPosition()))
                result.add(legal);
        return result;
    }

    /** Captures obligatoires pour `color`, ou liste vide si aucune. */
    public static List<Move> getMandatoryCaptures(Board board, PieceColor color) {
        List<Move> legal = getLegalMoves(board, color);
        if (legal.isEmpty() || !legal.get(0).isCapture()) return new ArrayList<>();
        return legal;
    }

    /** True si color a gagné : l'adversaire n'a plus aucun coup légal. */
    public static boolean checkWinner(Board board, PieceColor color) {
        return getLegalMoves(board, color.opposite()).isEmpty();
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
     * Construit récursivement toutes les chaînes de captures.
     * `origin` = case de départ de la pièce (fixe), `from` = case courante dans la chaîne.
     * À chaque étape : on capture → on avance sur le plateau → on recommence → on revient.
     * Quand plus aucune capture n'est possible → on enregistre la chaîne (de `origin` à `from`).
     */
    public static void chercherCaptures(Board board, Piece piece, Position origin, Position from,
                                        List<Position> capturees, Set<Position> visitees,
                                        List<Move> resultat) {
        List<int[]> prochaines = piece.getCaptures(board, from);

        if (prochaines.isEmpty()) {
            // Fin de chaîne : assembler et enregistrer le coup complet
            if (!capturees.isEmpty()) {
                boolean promotion = piece.canPromote()
                    && ((piece.getColor() == PieceColor.WHITE && from.getRow() == 0)
                    ||  (piece.getColor() == PieceColor.BLACK && from.getRow() == 7));
                Move coup = new Move(origin, from, promotion);
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
                Move coup = new Move(origin, arrivee, true);
                for (Position pos : nouvellesCapturees)
                    coup.addCaptured(pos);
                resultat.add(coup);
                continue;
            }

            // Avancer d'un pas sur le plateau, explorer la suite, puis revenir en arrière
            // (apply/undo en place — évite de copier le plateau à chaque capture)
            Move pas = new Move(from, arrivee);
            pas.addCaptured(ennemi);
            Board.MoveUndo undo = board.applyMove(pas, true);

            Set<Position> nouvellesVisitees = new HashSet<>(visitees);
            nouvellesVisitees.add(arrivee);
            chercherCaptures(board, piece, origin, arrivee, nouvellesCapturees, nouvellesVisitees, resultat);

            board.undoMove(undo);
        }
    }
}
