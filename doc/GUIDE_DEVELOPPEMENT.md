# Guide de Développement — Jeu de Dames
## Stack : Java 17 + JavaFX 21 + Scene Builder + Maven + MySQL

---

## Architecture du projet

```
MVC strict :
  model/      → logique métier pure (aucune dépendance JavaFX)
  view/       → BoardView.java (GridPane programmatique)
              → *.fxml (Scene Builder — pas des classes Java)
  controller/ → contrôleurs FXML (@FXML) + AppController (navigation)
  model/dao/  → accès base de données (JDBC + MySQL)
```

Structure des fichiers :
```
src/main/java/com/ensa/checkers/
  Main.java
  controller/
    AppController.java
    MenuController.java
    LoginController.java
    GameController.java
    ScoresController.java
    EndGameController.java
  model/
    Board.java, Piece.java, Pawn.java, King.java
    Move.java, Position.java, PieceColor.java
    Game.java, GameState.java, MoveValidator.java, GameRules.java
    player/  Player.java, HumanPlayer.java, AIPlayer.java
    ai/      MinimaxAI.java, BoardEvaluator.java
    dao/     DatabaseManager.java, PlayerDAO.java,
             GameDAO.java, ScoreDAO.java, ScoreEntry.java
  view/
    BoardView.java, BoardViewListener.java

src/main/resources/
  fxml/
    MenuView.fxml, LoginView.fxml, GameView.fxml,
    ScoresView.fxml, EndGameView.fxml
  css/
    style.css
  images/
```

---

## État actuel (déjà fait)

### Modèle (base complète)
- [x] `PieceColor` enum (WHITE, BLACK, opposite())
- [x] `Position` (row, col, isValid, equals, hashCode)
- [x] `Move` (from, to, capturedPositions, addCaptured, isCapture, isPromotion)
- [x] `Piece` abstract (color, position, getPossibleMoves abstract)
- [x] `Pawn` (getPossibleMoves : déplacements + captures + promotion)
- [x] `King` (getPossibleMoves : toutes directions, captures)
- [x] `Board` (grid 8x8, initialize, getPieceAt, applyMove, removePiece, copy)
- [x] `GameState` enum (EN_COURS, VICTOIRE_BLANC, VICTOIRE_NOIR, NUL)
- [x] `ScoreEntry` (DTO : username, gamesPlayed, gamesWon, totalPoints)

### Infrastructure
- [x] Maven (pom.xml : javafx-controls, javafx-fxml, mysql-connector-j)
- [x] JavaFX 21 résolu + `mvn javafx:run` fonctionnel
- [x] Scene Builder 25 installé et lié à IntelliJ
- [x] Structure de packages propre + diagrammes à jour (doc/*.puml)

---

## Répartition des tâches

> **Principe :** chacun touche aux **4 couches** (UI, logique, IA, DAO) pour
> que les deux apprennent toute la stack. Les parties ne se rencontrent qu'à
> travers les **contrats** définis plus bas.

### 🔵 ISMAIL — branche `feat/ismail`

**🖥️ UI**
- [ ] `Main.java` — point d'entrée, crée l'AppController et lance start()
- [ ] `AppController.java` — routeur (FXMLLoader + Stage.setScene), showMenu/showScores/showGame/showEndGame
- [ ] `MenuView.fxml` (Scene Builder) + `MenuController.java` — boutons Jouer vs Humain / vs IA / Scores / Quitter
- [ ] `ScoresView.fxml` (Scene Builder) + `ScoresController.java` — TableView, initialize() charge via ScoreDAO
- [ ] `BoardView.java` — extends GridPane, grille 8x8 (Rectangle + Circle), clics + drag & drop
- [ ] `BoardViewListener.java` — interface onCellClicked(pos), onMoveDragged(from, to)
- [ ] `style.css` — thème sombre, boutons, labels

**🧠 Logique**
- [ ] `GameRules.java` — checkWinner(game), shouldPromote(piece), isCaptureMandatory(board, player)

**🤖 IA**
- [ ] `BoardEvaluator.java` — evaluate(board, color) : +10 par pion adverse manquant, +30 par dame

**💾 DAO**
- [ ] `DatabaseManager.java` — singleton connexion JDBC (credentials dans config.properties)
- [ ] `ScoreDAO.java` — updateScore(playerId, result), getTopScores(limit)
- [ ] `config.properties` — url, user, password de la base MySQL (lu par DatabaseManager)

---

### 🟢 TAHA — branche `feat/taha`

**🖥️ UI**
- [ ] `LoginView.fxml` (Scene Builder) + `LoginController.java` — TextField nom, choix mode, crée les joueurs
- [ ] `GameView.fxml` (Scene Builder) — BorderPane : centre = conteneur BoardView, droite = labels (joueur/captures/timer), bas = Abandonner
- [ ] `GameController.java` — implémente BoardViewListener, startGame, onCellClicked/onMoveDragged → Game.tryPlay, handleAITurn dans un Task<Move>
- [ ] `EndGameView.fxml` (Scene Builder) + `EndGameController.java` — résultat + boutons Rejouer / Menu (reçoit le gagnant de GameController)

**🧠 Logique**
- [ ] `Player.java` (abstract) + `HumanPlayer.java` + `AIPlayer.java` (chooseMove via MinimaxAI)
- [ ] `MoveValidator.java` — isValidMove, getValidMovesFor, getMandatoryCaptures (*capture obligatoire*)
- [ ] `Game.java` — orchestrateur : board, players[2], state, tryPlay(move), getCurrentPlayer, isOver

**🤖 IA**
- [ ] `MinimaxAI.java` — findBestMove(game), minimax + élagage alpha-bêta, profondeur 4

**💾 DAO**
- [ ] `schema.sql` — script de création de la base MySQL + tables `players`, `games`, `scores`
- [ ] `PlayerDAO.java` — save(player), findByUsername(name), exists(name)
- [ ] `GameDAO.java` — save(game, winnerId, sec), findRecent(limit)

---

## Contrats entre vous (à fixer dès le début)

Un **contrat** = un point de contact entre vos deux parties. Avant de coder les
classes couplées, mettez-vous d'accord sur les signatures exactes. Chacun pilote 2 contrats.

### 🔵 ISMAIL
- [ ] **Contrat BoardView** — définir `setBoard(board)`, `setListener(l)`, `setSelected(pos)`, `clearHighlights()` (appelées par `GameController` de Taha)
- [ ] **Contrat DatabaseManager** — définir `getInstance()`, `getConnection()` (appelées par tous les DAO)

### 🟢 TAHA
- [ ] **Contrat GameRules** — définir `checkWinner(game)`, `shouldPromote(piece)`, `isCaptureMandatory(board, player)` (appelées par `Game`)
- [ ] **Contrat BoardEvaluator** — définir `evaluate(board, color)` (appelée par `MinimaxAI`)

---

## Ordre de travail conseillé

1. **Chacun commence par sa partie UI** (indépendante, résultat visible vite) :
   - Ismail : `Main` + `AppController` + `MenuView` (l'app démarre)
   - Taha : `Player` + `Game` + `MoveValidator` (la logique tourne)
2. **Fixer les contrats** (signatures) avant de coder les classes couplées.
3. **Coder en parallèle** chacun de son côté.
4. **Intégration finale** : brancher `GameController` (Taha) sur `BoardView` (Ismail) et tester le flux complet.

---

## Plan de branches Git

| Branche | Qui | Contenu |
|---|---|---|
| `feat/ismail` | Ismail | UI (Main, AppController, Menu, Scores, EndGame, BoardView), GameRules, BoardEvaluator, DatabaseManager, ScoreDAO |
| `feat/taha` | Taha | UI (Login, Game), Player/Human/AIPlayer, MoveValidator, Game, MinimaxAI, PlayerDAO, GameDAO |
| `feat/integration` | Ensemble | merge des deux branches + tests du flux complet |

---

## Checklist finale avant rendu

- [ ] `mvn javafx:run` démarre sans erreur
- [ ] Menu → Login → Plateau → fin de partie → scores : flux complet fonctionnel
- [ ] Partie Humain vs Humain jouable
- [ ] Partie Humain vs IA jouable (IA ne gèle pas l'interface)
- [ ] Scores sauvegardés et affichés depuis MySQL
- [ ] Diagrammes de classes à jour dans `doc/`
