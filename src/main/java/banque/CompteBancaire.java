package banque;

import banque.exceptions.MontantInvalideException;
import banque.exceptions.SoldeInsuffisantException;

/**
 * Représente un compte bancaire individuel : son solde, son découvert
 * autorisé, et les opérations de dépôt, retrait et calcul d'intérêts.
 */
public class CompteBancaire {

    private final String iban;
    private final String titulaire;
    private double solde;
    private final double decouvertAutorise;

    public CompteBancaire(String iban, String titulaire, double soldeInitial) {
        this(iban, titulaire, soldeInitial, 0.0);
    }

    public CompteBancaire(String iban, String titulaire, double soldeInitial, double decouvertAutorise) {
        this.iban = iban;
        this.titulaire = titulaire;
        this.solde = soldeInitial;
        this.decouvertAutorise = decouvertAutorise;
    }

    /**
     * Ajoute le montant donné au solde.
     *
     * @throws MontantInvalideException si le montant est négatif ou nul
     */
    public void deposer(double montant) {
        if (montant <= 0) {
            throw new MontantInvalideException(
                    "Le montant du dépôt doit être strictement positif : " + montant);
        }
        this.solde += montant;
    }

    /**
     * Retire le montant donné du solde. Le solde peut descendre jusqu'à
     * -decouvertAutorise, pas en dessous.
     *
     * @throws MontantInvalideException si le montant est négatif ou nul
     * @throws SoldeInsuffisantException si le retrait dépasserait le découvert autorisé
     */
    public void retirer(double montant) {
        if (montant <= 0) {
            throw new MontantInvalideException(
                    "Le montant du retrait doit être strictement positif : " + montant);
        }
        double nouveauSolde = this.solde - montant;
        if (nouveauSolde < -decouvertAutorise) {
            throw new SoldeInsuffisantException(
                    "Retrait refusé : solde actuel = " + solde
                            + ", découvert autorisé = " + decouvertAutorise
                            + ", montant demandé = " + montant);
        }
        this.solde = nouveauSolde;
    }

    /**
     * Calcule les intérêts (solde x taux) sans modifier le solde, si celui-ci
     * est positif. Retourne 0 si le solde n'est pas positif.
     *
     * @throws MontantInvalideException si le taux est négatif
     */
    public double calculerInterets(double taux) {
        if (taux < 0) {
            throw new MontantInvalideException("Le taux d'intérêt ne peut pas être négatif : " + taux);
        }
        if (solde <= 0) {
            return 0.0;
        }
        return solde * taux;
    }

    public boolean estEnDecouvert() {
        return solde < 0;
    }

    public double getSolde() {
        return solde;
    }

    public String getTitulaire() {
        return titulaire;
    }

    public String getIban() {
        return iban;
    }

    public double getDecouvertAutorise() {
        return decouvertAutorise;
    }
}
