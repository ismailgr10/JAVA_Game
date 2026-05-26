package com.ensa.checkers;

import com.ensa.checkers.controller.AppController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        AppController appController = new AppController(primaryStage);
        appController.showMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
