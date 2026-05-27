package com.ensa.checkers.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Gère les opérations sur la table `joueurs` dans la base de données.
 */
public class PlayerDAO {

    /** Ajoute un joueur s'il n'existe pas déjà. */
    public static void ajouterJoueur(String nom) {
        if (joueurExiste(nom)) return;

        try {
            Connection conn = DatabaseManager.getConnexion();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO joueurs (nom) VALUES (?)"
            );
            stmt.setString(1, nom);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Erreur ajout joueur : " + e.getMessage());
        }
    }

    /** Retourne true si le joueur existe déjà dans la base. */
    public static boolean joueurExiste(String nom) {
        try {
            Connection conn = DatabaseManager.getConnexion();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id FROM joueurs WHERE nom = ?"
            );
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Erreur vérification joueur : " + e.getMessage());
            return false;
        }
    }
}
