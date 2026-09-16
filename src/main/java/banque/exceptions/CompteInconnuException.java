package banque.exceptions;

/**
 * Levée lorsqu'on recherche un compte avec un IBAN qui n'existe pas
 * dans le GestionnaireComptes.
 */
public class CompteInconnuException extends RuntimeException {

    public CompteInconnuException(String message) {
        super(message);
    }
}
