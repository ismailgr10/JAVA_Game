package com.ensa.checkers.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private TextField        nameField;
    @FXML private TextField        name2Field;
    @FXML private VBox             player2Box;
    @FXML private ComboBox<String> modeCombo;
    @FXML private Label            errorLabel;

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    public void initialize() {
        modeCombo.setItems(FXCollections.observableArrayList(
            "HUMAIN VS HUMAIN",
            "HUMAIN VS IA"
        ));
        modeCombo.setValue("HUMAIN VS HUMAIN");

        // Affiche / masque le champ du joueur 2 selon le mode choisi
        modeCombo.valueProperty().addListener((obs, ancien, nouveau) -> updatePlayer2Field());
        updatePlayer2Field();
    }

    /** Pre-selects the combo to match the button that was pressed in MenuView. */
    public void setMode(String mode) {
        if ("HUMAIN_VS_IA".equals(mode))
            modeCombo.setValue("HUMAIN VS IA");
        else
            modeCombo.setValue("HUMAIN VS HUMAIN");
        updatePlayer2Field();
    }

    /** Le champ « joueur 2 » n'apparaît qu'en mode Humain vs Humain. */
    private void updatePlayer2Field() {
        boolean humainVsHumain = "HUMAIN VS HUMAIN".equals(modeCombo.getValue());
        player2Box.setVisible(humainVsHumain);
        player2Box.setManaged(humainVsHumain);
    }

    @FXML
    private void onConfirm() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Veuillez entrer votre nom.");
            return;
        }

        // Convert display label → internal key
        String selected = modeCombo.getValue();
        String mode     = (selected != null) ? selected.replace(" ", "_") : "HUMAIN_VS_HUMAIN";

        String player2;
        if ("HUMAIN_VS_IA".equals(mode)) {
            player2 = "Ordinateur";
        } else {
            player2 = name2Field.getText().trim();
            if (player2.isEmpty()) {
                showError("Veuillez entrer le nom du joueur 2.");
                return;
            }
        }

        appController.showGame(name, player2, mode);
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
