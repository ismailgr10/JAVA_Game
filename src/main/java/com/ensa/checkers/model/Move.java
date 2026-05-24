package com.ensa.checkers.model;

import java.util.ArrayList;
import java.util.List;

public class Move {
    private final Position from;
    private final Position to;
    private final List<Position> capturedPositions = new ArrayList<>();
    private final boolean isPromotion;

    // Déplacement simple sans capture ni promotion
    public Move(Position from, Position to) {
        this.from = from;
        this.to = to;
        this.isPromotion = false;
    }

    // Déplacement avec promotion possible
    public Move(Position from, Position to, boolean isPromotion) {
        this.from = from;
        this.to = to;
        this.isPromotion = isPromotion;
    }

    // Ajoute la position d'un pion capturé pendant ce coup
    public void addCaptured(Position pos) {
        capturedPositions.add(pos);
    }

    public Position getFrom()                    { return from; }
    public Position getTo()                      { return to; }
    public List<Position> getCapturedPositions() { return capturedPositions; }
    public boolean isCapture()                   { return !capturedPositions.isEmpty(); }
    public boolean isPromotion()                 { return isPromotion; }

    @Override public String toString() {
        return from + " -> " + to
            + (isCapture() ? " captures " + capturedPositions : "")
            + (isPromotion ? " [PROMOTION]" : "");
    }
}
