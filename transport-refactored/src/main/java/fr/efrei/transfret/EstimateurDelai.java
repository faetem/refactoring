package fr.efrei.transfret;

import fr.efrei.transport.contrat.ModeTransport;

public class EstimateurDelai {
    // retourne le délai de livraison en jours
    public static int calculerDelai(int distance, ModeTransport mode, boolean urgent) {
        int jours = 0;
        switch (mode) {
            case ROUTE:
                double heures = distance / 70.0;
                if (urgent) {
                    jours = (int) Math.ceil(heures / 18);
                } else {
                    jours = (int) Math.ceil(heures / 9);
                }
                break;
            case RAIL:
                jours = (int) Math.ceil(distance / 1440.0) + 1;
                if (urgent) jours = jours - 1;
                break;
            case MER:
                jours = (int) Math.ceil(distance / 720.0) + 2;
                break;
            case AIR:
                jours = 1;
                if (distance > 3000) jours = 2;
                if (urgent) jours = 1;
                break;
        }
        if (jours < 1) jours = 1;
        return jours;
    }
}
