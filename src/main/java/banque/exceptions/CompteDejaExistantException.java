package banque.exceptions;

/**
 * Levée lorsqu'on tente d'ajouter au GestionnaireComptes un compte
 * dont l'IBAN est déjà utilisé par un autre compte.
 */
public class CompteDejaExistantException extends RuntimeException {

    public CompteDejaExistantException(String message) {
        super(message);
    }
}
