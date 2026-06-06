package com.ensa.checkers;

import com.ensa.checkers.controller.AppController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application (Jeu de Dames).
 *
 * La classe hérite de {@link Application} : c'est JavaFX qui appelle {@link #start}
 * automatiquement au démarrage. On délègue ensuite toute la navigation entre les
 * écrans à {@link AppController}, et on affiche le menu principal.
 */
public class Main extends Application {

    /** Appelée par JavaFX au lancement : on crée le routeur et on ouvre le menu. */
    @Override
    public void start(Stage primaryStage) {
        AppController appController = new AppController(primaryStage);
        appController.afficherMenu();
    }

    /** Démarre l'application JavaFX (launch() finit par appeler start()). */
    public static void main(String[] args) {
        launch(args);
    }
}
