package com.ensa.checkers.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Contrôleur de l'écran de fin de partie (EndGameView.fxml).
 *
 * Affiche le résultat (gagnant ou match nul) et propose de rejouer ou de revenir au menu.
 */
public class EndGameController {

    @FXML private Label labelResultat;

    private AppController appController;

    public void definirAppController(AppController appController) {
        this.appController = appController;
    }

    /** Affiche le message de fin selon le résultat reçu du GameController. */
    public void definirGagnant(String nomGagnant) {
        if ("NUL".equals(nomGagnant))
            labelResultat.setText("🤝  Match nul !");
        else
            labelResultat.setText("🏆  " + nomGagnant + " a gagné !");
    }

    @FXML
    private void rejouer() {
        appController.afficherMenu();
    }

    @FXML
    private void retournerAuMenu() {
        appController.afficherMenu();
    }
}
