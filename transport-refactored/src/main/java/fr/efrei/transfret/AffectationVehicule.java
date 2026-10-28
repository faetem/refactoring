package fr.efrei.transfret;

import fr.efrei.transfret.Bateau;
import fr.efrei.transfret.CamionFrigorifique;
import fr.efrei.transfret.Colis;
import fr.efrei.transfret.Train;
import fr.efrei.transport.contrat.ModeTransport;
import static fr.efrei.transport.contrat.ModeTransport.*;
import static fr.efrei.transport.contrat.TypeMarchandise.*;


import java.util.List;


public class AffectationVehicule {

    // retourne le premier véhicule disponible et compatible, null si aucun
    public Vehicule trouverVehicule(List<Colis> colis, ModeTransport mode) {
        double poids = 0;
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            poids = poids + c.getPoids();
            if (c.getType().equals(DANGEREUX)) dangereux = true;
            if (c.getType().equals(REFRIGERE)) refrigere = true;
        }
        for (Vehicule v : Flotte.vehicules) {
            if (!v.estDisponible()) continue;
            if (v.getCapaciteMax() - v.getChargeActuelle() < poids) continue;
            if (mode.equals(ROUTE)) {
                if (v instanceof Camion) {
                    if (refrigere) {
                        if (v instanceof CamionFrigorifique) {
                            return v;
                        }
                    } else if (v instanceof CamionFrigorifique) {
                        // ne pas prendre le frigo pour autre chose, sinon ça plante au chargement
                        continue;
                    } else if (dangereux) {
                        if (v.peutTransporterMatieresDangereuses()) {
                            return v;
                        }
                    } else {
                        return v;
                    }
                }
            } else if (mode.equals(RAIL)) {
                if (v instanceof Train) {
                    return v;
                }
            } else if (mode.equals(MER)) {
                if (v instanceof Bateau) {
                    if (refrigere && !((Bateau) v).aConteneursRefrigeres()) {
                        continue;
                    }
                    return v;
                }
            } else if (mode.equals(AIR)) {
                if (v instanceof Avion) {
                    return v;
                }
            }
        }
        return null;
    }
}
