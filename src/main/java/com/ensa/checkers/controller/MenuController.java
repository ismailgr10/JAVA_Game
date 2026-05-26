package com.ensa.checkers.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MenuController {

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    private void onJouerHumain() {
        appController.showLogin("HUMAIN_VS_HUMAIN");
    }

    @FXML
    private void onJouerIA() {
        appController.showLogin("HUMAIN_VS_IA");
    }

    @FXML
    private void onScores() {
        appController.showScores();
    }

    @FXML
    private void onQuitter() {
        Platform.exit();
    }
}
