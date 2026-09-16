package banque.exceptions;

/**
 * Levée lorsqu'un retrait ferait descendre le solde d'un compte
 * en dessous de son découvert autorisé.
 */
public class SoldeInsuffisantException extends RuntimeException {

    public SoldeInsuffisantException(String message) {
        super(message);
    }
}
