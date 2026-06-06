package com.ensa.checkers.model.dao;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Représente une ligne du tableau des scores (nom, parties jouées, victoires, points).
 *
 * On utilise des « Property » JavaFX au lieu de simples champs : c'est ce que le
 * {@code TableView} de l'écran des scores sait lire automatiquement pour remplir
 * et rafraîchir chaque colonne.
 */
public class ScoreEntry {

    private final StringProperty  nom;
    private final IntegerProperty partiesJouees;
    private final IntegerProperty victoires;
    private final IntegerProperty points;

    public ScoreEntry(String nom, int partiesJouees, int victoires, int points) {
        this.nom          = new SimpleStringProperty(nom);
        this.partiesJouees = new SimpleIntegerProperty(partiesJouees);
        this.victoires     = new SimpleIntegerProperty(victoires);
        this.points        = new SimpleIntegerProperty(points);
    }

    public StringProperty  nomProperty()          { return nom; }
    public IntegerProperty partiesJoueesProperty() { return partiesJouees; }
    public IntegerProperty victoiresProperty()     { return victoires; }
    public IntegerProperty pointsProperty()        { return points; }

    public String getNom()           { return nom.get(); }
    public int    getPartiesJouees() { return partiesJouees.get(); }
    public int    getVictoires()     { return victoires.get(); }
    public int    getPoints()        { return points.get(); }
}
