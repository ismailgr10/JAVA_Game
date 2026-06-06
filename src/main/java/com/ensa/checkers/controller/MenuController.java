package com.ensa.checkers.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;

/**
 * Contrôleur du menu principal (MenuView.fxml).
 *
 * Chaque bouton du menu appelle une méthode annotée @FXML, qui demande à
 * {@link AppController} de naviguer vers l'écran correspondant.
 */
public class MenuController {

    private AppController appController;

    public void definirAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    private void jouerContreHumain() {
        appController.afficherConfig("HUMAIN_VS_HUMAIN");
    }

    @FXML
    private void jouerContreIA() {
        appController.afficherConfig("HUMAIN_VS_IA");
    }

    @FXML
    private void afficherScores() {
        appController.afficherScores();
    }

    @FXML
    private void quitter() {
        Platform.exit();
    }
}
