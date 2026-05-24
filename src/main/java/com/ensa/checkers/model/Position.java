package com.ensa.checkers.model;

import java.util.Objects;

public class Position {
    private final int row;
    private final int col;

    // Crée une position. Lance une exception si les coordonnées sont hors du plateau.
    public Position(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7)
            throw new IllegalArgumentException("Position hors du plateau: (" + row + ", " + col + ")");
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    //  isValid verifie est ce que la case existe dans le board
    public static boolean isValid(int row, int col) {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }

    // Deux positions sont égales si elles désignent la même case (même row ET même col).
    @Override public boolean equals(Object o) {
        if (!(o instanceof Position)) return false;
        Position other = (Position) o;
        return row == other.row && col == other.col;
    }

    // hashCode cohérent avec equals : obligatoire quand on redéfinit equals.
    @Override public int hashCode() { return Objects.hash(row, col); }

    @Override public String toString() { return "(" + row + ", " + col + ")"; }
}
