package com.ensa.checkers.model.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Fournit une connexion unique à la base de données MySQL (singleton).
 * La connexion est créée une seule fois et réutilisée partout dans l'application.
 */
public class DatabaseManager {

    private static Connection connexion = null;

    /** Retourne la connexion à la base de données (la crée si elle n'existe pas encore). */
    public static Connection getConnexion() {
        if (connexion != null)
            return connexion; // connexion déjà ouverte → on la réutilise

        try {
            // Lire les identifiants depuis config.properties
            Properties config = new Properties();
            InputStream fichier = DatabaseManager.class.getResourceAsStream("/config.properties");
            config.load(fichier);

            String url      = config.getProperty("db.url");
            String user     = config.getProperty("db.user");
            String password = config.getProperty("db.password");

            connexion = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion à la base de données réussie.");

        } catch (Exception e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }

        return connexion;
    }
}
