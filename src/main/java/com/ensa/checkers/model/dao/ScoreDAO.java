package com.ensa.checkers.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère les opérations sur la table `scores` dans la base de données.
 */
public class ScoreDAO {

    /** Retourne les meilleurs scores (triés par points décroissants). */
    public List<ScoreEntry> getMeilleursScores() {
        List<ScoreEntry> liste = new ArrayList<>();
        try {
            Connection conn = DatabaseManager.getConnexion();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT nom, parties, victoires, points FROM scores ORDER BY points DESC"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nom        = rs.getString("nom");
                int parties       = rs.getInt("parties");
                int victoires     = rs.getInt("victoires");
                int points        = rs.getInt("points");
                liste.add(new ScoreEntry(nom, parties, victoires, points));
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement scores : " + e.getMessage());
        }
        return liste;
    }

    /** Met à jour le score d'un joueur après une partie (crée la ligne si absente). */
    public void mettreAJourScore(String nom, boolean victoire) {
        try {
            Connection conn = DatabaseManager.getConnexion();

            // Vérifier si le joueur a déjà une ligne dans scores
            PreparedStatement check = conn.prepareStatement(
                "SELECT id FROM scores WHERE nom = ?"
            );
            check.setString(1, nom);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                // Ligne existante → incrémenter
                int gainPoints = victoire ? 30 : 0;
                int gainVictoire = victoire ? 1 : 0;
                PreparedStatement update = conn.prepareStatement(
                    "UPDATE scores SET parties = parties + 1, victoires = victoires + ?, points = points + ? WHERE nom = ?"
                );
                update.setInt(1, gainVictoire);
                update.setInt(2, gainPoints);
                update.setString(3, nom);
                update.executeUpdate();
            } else {
                // Nouvelle ligne
                int points = victoire ? 30 : 0;
                int victoires = victoire ? 1 : 0;
                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO scores (nom, parties, victoires, points) VALUES (?, 1, ?, ?)"
                );
                insert.setString(1, nom);
                insert.setInt(2, victoires);
                insert.setInt(3, points);
                insert.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Erreur mise à jour score : " + e.getMessage());
        }
    }
}
