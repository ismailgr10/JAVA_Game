package com.ensa.checkers.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Routeur central de l'application.
 * Toutes les navigations passent par ici — aucun controller ne connaît un autre controller.
 * Chaque controller reçoit une référence à AppController via setAppController().
 */
public class AppController {

    private final Stage stage;

    private static final String CSS      = "/css/style.css";
    private static final String MENU     = "/fxml/MenuView.fxml";
    private static final String LOGIN    = "/fxml/LoginView.fxml";
    private static final String GAME     = "/fxml/GameView.fxml";
    private static final String SCORES   = "/fxml/ScoresView.fxml";
    private static final String END_GAME = "/fxml/EndGameView.fxml";

    public AppController(Stage stage) {
        this.stage = stage;
        stage.setTitle("Jeu de Dames");
        stage.setResizable(false);
    }

    /* ------------------------------------------------------------------ */

    public void showMenu() {
        loadView(MENU, 730, 560);
    }

    public void showScores() {
        loadView(SCORES, 620, 620);
    }

    /** Appelée par MenuController — navigue vers l'écran de configuration (Taha). */
    public void showLogin(String mode) {
        FXMLLoader loader = buildLoader(LOGIN);
        if (loader == null) return;
        try {
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setAppController(this);
            controller.setMode(mode);
            applyScene(root, 620, 520);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Appelée par LoginController (Taha) une fois les joueurs configurés. */
    public void showGame(String player1Name, String player2Name, String mode) {
        FXMLLoader loader = buildLoader(GAME);
        if (loader == null) return;

        try {
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.setAppController(this);
            controller.startGame(player1Name, player2Name, mode);
            applyScene(root, 820, 640);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Appelée par GameController (Taha) en fin de partie. */
    public void showEndGame(String winnerName) {
        FXMLLoader loader = buildLoader(END_GAME);
        if (loader == null) return;

        try {
            Parent root = loader.load();
            EndGameController controller = loader.getController();
            controller.setAppController(this);
            controller.setWinner(winnerName);
            applyScene(root, 480, 360);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ------------------------------------------------------------------ */

    /** Charge un FXML simple (sans données à injecter après le load). */
    private void loadView(String fxmlPath, double width, double height) {
        FXMLLoader loader = buildLoader(fxmlPath);
        if (loader == null) return;

        try {
            Parent root = loader.load();
            Object controller = loader.getController();
            injectAppController(controller);
            applyScene(root, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FXMLLoader buildLoader(String fxmlPath) {
        var url = getClass().getResource(fxmlPath);
        if (url == null) {
            System.err.println("FXML introuvable : " + fxmlPath);
            return null;
        }
        return new FXMLLoader(url);
    }

    private void applyScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(
            getClass().getResource(CSS).toExternalForm()
        );
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /** Injecte this dans le controller si celui-ci expose setAppController(). */
    private void injectAppController(Object controller) {
        if (controller instanceof MenuController c)    c.setAppController(this);
        else if (controller instanceof ScoresController c) c.setAppController(this);
        else if (controller instanceof LoginController c)  c.setAppController(this);
    }
}
