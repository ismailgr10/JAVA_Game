package com.ensa.checkers.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Contrôleur de l'écran de configuration (LoginView.fxml).
 *
 * Permet de saisir le(s) nom(s) du/des joueur(s) et de choisir le mode
 * (Humain vs Humain ou Humain vs IA). Le second champ de nom n'apparaît qu'en
 * mode Humain vs Humain. À la validation, on lance la partie via {@link AppController}.
 */
public class LoginController {

    @FXML private TextField        champNom;
    @FXML private TextField        champNom2;
    @FXML private VBox             boiteJoueur2;
    @FXML private ComboBox<String> listeMode;
    @FXML private Label            labelErreur;

    private AppController appController;

    public void definirAppController(AppController appController) {
        this.appController = appController;
    }

    /** Appelée automatiquement par JavaFX au chargement de la vue. */
    @FXML
    public void initialize() {
        listeMode.setItems(FXCollections.observableArrayList(
            "HUMAIN VS HUMAIN",
            "HUMAIN VS IA"
        ));
        listeMode.setValue("HUMAIN VS HUMAIN");

        // Affiche / masque le champ du joueur 2 selon le mode choisi
        listeMode.valueProperty().addListener((obs, ancien, nouveau) -> majChampJoueur2());
        majChampJoueur2();
    }

    /** Pré-sélectionne le mode dans la liste selon le bouton cliqué dans le menu. */
    public void definirMode(String mode) {
        if ("HUMAIN_VS_IA".equals(mode))
            listeMode.setValue("HUMAIN VS IA");
        else
            listeMode.setValue("HUMAIN VS HUMAIN");
        majChampJoueur2();
    }

    /** Le champ « joueur 2 » n'apparaît qu'en mode Humain vs Humain. */
    private void majChampJoueur2() {
        boolean humainVsHumain = "HUMAIN VS HUMAIN".equals(listeMode.getValue());
        boiteJoueur2.setVisible(humainVsHumain);
        boiteJoueur2.setManaged(humainVsHumain);
    }

    @FXML
    private void confirmer() {
        String nom = champNom.getText().trim();
        if (nom.isEmpty()) {
            afficherErreur("Veuillez entrer votre nom.");
            return;
        }

        // Transforme le libellé affiché ("HUMAIN VS IA") en clé interne ("HUMAIN_VS_IA")
        String choisi = listeMode.getValue();
        String mode   = (choisi != null) ? choisi.replace(" ", "_") : "HUMAIN_VS_HUMAIN";

        String joueur2;
        if ("HUMAIN_VS_IA".equals(mode)) {
            joueur2 = "Ordinateur";
        } else {
            joueur2 = champNom2.getText().trim();
            if (joueur2.isEmpty()) {
                afficherErreur("Veuillez entrer le nom du joueur 2.");
                return;
            }
        }

        appController.afficherPartie(nom, joueur2, mode);
    }

    private void afficherErreur(String message) {
        labelErreur.setText(message);
        labelErreur.setVisible(true);
        labelErreur.setManaged(true);
    }
}
