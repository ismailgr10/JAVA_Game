package com.ensa.checkers.view;

import com.ensa.checkers.model.Position;

// Interface implémentée par le GameController pour réagir aux actions de l'utilisateur sur le plateau
public interface BoardViewListener {
    void onCellClicked(Position pos);
    void onMoveDragged(Position from, Position to);
}
