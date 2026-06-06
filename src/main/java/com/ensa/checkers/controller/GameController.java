package com.ensa.checkers.controller;

import com.ensa.checkers.model.Game;
import com.ensa.checkers.model.GameRules;
import com.ensa.checkers.model.Move;
import com.ensa.checkers.model.Piece;
import com.ensa.checkers.model.PieceColor;
import com.ensa.checkers.model.Position;
import com.ensa.checkers.model.dao.GameDAO;
import com.ensa.checkers.model.dao.PlayerDAO;
import com.ensa.checkers.model.dao.ScoreDAO;
import com.ensa.checkers.model.player.AIPlayer;
import com.ensa.checkers.model.player.HumanPlayer;
import com.ensa.checkers.model.player.Player;
import com.ensa.checkers.view.BoardView;
import com.ensa.checkers.view.BoardViewListener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contrôleur de l'écran de jeu (GameView.fxml). C'est le chef d'orchestre de la partie.
 *
 * Il fait le lien entre :
 *   - la <b>vue</b> {@link BoardView} (le plateau cliquable) ;
 *   - le <b>modèle</b> {@link Game} (la logique et les règles) ;
 *   - la <b>base de données</b> (DAO) pour enregistrer scores et parties.
 *
 * Il gère la sélection des pièces, l'application des coups, le tour de l'IA (dans un
 * thread pour ne pas geler l'interface), le minuteur, puis la fin de partie.
 */
public class GameController implements BoardViewListener {

    @FXML private StackPane conteneurPlateau;
    @FXML private Label     labelJoueurCourant;
    @FXML private Label     labelCapturesBlanc;
    @FXML private Label     labelCapturesNoir;
    @FXML private Label     labelMinuteur;
    @FXML private Button    boutonAbandon;

    private AppController appController;
    private Game          partie;
    private BoardView     vuePlateau;
    private String        mode;

    // Case actuellement sélectionnée par le joueur (null si aucune)
    private Position caseSelectionnee;

    // Nombre de pièces capturées par chaque couleur (affiché à droite)
    private int capturesBlanc = 0;
    private int capturesNoir  = 0;

    // Minuteur de la partie
    private Timeline minuteur;
    private int      secondesEcoulees = 0;

    // Vrai pendant que l'IA réfléchit (bloque les clics du joueur)
    private boolean iaReflechit = false;

    // ----------------------------------------------------------------

    public void definirAppController(AppController appController) {
        this.appController = appController;
    }

    public void demarrerPartie(String nomJoueur1, String nomJoueur2, String mode) {
        this.mode = mode;

        Player blanc = new HumanPlayer(nomJoueur1, PieceColor.WHITE);
        Player noir  = "HUMAIN_VS_IA".equals(mode)
                ? new AIPlayer(nomJoueur2, PieceColor.BLACK)
                : new HumanPlayer(nomJoueur2, PieceColor.BLACK);

        partie     = new Game(blanc, noir);
        vuePlateau = new BoardView(this);
        conteneurPlateau.getChildren().add(vuePlateau);

        // Enregistre les joueurs en base (échoue en silence si la base est absente)
        PlayerDAO.ajouterJoueur(nomJoueur1);
        if (!("HUMAIN_VS_IA".equals(mode)))
            PlayerDAO.ajouterJoueur(nomJoueur2);

        demarrerMinuteur();
        majInterface();
    }

    // ----------------------------------------------------------------  Minuteur

    private void demarrerMinuteur() {
        minuteur = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondesEcoulees++;
            labelMinuteur.setText(String.format("%02d:%02d",
                    secondesEcoulees / 60, secondesEcoulees % 60));
        }));
        minuteur.setCycleCount(Timeline.INDEFINITE);
        minuteur.play();
    }

    // ----------------------------------------------------------------  Interface

    private void majInterface() {
        vuePlateau.rafraichir(partie.getPlateau());

        Player courant = partie.getJoueurCourant();
        String etiquetteCouleur = courant.getCouleur() == PieceColor.WHITE ? "BLANC" : "NOIR";
        labelJoueurCourant.setText(courant.getNom() + "\n" + etiquetteCouleur);

        labelCapturesBlanc.setText(String.valueOf(capturesBlanc));
        labelCapturesNoir.setText(String.valueOf(capturesNoir));
    }

    // ----------------------------------------------------------------  Réactions de la vue (BoardViewListener)

    @Override
    public void onCaseCliquee(Position pos) {
        if (iaReflechit || partie.estTerminee()) return;
        if (!partie.getJoueurCourant().estHumain()) return;

        Piece piece = partie.getPlateau().getPiece(pos);

        if (caseSelectionnee == null) {
            // Premier clic : on tente de sélectionner une pièce
            if (piece != null && piece.getCouleur() == partie.getJoueurCourant().getCouleur()) {
                List<Move> coups = GameRules.getCoupsValidesPour(piece, partie.getPlateau());
                if (!coups.isEmpty()) {
                    selectionnerPiece(pos, coups);
                } else {
                    // Cette pièce n'a pas de coups légaux — signaler si capture obligatoire ailleurs
                    signalerCapturesObligatoires();
                }
            }
        } else {
            if (pos.equals(caseSelectionnee)) {
                annulerSelection();
            } else if (piece != null && piece.getCouleur() == partie.getJoueurCourant().getCouleur()) {
                // On change de sélection vers une autre pièce de notre couleur
                List<Move> coups = GameRules.getCoupsValidesPour(piece, partie.getPlateau());
                if (!coups.isEmpty()) {
                    selectionnerPiece(pos, coups);
                } else {
                    annulerSelection();
                    signalerCapturesObligatoires();
                }
            } else {
                // On essaie de déplacer la pièce sélectionnée vers la case cliquée
                Move coup = trouverCoupLegal(caseSelectionnee, pos);
                if (coup != null) jouerCoupHumain(coup);
                else annulerSelection();
            }
        }
    }

    @Override
    public void onPieceGlissee(Position depart, Position arrivee) {
        if (iaReflechit || partie.estTerminee()) return;
        if (!partie.getJoueurCourant().estHumain()) return;

        Piece piece = partie.getPlateau().getPiece(depart);
        if (piece == null || piece.getCouleur() != partie.getJoueurCourant().getCouleur()) return;

        Move coup = trouverCoupLegal(depart, arrivee);
        if (coup != null) {
            annulerSelection();
            jouerCoupHumain(coup);
        }
    }

    // ----------------------------------------------------------------  Application d'un coup

    private void jouerCoupHumain(Move coup) {
        vuePlateau.arreterAlerteCapture();
        PieceColor couleurJoueur = partie.getJoueurCourant().getCouleur();
        int nbCaptures           = coup.getPositionsCapturees().size();

        if (!partie.jouerCoup(coup)) { annulerSelection(); return; }

        if (couleurJoueur == PieceColor.WHITE) capturesBlanc += nbCaptures;
        else                                   capturesNoir  += nbCaptures;

        annulerSelection();
        majInterface();
        vuePlateau.glisserPiece(coup.getDepart(), coup.getArrivee());   // animation de glissement

        if (partie.estTerminee()) {
            terminerPartie();
        } else if (!partie.getJoueurCourant().estHumain()) {
            jouerTourIA();
        }
    }

    public void jouerTourIA() {
        // On ne lance l'IA que si le joueur courant est bien un joueur IA
        if (!(partie.getJoueurCourant() instanceof AIPlayer)) return;
        AIPlayer ia = (AIPlayer) partie.getJoueurCourant();

        iaReflechit = true;
        boutonAbandon.setDisable(true);
        vuePlateau.setDisable(true);

        // L'IA réfléchit dans un Task (thread de fond) pour ne pas geler l'interface.
        Task<Move> tache = new Task<>() {
            @Override protected Move call() { return ia.choisirCoup(partie); }
        };

        tache.setOnSucceeded(e -> {
            Move coup = tache.getValue();
            if (coup != null) {
                PieceColor couleurJoueur = partie.getJoueurCourant().getCouleur();
                int nbCaptures           = coup.getPositionsCapturees().size();
                partie.jouerCoup(coup);
                if (couleurJoueur == PieceColor.WHITE) capturesBlanc += nbCaptures;
                else                                   capturesNoir  += nbCaptures;
            }
            iaReflechit = false;
            boutonAbandon.setDisable(false);
            vuePlateau.setDisable(false);
            majInterface();
            if (coup != null) vuePlateau.glisserPiece(coup.getDepart(), coup.getArrivee());
            if (partie.estTerminee()) terminerPartie();
        });

        tache.setOnFailed(e -> {
            iaReflechit = false;
            boutonAbandon.setDisable(false);
            vuePlateau.setDisable(false);
        });

        Thread t = new Thread(tache);
        t.setDaemon(true);
        t.start();
    }

    // ----------------------------------------------------------------  Fin de partie

    public void terminerPartie() {
        if (minuteur != null) minuteur.stop();

        Player gagnant     = partie.getGagnant();
        String nomGagnant  = (gagnant != null) ? gagnant.getNom() : "NUL";

        String nom1 = partie.getJoueur(0).getNom();
        String nom2 = partie.getJoueur(1).getNom();
        GameDAO.enregistrerPartie(nom1, nom2, nomGagnant, mode);

        ScoreDAO scoreDAO = new ScoreDAO();
        scoreDAO.mettreAJourScore(nom1, gagnant != null && gagnant == partie.getJoueur(0));
        if (partie.getJoueur(1).estHumain())
            scoreDAO.mettreAJourScore(nom2, gagnant != null && gagnant == partie.getJoueur(1));

        appController.afficherFin(nomGagnant);
    }

    // ----------------------------------------------------------------  Boutons

    @FXML
    private void abandonner() {
        if (minuteur != null) minuteur.stop();

        // Le joueur qui abandonne perd — l'autre gagne
        Player perdant = partie.getJoueurCourant();
        Player gagnant = (perdant == partie.getJoueur(0)) ? partie.getJoueur(1) : partie.getJoueur(0);

        String nom1 = partie.getJoueur(0).getNom();
        String nom2 = partie.getJoueur(1).getNom();
        GameDAO.enregistrerPartie(nom1, nom2, gagnant.getNom(), mode);

        ScoreDAO scoreDAO = new ScoreDAO();
        scoreDAO.mettreAJourScore(nom1, gagnant == partie.getJoueur(0));
        if (partie.getJoueur(1).estHumain())
            scoreDAO.mettreAJourScore(nom2, gagnant == partie.getJoueur(1));

        appController.afficherFin(gagnant.getNom());
    }

    @FXML
    private void retournerAuMenu() {
        if (minuteur != null) minuteur.stop();
        appController.afficherMenu();
    }

    // ----------------------------------------------------------------  Méthodes utilitaires

    /**
     * Si des captures sont obligatoires pour le joueur courant, déclenche
     * l'animation rouge sur les cases des pièces qui peuvent capturer.
     */
    private void signalerCapturesObligatoires() {
        List<Move> obligatoires = GameRules.getCapturesObligatoires(
                partie.getPlateau(), partie.getJoueurCourant().getCouleur());
        if (!obligatoires.isEmpty()) {
            Set<Position> casesCapture = new HashSet<>();
            for (Move m : obligatoires)
                casesCapture.add(m.getDepart());
            vuePlateau.signalerCaptures(casesCapture);
        }
    }

    /** Sélectionne une pièce et met en valeur ses coups possibles sur le plateau. */
    private void selectionnerPiece(Position pos, List<Move> coups) {
        vuePlateau.arreterAlerteCapture();
        caseSelectionnee = pos;
        vuePlateau.marquerSelection(pos);
        vuePlateau.marquerCoups(arriveesDe(coups));
        vuePlateau.rafraichir(partie.getPlateau());
    }

    /** Renvoie la liste des cases d'arrivée d'une liste de coups. */
    private List<Position> arriveesDe(List<Move> coups) {
        List<Position> arrivees = new ArrayList<>();
        for (Move m : coups)
            arrivees.add(m.getArrivee());
        return arrivees;
    }

    /** Cherche, parmi les coups légaux, celui qui va de `depart` à `arrivee` (ou null). */
    private Move trouverCoupLegal(Position depart, Position arrivee) {
        for (Move m : GameRules.getCoupsLegaux(partie.getPlateau(), partie.getJoueurCourant().getCouleur()))
            if (m.getDepart().equals(depart) && m.getArrivee().equals(arrivee)) return m;
        return null;
    }

    private void annulerSelection() {
        caseSelectionnee = null;
        vuePlateau.effacerMarques();
        vuePlateau.rafraichir(partie.getPlateau());
    }
}
