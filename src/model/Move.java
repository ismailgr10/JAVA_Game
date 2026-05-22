package model;

import java.util.ArrayList;
import java.util.List;

public class Move {
    private final Position from;
    private final Position to;
    private final List<Position> capturedPositions;
    private final boolean isPromotion;

    public Move(Position from, Position to) {
        this(from, to, new ArrayList<>(), false);
    }

    public Move(Position from, Position to, List<Position> capturedPositions, boolean isPromotion) {
        this.from = from;
        this.to = to;
        this.capturedPositions = capturedPositions != null ? capturedPositions : new ArrayList<>();
        this.isPromotion = isPromotion;
    }

    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public List<Position> getCapturedPositions() { return capturedPositions; }
    public boolean isCapture() { return !capturedPositions.isEmpty(); }
    public boolean isPromotion() { return isPromotion; }

    @Override
    public String toString() {
        return from + " -> " + to + (isCapture() ? " captures " + capturedPositions : "") + (isPromotion ? " [PROMOTION]" : "");
    }
}
