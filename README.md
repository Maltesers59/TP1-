# TP : Tests unitaires avec JUnit

## 1. Présentation : quoi, en quelques lignes, ce que fait le projet :

Le projet est une application de gestion de comptes bancaires développée en Java. Nous devons faire des tests unitaires avec ***JUnit 5***. Il permet de créer des *comptes bancaires* avec différents ajout, et de gérer plusieurs comptes avec un *GestionnaireComptes* qui va lui aussi avoir différentes options 

## 2. Choix de conception : pourquoi ce découpage de classes, gestion des exceptions, avez-vous fait du TDD ou testé après coup ?

**Pourquoi deux classes séparées.** J'ai séparé le projet en deux classes parce qu'elles ne s'occupent pas de la même chose : `CompteBancaire` gère un seul compte, alors que `GestionnaireComptes` s'occupe de gérer plusieurs comptes en même temps. Ça évite de tout mettre dans une seule grosse classe, et si plus tard on voulait par exemple sauvegarder les comptes dans une base de données, ça toucherait seulement `GestionnaireComptes`, pas `CompteBancaire`. J'ai aussi mis les exceptions dans leur propre dossier (`exceptions`) pour ne pas les mélanger avec le code qui gère vraiment les comptes.

**Pourquoi des exceptions personnalisées.** Plutôt que d'utiliser des exceptions Java génériques, j'ai créé 4 exceptions à moi (`MontantInvalideException`, `SoldeInsuffisantException`, `CompteInconnuException`, `CompteDejaExistantException`). Comme ça, quand une erreur arrive, on sait tout de suite de quel type de problème il s'agit, et dans les tests c'est plus simple de vérifier qu'une méthode plante bien pour la bonne raison.

**TDD ou tests après coup.** J'ai d'abord codé les classes `CompteBancaire` et `GestionnaireComptes` en suivant le tableau des méthodes donné dans le sujet, puis j'ai écrit les tests après, en reprenant un par un tous les cas demandés pour être sûr de n'en avoir oublié aucun, comme le coup du retrait d'un centime de trop par rapport au découvert autorisé. Je n'ai pas fait du TDD pur (où on écrit le test avant le code) parce que le sujet donnait déjà toutes les règles à l'avance, donc il suffisait de les coder puis de vérifier avec les tests.pour bien séparer la logique métier de la gestion des cas d'erreur.

## 3. Comment lancer les tests

Avec Maven :

```bash
mvn test
```

## 4. Récapitulatif des tests

| Classe de test | Nombre de tests | Ce qu'ils couvrent |
|---|---|---|
| `CompteBancaireTest` | 13 | Dépôt et retrait normaux, calcul des intérêts, retrait qui tombe pile sur le découvert autorisé, retrait d'un centime de trop, montants à zéro, montants négatifs, taux négatif, vérification de `estEnDecouvert()` |
| `GestionnaireComptesTest` | 9 | Virement qui marche, calcul du solde total, recherche d'un compte par IBAN, IBAN qui n'existe pas, IBAN déjà utilisé, virement qui échoue au milieu (vérifie que rien n'a bougé), virement avec un compte source inconnu, liste des comptes en découvert |

## 5. Difficultés rencontrées

- **Savoir où mettre la limite du découvert** : le plus dur ça a été de bien choisir entre `<` et `<=` pour définir si un retrait qui tombe pile sur le découvert autorisé doit passer ou non. Pour trouver la bonne condition, j'ai commencé par écrire le test de ce cas limite, ce qui m'a aidé à voir clairement ce qu'il fallait coder (`nouveauSolde < -decouvertAutorise` refuse le retrait, mais l'égalité est acceptée).
- **Faire en sorte que le virement ne bouge rien s'il échoue** : il fallait s'assurer que si le retrait plante, le dépôt ne se fasse jamais. Je l'ai réglé en appelant `retirer()` avant `deposer()` : si `retirer()` plante, la méthode s'arrête tout de suite et `deposer()` n'est jamais lancé.
- **Comparer des nombres à virgule dans les tests** : avec des `double`, on ne peut pas juste comparer deux valeurs directement, il faut ajouter une petite marge d'erreur (`assertEquals(attendu, obtenu, 0.001)`), sinon certains tests plantent à cause des arrondis alors que le résultat est juste.

## 6. Bilan

Ce TP m'a permis de vraiment comprendre comment écrire des tests unitaires bien organisés avec JUnit 5, et surtout de faire la différence entre les trois types de cas à tester : les cas normaux, les cas limites, et les cas où ça doit planter volontairement. J'ai aussi compris pourquoi c'est utile de créer ses propres exceptions plutôt que d'utiliser celles de Java par défaut : ça rend le code et les tests beaucoup plus clairs. Enfin, ça m'a servi à m'entraîner à faire des commits Git réguliers au fur et à mesure du travail, plutôt que de tout pousser d'un coup à la fin.


