package com.ensa.checkers.controller;

public class EndGameController {

    private AppController appController;
    private String winnerName;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setWinner(String winnerName) {
        this.winnerName = winnerName;
    }
}
