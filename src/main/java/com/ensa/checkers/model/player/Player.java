package com.ensa.checkers.model.player;

import com.ensa.checkers.model.PieceColor;

/**
 * Classe abstraite représentant un joueur (un humain ou l'ordinateur).
 *
 * Tout joueur possède un nom et une couleur. La méthode {@link #estHumain()} permet
 * au reste du programme de savoir s'il faut attendre un clic (humain) ou faire
 * jouer l'IA, sans connaître le type concret du joueur.
 */
public abstract class Player {

    protected final String nom;
    protected final PieceColor couleur;

    public Player(String nom, PieceColor couleur) {
        this.nom  = nom;
        this.couleur = couleur;
    }

    public String     getNom()     { return nom; }
    public PieceColor getCouleur() { return couleur; }

    /** True si c'est un joueur humain, false si c'est l'ordinateur. */
    public abstract boolean estHumain();
}
