package com.ensa.checkers.controller;

public class LoginController {

    private AppController appController;
    private String mode;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
