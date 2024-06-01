# CompilerMiniAda

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![Graphviz](https://img.shields.io/badge/Graphviz-204374.svg?style=for-the-badge&logo=Graphviz&logoColor=white)

CompilerMiniAda est un compilateur construit de zéro en partant uniquement d'une grammaire donnée reprenant la syntaxe du langage Ada.

## Grammaire

La grammaire utilisée pour ce projet se trouve dans le dossier `/doc/grammaire/grammaire_originale.png`.
Elle a été transformée en une grammaire LL(1) à la main pour faciliter l'analyse syntaxique.
Les explications de la construction de cette dernière peuvent être retrouvées dans le [rapport du projet](/doc/livrable/rapport.pdf).

![Grammaire](/doc/grammaire/grammaire_originale.png)

# Fonctionnalités

Les fonctionnalités prises en charge par le compilateur sont les suivantes :

## Programme principal avec structures de contrôle élémentaires

- Les déclarations de variables, avec et sans initialisation.
- Les affectations simples et les affectations d'expressions arithmétiques (addition, soustraction).
- Les expressions arithmétiques (avec variables, gestion des priorités, opérateurs logiques, etc.).

## Appels de fonctions

- Les définitions et les appels de fonctions et procédures avec paramètres.

## Récursivité et imbrication des constructions `if`/`for`/`while`

- Les appels récursifs.
- Les constructions `if`, `while` et `for` imbriquées.

## Les records

- L'opération `put()`.
- Les définitions de type.
- Les records et l'accès aux champs.
- Les opérations sur les records.
- Les records imbriqués.
- Les records en paramètres de fonctions.
- Les records en retour de fonctions.

## Prérequis

Pour utiliser l'affichage en graphique, il est nécessaire d'installer les dépendances Python avec la commande suivante :
```
pip install -r ./python/requirements.txt
```

## Utilisation

La classe principale du projet est `org.trad.pcl.Main`. Voici les différentes commandes possibles :

- Pour afficher tous les tokens du fichier en entrée :
```
java Main <file> -t
```
- Pour générer le graphe AST du fichier en entrée :
```
java Main <file> -g
```
- Pour lancer l'analyse sémantique et la génération de code ASM du fichier en entrée :
```
java Main <file>
```
Note : le fichier en entrée doit avoir l'extension `.canAda`.

## Tests

Des classes de test sont disponibles dans le dossier `src/tests/java` :

- `LexerTest` : teste la classe `Lexer` en charge de l'analyse lexicale.
- `ParserTest` : teste la classe `Parser` en charge de l'analyse syntaxique.
- `SemanticTest` : teste la classe `SemanticAnalysisVisitor` en charge de l'analyse sémantique.

Un dossier `AstTest` est également disponible avec les classes suivantes :

- `AstTest` : teste la génération de l'arbre de syntaxe abstraite (AST).
- `DeclarationTest` : teste la génération des nœuds de déclaration de l'AST.
- `StatementTest` : teste la génération des nœuds d'instruction de l'AST.

Ces tests utilisent des fichiers `.canAda` en entrée qui se trouvent dans le dossier `src/main/resources/tests`.

## Programme de démonstration

Un programme de démonstration est disponible dans le dossier `src/main/resources/demo` :

- `multiplicationTable.canAda` : programme affichant la table de multiplication de 1 à 10.
- `sumFirstIntegers.canAda` : programme calculant la somme des `n` premiers entiers.
- `factorial.canAda` : programme récursif calculant le factoriel d'un nombre.
- `tictactoe.canAda` : programme simulant un jeu de morpion.
- `combatSimulator.canAda` : programme simulant un combat entre deux personnages.