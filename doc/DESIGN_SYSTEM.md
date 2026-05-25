# 🎨 DESIGN SYSTEM — Jeu de Dames

> **Bible visuelle & guide de référence**
> Java 17 · JavaFX 21 · Architecture MVC strict · Vues Scene Builder + `style.css` central
> Thème : **« Bois Moderne »**

---

## 1. Philosophie & Thème Global

L'application repose sur une tension maîtrisée entre **l'organique** et **le numérique**.

- **Skeuomorphisme** pour tout ce qui touche au *jeu* : le plateau, les textures de bois, les pions en relief, les boutons sculptés dans le bois. Ces éléments évoquent un véritable plateau de dames posé sur une table.
- **Material / Flat Design** pour tout ce qui touche à *l'interface* : panneaux crème translucides, champs de saisie épurés, tableaux de scores propres, coins arrondis et ombres douces.

**Mots-clés de l'ambiance :** élégance, chaleur, clarté, contraste, lisibilité.

L'objectif est qu'un joueur ait l'impression de manipuler un objet réel et chaleureux (le bois), tout en bénéficiant du confort d'une interface logicielle moderne (les panneaux et menus).

> **Règle d'or :** le *bois* habille le contenu de jeu ; le *crème/flat* habille les contrôles et l'information. On ne mélange jamais les deux registres sur un même composant.

---

## 2. Palette de Couleurs

Codes HEX approximatifs relevés sur la maquette. À déclarer en tant que variables `LOOKED-UP COLOR` (`-fx-*`) au sommet du `style.css` via le bloc `.root`.

### 2.1 Couleurs de fond — le Bois

| Rôle | Nom variable | HEX | Usage |
|------|--------------|-----|-------|
| Bois sombre (fond principal) | `-wood-dark` | `#3E2A20` | Fond du menu principal, fond du tableau des scores |
| Bois sombre dégradé bas | `-wood-dark-2` | `#2A1A12` | Bas du dégradé vertical du fond |
| Bois moyen (boutons) | `-wood-medium` | `#6B4A33` | Texture des boutons en relief, en-têtes |
| Bois clair / beige (cases claires) | `-wood-light` | `#D9B98C` | Cases claires du damier |
| Bois foncé (cases sombres) | `-wood-square-dark` | `#9C6B3F` | Cases sombres du damier |

### 2.2 Couleurs d'interface — Panneaux

| Rôle | Nom variable | HEX | Usage |
|------|--------------|-----|-------|
| Crème panneau | `-panel-cream` | `#F3EAD8` | Fond des panneaux (Login, Joueur courant, Captures) |
| Blanc cassé | `-panel-offwhite` | `#FBF6EC` | Cartes claires, fond des champs |
| Bordure panneau | `-panel-border` | `#C9B79A` | Liserés subtils des panneaux et cartes |
| Translucide (overlay Login) | `-panel-glass` | `rgba(243, 234, 216, 0.92)` | Panneau de configuration posé sur le damier |

### 2.3 Couleurs typographiques

| Rôle | Nom variable | HEX | Usage |
|------|--------------|-----|-------|
| Texte clair sur bois | `-text-on-dark` | `#F5E9D6` | Titres « JEU DE DAMES », labels de boutons |
| Texte sombre sur crème | `-text-on-light` | `#3A2A1E` | Labels de formulaires, contenu des tableaux |
| Texte secondaire | `-text-muted` | `#8A7560` | Sous-titres, libellés discrets (ex. « MINUTEUR ») |

### 2.4 Couleurs d'accentuation & jeu

| Rôle | Nom variable | HEX | Usage |
|------|--------------|-----|-------|
| Jaune sélection | `-accent-select` | `#F2C400` | Contour des cases sélectionnées / coups possibles |
| Jaune sélection (lueur) | `-accent-select-glow` | `rgba(242, 196, 0, 0.55)` | Halo de mise en valeur |
| Pion blanc (corps) | `-piece-white` | `#F7F3EA` | Pions clairs |
| Pion blanc (relief) | `-piece-white-ring` | `#C9C2B2` | Anneaux/ombres du pion blanc |
| Pion noir (corps) | `-piece-black` | `#3B2C22` | Pions foncés |
| Pion noir (relief) | `-piece-black-ring` | `#1E140D` | Anneaux/ombres du pion noir |
| Focus champ (cadre bleu) | `-focus-ring` | `#2E8BE6` | Bordure d'un `TextField` actif |

---

## 3. Typographie

Police principale recommandée : **Montserrat** (ou **Poppins**) pour les titres — caractère géométrique et impactant ; **Open Sans** ou **Roboto** pour le corps de texte — neutralité et lisibilité maximale.

> Embarquer les `.ttf` dans `src/main/resources/fonts/` et les charger via `Font.loadFont(...)` au démarrage pour garantir le rendu identique sur toutes les machines (pas de dépendance aux polices système).

| Niveau | Police | Taille | Graisse | Casse | Exemple |
|--------|--------|--------|---------|-------|---------|
| Titre principal (H1) | Montserrat | 42–48 px | **Bold (700)** | MAJUSCULES | `JEU DE DAMES` |
| Titre de vue (H2) | Montserrat | 28–32 px | **Bold (700)** | MAJUSCULES | `TABLEAU DES SCORES` |
| Label de bouton | Montserrat | 18–20 px | **SemiBold (600)** | MAJUSCULES | `JOUER VS HUMAIN` |
| Label de formulaire | Open Sans | 13–14 px | **Bold (700)** | MAJUSCULES | `NOM DU JOUEUR` |
| Corps / contenu tableau | Open Sans | 14–15 px | Regular (400) | Normal | `Ismail` |
| Texte secondaire | Open Sans | 12–13 px | Regular (400) | MAJUSCULES | `MINUTEUR : 01:23` |

**Lettrage (letter-spacing) :** appliquer un léger espacement (`0.5px` à `1px`) sur les titres et labels en majuscules pour renforcer l'élégance.

```css
.root {
    -fx-font-family: "Open Sans", sans-serif;
}
.title-h1 {
    -fx-font-family: "Montserrat";
    -fx-font-size: 46px;
    -fx-font-weight: bold;
    -fx-text-fill: -text-on-dark;
    /* JavaFX n'a pas de letter-spacing CSS : utiliser Text.setLetterSpacing ou un espacement visuel */
}
```

---

## 4. Composants d'Interface (règles JavaFX / CSS)

### 4.1 Boutons

Deux familles distinctes :

**A. Boutons « Bois » (menu principal)** — skeuomorphes, en relief.

- **Forme :** coins très arrondis, quasi pilule → `-fx-background-radius: 30px;`
- **Fond :** dégradé vertical bois clair → bois moyen pour simuler le galbe.
- **Ombre portée :** `dropshadow` subtile pour décoller le bouton du fond.
- **Texte :** clair (`-text-on-dark`), SemiBold, avec icône à gauche.

```css
.button-wood {
    -fx-background-color: linear-gradient(to bottom, #8A6240, #5E3F2A);
    -fx-background-radius: 30px;
    -fx-text-fill: -text-on-dark;
    -fx-font-family: "Montserrat";
    -fx-font-size: 19px;
    -fx-font-weight: 600;
    -fx-padding: 14px 28px;
    -fx-cursor: hand;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 10, 0.2, 0, 4);
}
.button-wood:hover {
    -fx-background-color: linear-gradient(to bottom, #9C724E, #6B4A33);
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 14, 0.3, 0, 5);
}
.button-wood:pressed {
    -fx-background-color: linear-gradient(to bottom, #5E3F2A, #4A3120);
    -fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 8, 0.2, 0, 2);
    -fx-translate-y: 1px;
}
```

**B. Boutons « Action » (Commencer, Abandonner, Retour au menu)** — flat, compacts.

- **Forme :** coins arrondis modérés → `-fx-background-radius: 8px;`
- **Fond :** bois moyen uni (`-wood-medium`) ou crème selon le contexte.

```css
.button-primary {
    -fx-background-color: -wood-medium;
    -fx-background-radius: 8px;
    -fx-text-fill: -text-on-dark;
    -fx-font-weight: 600;
    -fx-padding: 10px 22px;
    -fx-cursor: hand;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0.1, 0, 2);
}
.button-primary:hover { -fx-background-color: derive(-wood-medium, 12%); }
.button-primary:pressed { -fx-background-color: derive(-wood-medium, -12%); }
```

### 4.2 Panneaux & Cartes

Pour le panneau de configuration (Login), « Joueur courant », « Captures ».

- **Fond :** crème (`-panel-cream`) ou translucide (`-panel-glass`) quand posé sur le damier.
- **Coins arrondis :** `-fx-background-radius: 14px;`
- **Bordure :** liseré subtil `-panel-border`, 1px.
- **Ombre :** douce et diffuse pour faire flotter la carte au-dessus du bois.

```css
.panel-cream {
    -fx-background-color: -panel-cream;
    -fx-background-radius: 14px;
    -fx-border-color: -panel-border;
    -fx-border-radius: 14px;
    -fx-border-width: 1px;
    -fx-padding: 20px;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 18, 0.15, 0, 6);
}
/* Variante translucide posée sur le plateau (écran de configuration) */
.panel-glass {
    -fx-background-color: rgba(243, 234, 216, 0.92);
    -fx-background-radius: 14px;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 24, 0.2, 0, 8);
}
```

### 4.3 Champs de saisie (`TextField`) & Menus (`ComboBox`)

Style minimaliste, fond blanc cassé, bordure discrète, focus mis en valeur par un cadre bleu (comme sur la maquette Login).

```css
.text-field, .combo-box {
    -fx-background-color: -panel-offwhite;
    -fx-background-radius: 6px;
    -fx-border-color: -panel-border;
    -fx-border-radius: 6px;
    -fx-border-width: 1px;
    -fx-padding: 8px 12px;
    -fx-font-size: 14px;
    -fx-text-fill: -text-on-light;
}
.text-field:focused {
    -fx-border-color: -focus-ring;
    -fx-border-width: 2px;
    -fx-effect: dropshadow(gaussian, rgba(46,139,230,0.35), 6, 0.2, 0, 0);
}
.combo-box .list-cell:filled:hover,
.combo-box-popup .list-view .list-cell:hover {
    -fx-background-color: -wood-medium;
    -fx-text-fill: -text-on-dark;   /* item survolé en bois foncé, texte clair (cf. maquette) */
}
```

### 4.4 Tableaux (`TableView`) — Tableau des scores

- **En-tête :** fond bois sombre (`-wood-dark` / `-wood-medium`), texte clair, gras, centré.
- **Lignes :** alternance crème / blanc cassé (*zebra striping*).
- **Survol de ligne :** légère teinte plus chaude.
- **Bordures :** quasi invisibles, fines lignes crème.

```css
.table-scores {
    -fx-background-color: transparent;
    -fx-background-radius: 10px;
    -fx-border-color: -panel-border;
    -fx-border-radius: 10px;
}
/* En-tête sombre */
.table-scores .column-header-background { -fx-background-color: transparent; }
.table-scores .column-header,
.table-scores .filler {
    -fx-background-color: -wood-dark;
}
.table-scores .column-header .label {
    -fx-text-fill: -text-on-dark;
    -fx-font-weight: bold;
    -fx-alignment: CENTER;
}
/* Zebra striping crème / blanc cassé */
.table-scores .table-row-cell {
    -fx-background-color: -panel-offwhite;
    -fx-table-cell-border-color: transparent;
}
.table-scores .table-row-cell:odd {
    -fx-background-color: -panel-cream;
}
.table-scores .table-row-cell:hover {
    -fx-background-color: derive(-panel-cream, -6%);
}
.table-scores .table-cell {
    -fx-text-fill: -text-on-light;
    -fx-alignment: CENTER-LEFT;
    -fx-padding: 8px 12px;
}
```

> 💡 Les médailles 🥇🥈🥉 des 3 premiers se gèrent côté contrôleur via une `cellFactory` qui injecte une icône `ImageView` dans la colonne « NOM ».

---

## 5. Layout & Espacement

Une grille d'espacement basée sur un **multiple de 8 px** garantit la cohérence. L'interface doit *respirer* sans être dispersée.

| Token | Valeur | Usage |
|-------|--------|-------|
| `space-xs` | 4 px | Espacement icône ↔ texte |
| `space-sm` | 8 px | Padding interne compact |
| `space-md` | 16 px | Padding standard des panneaux, gap entre boutons |
| `space-lg` | 24 px | Marge autour des blocs principaux |
| `space-xl` | 40 px | Marge des titres de vue, espacement vertical du menu |

**Règles générales :**

- **Menu principal :** boutons empilés en `VBox` avec `-fx-spacing: 18;`, largeur uniforme, centrés.
- **Panneaux latéraux (vue jeu) :** `padding: 16–20px`, `spacing: 16px` entre les sections.
- **Formulaires :** `spacing: 12px` entre label et champ ; label collé au champ (`4px`).
- **Marge écran :** garder au minimum `24px` entre le bord de la fenêtre et le contenu, sauf pour les fonds bois qui occupent 100 %.
- **Plateau de jeu :** carré, occupe l'espace dominant ; les panneaux d'info prennent ~25–30 % de la largeur à droite.

---

## 6. Bonnes Pratiques CSS JavaFX (travail en équipe)

Pour que mon binôme et moi puissions travailler sans nous marcher dessus :

1. **Aucun style en ligne dans Scene Builder.** On n'utilise jamais le champ *Style* de l'inspecteur. Tout passe par des **classes CSS** (`Style Class` dans l'onglet *Properties*) définies dans `style.css`.

2. **Un seul `style.css` central**, lié une fois dans chaque FXML ou globalement sur la `Scene` :
   ```java
   scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
   ```

3. **Variables de couleurs en tête de fichier** dans `.root` (voir section 2). On ne réécrit jamais un HEX en dur ailleurs — toujours `-wood-dark`, `-accent-select`, etc. Changer une couleur = une seule ligne à modifier.

4. **Nommage des classes** : préfixe par rôle, kebab-case, explicite.
   - `.button-wood`, `.button-primary` — boutons
   - `.panel-cream`, `.panel-glass` — panneaux
   - `.table-scores` — tableau des scores
   - `.title-h1`, `.title-h2`, `.form-label` — typographie
   - `.board-square-light`, `.board-square-dark`, `.square-selected` — damier
   - `.piece-white`, `.piece-black` — pions

5. **Pseudo-classes pour les états** plutôt que du code : `:hover`, `:pressed`, `:focused`, `:odd`, `:selected`. Les transitions visuelles vivent dans le CSS, jamais dans le contrôleur.

6. **Respect du MVC :** le CSS et les FXML appartiennent à la couche **View**. Aucune logique métier ni `setStyle()` programmatique dans les contrôleurs — uniquement de l'ajout/retrait de classes via `getStyleClass().add(...)` / `.remove(...)` (ex. pour surligner une case sélectionnée en jaune).

7. **Organisation du fichier `style.css`** en sections commentées et ordonnées :
   ```css
   /* ===== 1. VARIABLES (.root) ===== */
   /* ===== 2. TYPOGRAPHIE ===== */
   /* ===== 3. BOUTONS ===== */
   /* ===== 4. PANNEAUX & CARTES ===== */
   /* ===== 5. CHAMPS & MENUS ===== */
   /* ===== 6. TABLEAU DES SCORES ===== */
   /* ===== 7. PLATEAU & PIONS ===== */
   ```

8. **Convention d'équipe :** on commente toute classe non triviale, et on ne supprime jamais une classe utilisée dans un FXML sans prévenir l'autre (chercher la classe dans tous les `.fxml` avant suppression).

---

*Document de référence — à tenir à jour à chaque évolution visuelle. En cas de doute, la maquette fait foi.*
