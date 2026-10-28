package fr.efrei.transfret;

import java.util.Date;

import static fr.efrei.transport.contrat.Statut.ANNULEE;
import static fr.efrei.transport.contrat.TypeClient.ENTREPRISE;

public class GenerateurFacture {

    public static String genererFacture(Expedition expedition) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================\n");
        sb.append("              FACTURE TRANSFRET\n");
        sb.append("==============================================\n");
        sb.append("Facture n° F-" + expedition.getId() + "\n");
        sb.append("Date : " + Utils.formaterDate(new Date()) + "\n");
        sb.append("Client : " + expedition.getClient().getNom() + "\n");
        if (expedition.getClient().getType().equals(ENTREPRISE)) {
            sb.append("SIRET : " + expedition.getClient().getSiret() + "\n");
        }
        sb.append("Adresse : " + expedition.getClient().getAdresse() + "\n");
        sb.append("----------------------------------------------\n");
        sb.append("Trajet : " + expedition.getVilleDepart() + " -> " + expedition.getVilleArrivee() + " (" + expedition.getDistance() + " km)\n");
        sb.append("Mode : " + expedition.getMode() + (expedition.isUrgent() ? " (URGENT)" : "") + "\n");
        sb.append("Colis :\n");
        for (Colis c : expedition.getColis()) {
            sb.append("  - " + c.getDescription() + " : " + c.getPoids() + " kg [" + c.getLibelleType() + "]\n");
        }
        sb.append("----------------------------------------------\n");
        if (expedition.getStatut().equals(ANNULEE)) {
            sb.append("EXPEDITION ANNULEE\n");
            sb.append("Frais d'annulation TTC : " + String.format("%.2f", expedition.getFraisAnnulation()) + " €\n");
        } else {
            sb.append("Total HT  : " + String.format("%.2f", expedition.getPrixHT()) + " €\n");
            sb.append("TVA 20%   : " + String.format("%.2f", expedition.getPrixTTC() - expedition.getPrixHT()) + " €\n");
            sb.append("Total TTC : " + String.format("%.2f", expedition.getPrixTTC()) + " €\n");
        }
        sb.append("==============================================\n");
        return sb.toString();
    }
}
