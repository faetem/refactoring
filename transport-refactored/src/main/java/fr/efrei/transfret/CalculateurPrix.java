package fr.efrei.transfret;

import fr.efrei.transfret.Colis;
import fr.efrei.transport.contrat.ModeTransport;
import fr.efrei.transport.contrat.TypeClient;

import static fr.efrei.transfret.Utils.arrondir;
import static fr.efrei.transport.contrat.ModeTransport.*;
import static fr.efrei.transport.contrat.TypeClient.*;
import static fr.efrei.transport.contrat.TypeMarchandise.*;

import java.util.List;

public class CalculateurPrix {

    public static final double ROUTE_TARIF_KM = 1.2;
    public static final double ROUTE_TARIF_KG = 0.10;
    public static final double RAIL_TARIF_KM = 0.8;
    public static final double RAIL_TARIF_KG = 0.07;
    public static final int RAIL_TARIF_MIN = 150;
    public static final int MER_TARIF_MIN = 500;
    public static final double MER_TARIF_KM = 0.5;
    public static final double MER_TARIF_KG = 0.03;
    public static final int AIR_FACTEUR_VOLUMETRIQUE = 167;
    public static final double AIR_TARIF_KM = 0.6;
    public static final double AIR_TARIF_KG = 4.5;
    public static final double SUPPLEMENT_FRAGILE = 1.15;
    public static final double SUPPLEMENT_DANGER = 1.30;
    public static final double SUPPLEMENT_REFRIGERE = 1.2;
    public static final double SUPPLEMENT_URGENT = 1.5;
    public static final double REMISE_ENTREPRISE_BASIC = 0.90;
    public static final double REMISE_ENTREPRISE_PLUS = 0.85;
    public static final double TAUX_ASSURANCE = 0.02;
    public static final int PRIX_MIN_ASSURANCE = 15;

    public double calculerPrixHT(List<Colis> colis, int distance, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
        double poids = 0;
        double volume = 0;
        double valeur = 0;
        boolean fragile = false;
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            poids = poids + c.getPoids();
            volume = volume + c.getVolume();
            valeur = valeur + c.getValeurDeclaree();
            if (c.getType().equals(FRAGILE)) fragile = true;
            if (c.getType().equals(DANGEREUX)) dangereux = true;
            if (c.getType().equals(REFRIGERE)) refrigere = true;
        }

        // tarif de base selon le mode
        double prix;
        if (mode.equals(ROUTE)) {
            prix = distance * ROUTE_TARIF_KM + poids * ROUTE_TARIF_KG;
        } else if (mode.equals(RAIL)) {
            prix = distance * RAIL_TARIF_KM + poids * RAIL_TARIF_KG;
            if (prix < RAIL_TARIF_MIN) prix = RAIL_TARIF_MIN;
        } else if (mode.equals(MER)) {
            prix = distance * MER_TARIF_KM + poids * MER_TARIF_KG;
            if (prix < MER_TARIF_MIN) prix = MER_TARIF_MIN;
        } else if (mode.equals(AIR)) {
            double poidsTaxable = poids;
            if (volume * AIR_FACTEUR_VOLUMETRIQUE > poids) {
                poidsTaxable = volume * AIR_FACTEUR_VOLUMETRIQUE;
            }
            prix = distance * AIR_TARIF_KM + poidsTaxable * AIR_TARIF_KG;
        } else {
            throw new IllegalArgumentException("Mode inconnu : " + mode);
        }

        // suppléments
        if (fragile) prix = prix * SUPPLEMENT_FRAGILE;
        if (dangereux) prix = prix * SUPPLEMENT_DANGER;
        if (refrigere) prix = prix * SUPPLEMENT_REFRIGERE;
        if (urgent) prix = prix * SUPPLEMENT_URGENT;

        // remise client
        if (typeClient.equals(ENTREPRISE)) {
            if (prix > 5000) {
                prix = prix * REMISE_ENTREPRISE_PLUS;
            } else {
                prix = prix * REMISE_ENTREPRISE_BASIC;
            }
        } else if (typeClient.equals(PREMIUM)) {
            prix = prix * RAIL_TARIF_KM;
        }

        // assurance
        if (assurance) {
            double montantAssurance = valeur * TAUX_ASSURANCE;
            if (montantAssurance < PRIX_MIN_ASSURANCE) montantAssurance = PRIX_MIN_ASSURANCE;
            prix = prix + montantAssurance;
        }
        return arrondir(prix);
    }
}
