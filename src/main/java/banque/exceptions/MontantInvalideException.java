package banque.exceptions;

/**
 * Levée lorsqu'un montant (dépôt, retrait) ou un taux (calcul d'intérêts)
 * est négatif ou nul alors qu'une valeur strictement positive est requise.
 */
public class MontantInvalideException extends RuntimeException {

    public MontantInvalideException(String message) {
        super(message);
    }
}
