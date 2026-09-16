package banque;

import banque.exceptions.CompteDejaExistantException;
import banque.exceptions.CompteInconnuException;
import banque.exceptions.SoldeInsuffisantException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GestionnaireComptesTest {

    private static final double DELTA = 0.001;

    private GestionnaireComptes gestionnaire;
    private CompteBancaire compteA;
    private CompteBancaire compteB;

    @BeforeEach
    void setUp() {
        gestionnaire = new GestionnaireComptes();
        compteA = new CompteBancaire("FR001", "Alice", 200.0, 0.0);
        compteB = new CompteBancaire("FR002", "Bob", 50.0, 0.0);
        gestionnaire.ajouterCompte(compteA);
        gestionnaire.ajouterCompte(compteB);
    }

    @Nested
    @DisplayName("Cas nominaux")
    class CasNominaux {

        @Test
        @DisplayName("Un virement réussi met à jour les deux comptes")
        void virementReussiEntreDeuxComptes() {
            gestionnaire.virement("FR001", "FR002", 100.0);

            assertEquals(100.0, compteA.getSolde(), DELTA);
            assertEquals(150.0, compteB.getSolde(), DELTA);
        }

        @Test
        @DisplayName("Le solde total est la somme des soldes de tous les comptes")
        void soldeTotalCorrect() {
            assertEquals(250.0, gestionnaire.soldeTotal(), DELTA);
        }

        @Test
        @DisplayName("Une recherche par IBAN existant retourne le bon compte")
        void rechercherCompteExistant() {
            CompteBancaire trouve = gestionnaire.rechercherCompte("FR001");

            assertEquals(compteA, trouve);
        }
    }

    @Nested
    @DisplayName("Cas d'erreur (exceptions)")
    class CasErreur {

        @Test
        @DisplayName("Rechercher un IBAN inconnu lève CompteInconnuException")
        void rechercherIbanInconnuLeveException() {
            assertThrows(CompteInconnuException.class, () -> gestionnaire.rechercherCompte("IBAN_INCONNU"));
        }

        @Test
        @DisplayName("Ajouter un IBAN déjà existant lève CompteDejaExistantException")
        void ajouterIbanExistantLeveException() {
            CompteBancaire doublon = new CompteBancaire("FR001", "Alice Bis", 0.0, 0.0);

            assertThrows(CompteDejaExistantException.class, () -> gestionnaire.ajouterCompte(doublon));
        }

        @Test
        @DisplayName("Un virement qui échoue au milieu (solde insuffisant) ne modifie aucun solde")
        void virementEchoueNeModifieAucunSolde() {
            double soldeAAvant = compteA.getSolde();
            double soldeBAvant = compteB.getSolde();

            assertThrows(SoldeInsuffisantException.class,
                    () -> gestionnaire.virement("FR001", "FR002", 10_000.0));

            assertEquals(soldeAAvant, compteA.getSolde(), DELTA);
            assertEquals(soldeBAvant, compteB.getSolde(), DELTA);
        }

        @Test
        @DisplayName("Un virement depuis un IBAN source inconnu lève CompteInconnuException")
        void virementAvecSourceInconnueLeveException() {
            assertThrows(CompteInconnuException.class,
                    () -> gestionnaire.virement("IBAN_INCONNU", "FR002", 10.0));
        }
    }

    @Nested
    @DisplayName("Autres méthodes")
    class Autres {

        @Test
        @DisplayName("listeComptesEnDecouvert retourne uniquement les comptes à solde négatif")
        void listeComptesEnDecouvertCorrecte() {
            CompteBancaire compteC = new CompteBancaire("FR003", "Carla", -20.0, 100.0);
            gestionnaire.ajouterCompte(compteC);

            List<CompteBancaire> enDecouvert = gestionnaire.listeComptesEnDecouvert();

            assertEquals(1, enDecouvert.size());
            assertEquals("FR003", enDecouvert.get(0).getIban());
        }
    }
}
