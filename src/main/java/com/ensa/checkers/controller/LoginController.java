package com.ensa.checkers.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField        nameField;
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
    }

    /** Pre-selects the combo to match the button that was pressed in MenuView. */
    public void setMode(String mode) {
        if ("HUMAIN_VS_IA".equals(mode))
            modeCombo.setValue("HUMAIN VS IA");
        else
            modeCombo.setValue("HUMAIN VS HUMAIN");
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

        String player2 = "HUMAIN_VS_IA".equals(mode) ? "Ordinateur" : "Joueur 2";

        appController.showGame(name, player2, mode);
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
