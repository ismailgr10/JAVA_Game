package com.ensa.checkers.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Gère les opérations sur la table `parties` dans la base de données.
 */
public class GameDAO {

    /** Enregistre une partie jouée dans la base de données. */
    public static void enregistrerPartie(String joueur1, String joueur2, String gagnant, String mode) {
        try {
            Connection conn = DatabaseManager.getConnexion();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO parties (joueur1, joueur2, gagnant, mode, date_partie) VALUES (?, ?, ?, ?, NOW())"
            );
            stmt.setString(1, joueur1);
            stmt.setString(2, joueur2);
            stmt.setString(3, gagnant);
            stmt.setString(4, mode);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Erreur enregistrement partie : " + e.getMessage());
        }
    }
}
