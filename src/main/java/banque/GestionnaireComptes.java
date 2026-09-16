package banque;

import banque.exceptions.CompteDejaExistantException;
import banque.exceptions.CompteInconnuException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gère un ensemble de comptes bancaires : ajout, recherche par IBAN,
 * virements entre comptes, et calculs agrégés (solde total, comptes en
 * découvert).
 */
public class GestionnaireComptes {

    private final Map<String, CompteBancaire> comptes = new LinkedHashMap<>();

    /**
     * Ajoute un compte au gestionnaire.
     *
     * @throws CompteDejaExistantException si un compte avec le même IBAN existe déjà
     */
    public void ajouterCompte(CompteBancaire compte) {
        if (comptes.containsKey(compte.getIban())) {
            throw new CompteDejaExistantException(
                    "Un compte existe déjà pour l'IBAN : " + compte.getIban());
        }
        comptes.put(compte.getIban(), compte);
    }

    /**
     * Recherche un compte par son IBAN.
     *
     * @throws CompteInconnuException si aucun compte ne correspond à cet IBAN
     */
    public CompteBancaire rechercherCompte(String iban) {
        CompteBancaire compte = comptes.get(iban);
        if (compte == null) {
            throw new CompteInconnuException("Aucun compte trouvé pour l'IBAN : " + iban);
        }
        return compte;
    }

    /**
     * Effectue un virement entre deux comptes gérés. Le retrait est tenté en
     * premier sur le compte source : s'il échoue (montant invalide ou solde
     * insuffisant), une exception est propagée et aucun dépôt n'a lieu sur le
     * compte destination, ce qui garantit l'atomicité de l'opération.
     */
    public void virement(String ibanSource, String ibanDestination, double montant) {
        CompteBancaire source = rechercherCompte(ibanSource);
        CompteBancaire destination = rechercherCompte(ibanDestination);

        // Si retirer() lève une exception, elle remonte immédiatement et
        // deposer() n'est jamais appelé : aucun solde n'a bougé.
        source.retirer(montant);
        destination.deposer(montant);
    }

    /**
     * Retourne la somme des soldes de tous les comptes gérés.
     */
    public double soldeTotal() {
        double total = 0.0;
        for (CompteBancaire compte : comptes.values()) {
            total += compte.getSolde();
        }
        return total;
    }

    /**
     * Retourne la liste des comptes dont le solde est négatif.
     */
    public List<CompteBancaire> listeComptesEnDecouvert() {
        List<CompteBancaire> resultat = new ArrayList<>();
        for (CompteBancaire compte : comptes.values()) {
            if (compte.estEnDecouvert()) {
                resultat.add(compte);
            }
        }
        return resultat;
    }
}
