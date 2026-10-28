package fr.efrei.transfret;

import fr.efrei.transport.contrat.ModeTransport;

import java.util.List;

import static fr.efrei.transport.contrat.ModeTransport.*;
import static fr.efrei.transport.contrat.ModeTransport.MER;
import static fr.efrei.transport.contrat.TypeMarchandise.DANGEREUX;
import static fr.efrei.transport.contrat.TypeMarchandise.REFRIGERE;

public class ValidateurExpedition {

    boolean client;
    List<Colis> colis;
    ModeTransport mode;
    private static AffectationVehicule affectation = new AffectationVehicule();


    public static int calculDistance(Expedition e) {
        int distance = Utils.getDistance(e.getVilleDepart(), e.getVilleArrivee());
        return distance;
    }

    public static Vehicule affecterVehicule(Expedition e) {
        Vehicule v = affectation.trouverVehicule(e.getColis(), e.getMode());
        return v;
    }

    public static Expedition validerExpedition(Expedition e) {

        if (e.getClient() == null) {
            System.out.println("ERREUR : client obligatoire");
            return null;
        }
        if (e.getColis() == null || e.getColis().isEmpty()) {
            System.out.println("ERREUR : au moins un colis est nécessaire");
            return null;
        }
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : e.getColis()) {
            if (c.getPoids() <= 0) {
                System.out.println("ERREUR : poids invalide pour " + c.getDescription());
                return null;
            }
            if (c.getType().equals(DANGEREUX)) dangereux = true;
            if (c.getType().equals(REFRIGERE)) refrigere = true;
        }
        if (dangereux && e.getMode().equals(AIR)) {
            System.out.println("ERREUR : les matières dangereuses sont interdites en transport aérien");
            return null;
        }
        if (refrigere && !(e.getMode().equals(ROUTE) || e.getMode().equals(MER))) {
            System.out.println("ERREUR : le transport réfrigéré n'est possible que par route ou par mer");
            return null;
        }
        if (e.isUrgent() && e.getMode().equals(MER)) {
            System.out.println("ERREUR : pas d'option urgente pour le transport maritime");
            return null;
        }

        if (calculDistance(e) == -1) {
            System.out.println("ERREUR : trajet inconnu " + e.getVilleDepart() + " -> " + e.getVilleArrivee());
            return null;
        }

        if (affecterVehicule(e) == null) {
            System.out.println("ERREUR : aucun véhicule disponible pour cette expédition");
            return null;
        }
        for (Colis c : e.getColis()) {
            affecterVehicule(e).charger(c);
        }

        return e;
    }

}
