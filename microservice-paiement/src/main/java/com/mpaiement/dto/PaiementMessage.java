package com.mpaiement.dto;

import java.io.Serializable;

/**
 * DTO = Data Transfer Object
 * C'est l'objet qui voyage dans RabbitMQ entre microservices.
 * Serializable = peut être converti en bytes pour voyager sur le réseau.
 */
public class PaiementMessage implements Serializable {

    private int idCommande;
    private double montant;
    private String statut;

    // Constructeur vide OBLIGATOIRE pour que Jackson puisse
    // désérialiser le JSON en objet Java côté notification
    public PaiementMessage() {}

    public PaiementMessage(int idCommande, double montant, String statut) {
        this.idCommande = idCommande;
        this.montant = montant;
        this.statut = statut;
    }

    public int getIdCommande() { return idCommande; }
    public void setIdCommande(int idCommande) { this.idCommande = idCommande; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "PaiementMessage{idCommande=" + idCommande +
               ", montant=" + montant + ", statut=" + statut + "}";
    }
}
