package com.clientui.microserviceclientui.controller;

import com.clientui.microserviceclientui.beans.CommandeBean;
import com.clientui.microserviceclientui.beans.PaiementBean;
import com.clientui.microserviceclientui.beans.ProductBean;
import com.clientui.microserviceclientui.proxies.MicroserviceCommandeProxy;
import com.clientui.microserviceclientui.proxies.MicroservicePaiementProxy;
import com.clientui.microserviceclientui.proxies.MicroserviceProduitsProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Date;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final MicroserviceProduitsProxy ProduitsProxy;

    private final MicroserviceCommandeProxy CommandesProxy;

    private final MicroservicePaiementProxy paiementProxy;

    public ClientController(MicroserviceProduitsProxy produitsProxy,
                            MicroserviceCommandeProxy commandesProxy,
                            MicroservicePaiementProxy paiementProxy) {
        this.ProduitsProxy = produitsProxy;
        this.CommandesProxy = commandesProxy;
        this.paiementProxy = paiementProxy;
    }


    /*
    * Étape (1)
    * Opération qui récupère la liste des produits et on les affichent dans la page d'accueil.
    * Les produits sont récupérés grâce à ProduitsProxy
    * On fini par rentourner la page Accueil.html à laquelle on passe la liste d'objets "produits" récupérés.
    * */
    @RequestMapping("/")
    public String accueil(Model model){

        List<ProductBean> produits =  ProduitsProxy.listeDesProduits();

        model.addAttribute("produits", produits);

        return "Accueil";
    }

    /*
    * Étape (2)
    * Opération qui récupère les détails d'un produit
    * On passe l'objet "produit" récupéré et qui contient les détails en question à  FicheProduit.html
    * */
    @RequestMapping("/details-produit/{id}")
    public String ficheProduit(@PathVariable int id,  Model model){

        ProductBean produit = ProduitsProxy.recupererUnProduit(id);

        model.addAttribute("produit", produit);

        return "FicheProduit";
    }

    /*
    * Étape (3) et (4)
    * Opération qui fait appel au microservice de commande pour placer une commande et récupérer les détails de la commande créée
    * */
    @RequestMapping(value = "/commander-produit/{idProduit}/{montant}")
    public String passerCommande(@PathVariable int idProduit, @PathVariable Double montant,  Model model){

        CommandeBean commande = new CommandeBean();
        commande.setProductId(idProduit);
        commande.setQuantite(1);
        commande.setDateCommande(new Date());

        CommandeBean commandeAjoutee = CommandesProxy.ajouterCommande(commande);
        model.addAttribute("commande", commandeAjoutee);
        model.addAttribute("montant", montant);

        return "Paiement";
    }

    @PostMapping(value = "/commander-produit")
    public String passerCommandePost(@RequestParam int idProduit,
                                     @RequestParam Double montant,
                                     @RequestParam(defaultValue = "1") int quantite,
                                     Model model) {

        try {
            logger.info("Commande reçue: idProduit={}, montant={}, quantite={}", idProduit, montant, quantite);

            CommandeBean commande = new CommandeBean();
            commande.setProductId(idProduit);
            commande.setQuantite(quantite);
            commande.setDateCommande(new Date());

            Double montantTotal = montant * quantite;

            logger.info("Appel du proxy commandes pour ajouter la commande");
            CommandeBean commandeAjoutee = CommandesProxy.ajouterCommande(commande);

            model.addAttribute("commande", commandeAjoutee);
            model.addAttribute("montant", montantTotal);

            logger.info("Commande ajoutée avec succès, id={}", commandeAjoutee.getId());

            return "Paiement";
        } catch (Exception e) {
            logger.error("Erreur lors de la commande: {}", e.getMessage(), e);
            model.addAttribute("statusCode", 500);
            model.addAttribute("errorMessage", "Une erreur interne est survenue lors de la commande. Veuillez réessayer.");
            return "error";
        }
    }

    @RequestMapping(value = "/payer-commande/{idCommande}/{montantCommande}")
    public String payerCommandeGet(@PathVariable int idCommande, @PathVariable Double montantCommande, Model model){

        PaiementBean paiementAExcecuter = new PaiementBean();
        paiementAExcecuter.setIdCommande(idCommande);
        paiementAExcecuter.setMontant(montantCommande);
        paiementAExcecuter.setNumeroCarte(numcarte());

        ResponseEntity<PaiementBean> paiement = paiementProxy.payerUneCommande(paiementAExcecuter);

        boolean paiementAccepte = false;
        if(paiement.getStatusCode() == HttpStatus.CREATED)
            paiementAccepte = true;

        model.addAttribute("paiementOk", paiementAccepte);
        model.addAttribute("idCommande", idCommande);
        model.addAttribute("montantCommande", montantCommande);

        return "Confirmation";
    }

    @PostMapping(value = "/payer-commande")
    public String payerCommandePost(@RequestParam int idCommande,
                                    @RequestParam Double montantCommande,
                                    @RequestParam(required = false) String numeroCarte,
                                    Model model){

        model.addAttribute("idCommande", idCommande);
        model.addAttribute("montantCommande", montantCommande);

        try {
            logger.info("Paiement reçu: idCommande={}, montant={}, numeroCarte={}", idCommande, montantCommande, numeroCarte != null ? "****" : "null");

            PaiementBean paiementAExcecuter = new PaiementBean();
            paiementAExcecuter.setIdCommande(idCommande);
            paiementAExcecuter.setMontant(montantCommande);

            if (numeroCarte != null && !numeroCarte.isBlank()) {
                try {
                    paiementAExcecuter.setNumeroCarte(Long.parseLong(numeroCarte.replaceAll("\\D", "")));
                } catch (NumberFormatException e) {
                    paiementAExcecuter.setNumeroCarte(numcarte());
                }
            } else {
                paiementAExcecuter.setNumeroCarte(numcarte());
            }

            logger.info("Appel du proxy paiement");
            ResponseEntity<PaiementBean> paiement = paiementProxy.payerUneCommande(paiementAExcecuter);

            boolean paiementAccepte = false;
            if(paiement.getStatusCode() == HttpStatus.CREATED)
                paiementAccepte = true;

            model.addAttribute("paiementOk", paiementAccepte);

            logger.info("Paiement traité: accepté={}", paiementAccepte);

            return "Confirmation";
        } catch (Exception e) {
            logger.error("Erreur lors du paiement: {}", e.getMessage(), e);
            model.addAttribute("paiementOk", false);
            model.addAttribute("errorMessage", "Une erreur est survenue: " + e.getMessage());
            return "Confirmation";
        }
    }

    //Génére une serie de 16 chiffres au hasard pour simuler vaguement une CB
    private Long numcarte() {

        return ThreadLocalRandom.current().nextLong(1000000000000000L,9000000000000000L );
    }
}
