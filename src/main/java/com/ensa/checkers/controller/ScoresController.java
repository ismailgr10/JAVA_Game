package com.ensa.checkers.controller;

import com.ensa.checkers.model.dao.ScoreDAO;
import com.ensa.checkers.model.dao.ScoreEntry;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Contrôleur de l'écran des scores (ScoresView.fxml).
 *
 * Au chargement, on relie chaque colonne du tableau à une propriété de
 * {@link ScoreEntry}, puis on remplit le tableau avec les meilleurs scores
 * lus en base via {@link ScoreDAO}.
 */
public class ScoresController {

    private AppController appController;

    @FXML private TableView<ScoreEntry>            tableauScores;
    @FXML private TableColumn<ScoreEntry, String>  colNom;
    @FXML private TableColumn<ScoreEntry, Integer> colParties;
    @FXML private TableColumn<ScoreEntry, Integer> colVictoires;
    @FXML private TableColumn<ScoreEntry, Integer> colPoints;

    public void definirAppController(AppController appController) {
        this.appController = appController;
    }

    /** Appelée automatiquement par JavaFX au chargement de la vue. */
    @FXML
    public void initialize() {
        // On indique à chaque colonne quelle propriété de ScoreEntry afficher
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colParties.setCellValueFactory(new PropertyValueFactory<>("partiesJouees"));
        colVictoires.setCellValueFactory(new PropertyValueFactory<>("victoires"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));

        // On remplit le tableau avec les scores lus en base de données
        tableauScores.setItems(
            FXCollections.observableArrayList(new ScoreDAO().getMeilleursScores())
        );
        tableauScores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    @FXML
    private void retour() {
        appController.afficherMenu();
    }
}
