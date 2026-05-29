package com.ensa.checkers.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EndGameController {

    @FXML private Label resultLabel;

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setWinner(String winnerName) {
        if ("NUL".equals(winnerName))
            resultLabel.setText("Match nul !");
        else
            resultLabel.setText("🏆  " + winnerName + " a gagné !");
    }

    @FXML
    private void onPlayAgain() {
        appController.showMenu();
    }

    @FXML
    private void onBackToMenu() {
        appController.showMenu();
    }
}
