package com.ensa.checkers.controller;

import com.ensa.checkers.model.dao.ScoreDAO;
import com.ensa.checkers.model.dao.ScoreEntry;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ScoresController {

    private AppController appController;

    @FXML private TableView<ScoreEntry>            tableScores;
    @FXML private TableColumn<ScoreEntry, String>  colNom;
    @FXML private TableColumn<ScoreEntry, Integer> colParties;
    @FXML private TableColumn<ScoreEntry, Integer> colVictoires;
    @FXML private TableColumn<ScoreEntry, Integer> colPoints;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colParties.setCellValueFactory(new PropertyValueFactory<>("partiesJouees"));
        colVictoires.setCellValueFactory(new PropertyValueFactory<>("victoires"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));

        tableScores.setItems(
            FXCollections.observableArrayList(new ScoreDAO().getTopScores())
        );
        tableScores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    @FXML
    private void onRetour() {
        appController.showMenu();
    }
}
