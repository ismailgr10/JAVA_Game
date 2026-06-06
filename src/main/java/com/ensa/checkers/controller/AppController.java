package com.ensa.checkers.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Routeur central de l'application.
 * Toutes les navigations passent par ici — aucun contrôleur ne connaît un autre contrôleur.
 * Chaque contrôleur reçoit une référence à AppController via definirAppController().
 */
public class AppController {

    private final Stage fenetre;

    private static final String CSS      = "/css/style.css";
    private static final String MENU     = "/fxml/MenuView.fxml";
    private static final String CONFIG   = "/fxml/LoginView.fxml";
    private static final String PARTIE   = "/fxml/GameView.fxml";
    private static final String SCORES   = "/fxml/ScoresView.fxml";
    private static final String FIN      = "/fxml/EndGameView.fxml";

    public AppController(Stage fenetre) {
        this.fenetre = fenetre;
        fenetre.setTitle("Jeu de Dames");
        fenetre.setResizable(false);
    }

    /* ------------------------------------------------------------------ */

    public void afficherMenu() {
        chargerVue(MENU, 730, 560);
    }

    public void afficherScores() {
        chargerVue(SCORES, 620, 620);
    }

    /** Navigue vers l'écran de configuration (saisie des noms + choix du mode). */
    public void afficherConfig(String mode) {
        FXMLLoader loader = construireLoader(CONFIG);
        if (loader == null) return;
        try {
            Parent racine = loader.load();
            LoginController controleur = loader.getController();
            controleur.definirAppController(this);
            controleur.definirMode(mode);
            appliquerScene(racine, 620, 520);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Lance la partie une fois les joueurs configurés. */
    public void afficherPartie(String nomJoueur1, String nomJoueur2, String mode) {
        FXMLLoader loader = construireLoader(PARTIE);
        if (loader == null) return;

        try {
            Parent racine = loader.load();
            GameController controleur = loader.getController();
            controleur.definirAppController(this);
            controleur.demarrerPartie(nomJoueur1, nomJoueur2, mode);
            appliquerScene(racine, 820, 640);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Affiche l'écran de fin de partie avec le nom du gagnant. */
    public void afficherFin(String nomGagnant) {
        FXMLLoader loader = construireLoader(FIN);
        if (loader == null) return;

        try {
            Parent racine = loader.load();
            EndGameController controleur = loader.getController();
            controleur.definirAppController(this);
            controleur.definirGagnant(nomGagnant);
            appliquerScene(racine, 730, 560);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ------------------------------------------------------------------ */

    /** Charge un FXML simple (sans données à injecter après le chargement). */
    private void chargerVue(String cheminFxml, double largeur, double hauteur) {
        FXMLLoader loader = construireLoader(cheminFxml);
        if (loader == null) return;

        try {
            Parent racine = loader.load();
            Object controleur = loader.getController();
            injecterAppController(controleur);
            appliquerScene(racine, largeur, hauteur);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FXMLLoader construireLoader(String cheminFxml) {
        URL url = getClass().getResource(cheminFxml);
        if (url == null) {
            System.err.println("FXML introuvable : " + cheminFxml);
            return null;
        }
        return new FXMLLoader(url);
    }

    private void appliquerScene(Parent racine, double largeur, double hauteur) {
        Scene scene = new Scene(racine, largeur, hauteur);
        scene.getStylesheets().add(
            getClass().getResource(CSS).toExternalForm()
        );
        fenetre.setScene(scene);
        fenetre.centerOnScreen();
        fenetre.show();
    }

    /** Injecte this dans le contrôleur si celui-ci expose definirAppController(). */
    private void injecterAppController(Object controleur) {
        if (controleur instanceof MenuController)
            ((MenuController) controleur).definirAppController(this);
        else if (controleur instanceof ScoresController)
            ((ScoresController) controleur).definirAppController(this);
        else if (controleur instanceof LoginController)
            ((LoginController) controleur).definirAppController(this);
    }
}
