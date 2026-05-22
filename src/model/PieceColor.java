package model;

public enum PieceColor {
    WHITE, BLACK;

    public PieceColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
    //color.opposite() pour obtenir la couleur adverse  de la couleur actuelle sans écrire de if/else
}
