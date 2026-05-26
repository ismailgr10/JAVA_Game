package com.ensa.checkers.model.dao;

import java.util.List;

public class ScoreDAO {

    public List<ScoreEntry> getTopScores() {
        // TODO: remplacer par vraie requête SQL via DatabaseManager
        return List.of(
            new ScoreEntry("Alice",   10, 8, 240),
            new ScoreEntry("Bob",      7, 5, 150),
            new ScoreEntry("Charlie",  5, 2,  60)
        );
    }
}
