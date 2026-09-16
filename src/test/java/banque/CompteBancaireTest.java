package banque;

import banque.exceptions.MontantInvalideException;
import banque.exceptions.SoldeInsuffisantException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompteBancaireTest {

    private static final double DELTA = 0.001;

    private CompteBancaire compte;

    @BeforeEach
    void setUp() {
        // solde initial 100, découvert autorisé 50
        compte = new CompteBancaire("FR7612345987650123456789014", "Jean Dupont", 100.0, 50.0);
    }

    @Nested
    @DisplayName("Cas nominaux")
    class CasNominaux {

        @Test
        @DisplayName("Un dépôt valide augmente le solde")
        void deposerAugmenteLeSolde() {
            compte.deposer(50.0);
            assertEquals(150.0, compte.getSolde(), DELTA);
        }

        @Test
        @DisplayName("Un retrait valide diminue le solde")
        void retirerDiminueLeSolde() {
            compte.retirer(30.0);
            assertEquals(70.0, compte.getSolde(), DELTA);
        }

        @Test
        @DisplayName("Le calcul des intérêts est correct sur un solde positif et ne modifie pas le solde")
        void calculerInteretsSurSoldePositif() {
            double interets = compte.calculerInterets(0.05);

            assertEquals(5.0, interets, DELTA);
            assertEquals(100.0, compte.getSolde(), DELTA);
        }
    }

    @Nested
    @DisplayName("Cas limites")
    class CasLimites {

        @Test
        @DisplayName("Un retrait qui amène exactement au découvert autorisé est accepté")
        void retraitJusquauDecouvertExactPasse() {
            compte.retirer(150.0); // 100 - 150 = -50 = -decouvertAutorise

            assertEquals(-50.0, compte.getSolde(), DELTA);
        }

        @Test
        @DisplayName("Un retrait d'un centime de plus que le découvert autorisé lève une exception")
        void retraitUnCentimeDePlusQueDecouvertEchoue() {
            assertThrows(SoldeInsuffisantException.class, () -> compte.retirer(150.01));
        }

        @Test
        @DisplayName("Un dépôt de 0 est rejeté")
        void depotDeZeroEstRejete() {
            assertThrows(MontantInvalideException.class, () -> compte.deposer(0));
        }

        @Test
        @DisplayName("Un retrait de 0 est rejeté")
        void retraitDeZeroEstRejete() {
            assertThrows(MontantInvalideException.class, () -> compte.retirer(0));
        }
    }

    @Nested
    @DisplayName("Cas d'erreur (exceptions)")
    class CasErreur {

        @Test
        @DisplayName("Un dépôt négatif lève MontantInvalideException")
        void depotNegatifLeveException() {
            assertThrows(MontantInvalideException.class, () -> compte.deposer(-10.0));
        }

        @Test
        @DisplayName("Un retrait négatif lève MontantInvalideException")
        void retraitNegatifLeveException() {
            assertThrows(MontantInvalideException.class, () -> compte.retirer(-10.0));
        }

        @Test
        @DisplayName("Un retrait dépassant largement le découvert lève SoldeInsuffisantException")
        void retraitDepassantDecouvertLeveException() {
            assertThrows(SoldeInsuffisantException.class, () -> compte.retirer(1000.0));
        }

        @Test
        @DisplayName("Un taux d'intérêt négatif est rejeté")
        void tauxNegatifEstRejete() {
            assertThrows(MontantInvalideException.class, () -> compte.calculerInterets(-0.01));
        }

        @Test
        @DisplayName("estEnDecouvert retourne true quand le solde est négatif")
        void estEnDecouvertRetourneTrueSiSoldeNegatif() {
            compte.retirer(120.0);

            assertTrue(compte.estEnDecouvert());
        }

        @Test
        @DisplayName("estEnDecouvert retourne false quand le solde est positif")
        void estEnDecouvertRetourneFalseSiSoldePositif() {
            assertFalse(compte.estEnDecouvert());
        }
    }
}
