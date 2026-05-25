//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2026.05.10 à 06:34:45 AM WAT 
//


package com.mpaiement.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idPaiement" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="idCommande" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="montant" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="statut" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "idPaiement",
    "idCommande",
    "montant",
    "statut"
})
@XmlRootElement(name = "payerCommandeResponse")
public class PayerCommandeResponse {

    protected int idPaiement;
    protected int idCommande;
    protected double montant;
    @XmlElement(required = true)
    protected String statut;

    /**
     * Obtient la valeur de la propriété idPaiement.
     * 
     */
    public int getIdPaiement() {
        return idPaiement;
    }

    /**
     * Définit la valeur de la propriété idPaiement.
     * 
     */
    public void setIdPaiement(int value) {
        this.idPaiement = value;
    }

    /**
     * Obtient la valeur de la propriété idCommande.
     * 
     */
    public int getIdCommande() {
        return idCommande;
    }

    /**
     * Définit la valeur de la propriété idCommande.
     * 
     */
    public void setIdCommande(int value) {
        this.idCommande = value;
    }

    /**
     * Obtient la valeur de la propriété montant.
     * 
     */
    public double getMontant() {
        return montant;
    }

    /**
     * Définit la valeur de la propriété montant.
     * 
     */
    public void setMontant(double value) {
        this.montant = value;
    }

    /**
     * Obtient la valeur de la propriété statut.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatut() {
        return statut;
    }

    /**
     * Définit la valeur de la propriété statut.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatut(String value) {
        this.statut = value;
    }

}
