package com.clientui.microserviceclientui.controller;

import com.clientui.microserviceclientui.beans.CommandeBean;
import com.clientui.microserviceclientui.beans.PaiementBean;
import com.clientui.microserviceclientui.beans.ProductBean;
import com.clientui.microserviceclientui.proxies.MicroserviceCommandeProxy;
import com.clientui.microserviceclientui.proxies.MicroservicePaiementProxy;
import com.clientui.microserviceclientui.proxies.MicroserviceProduitsProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class ClientController {

    private static final Logger logger =
            LoggerFactory.getLogger(ClientController.class);

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

    // ============================================================
    // AUTHENTIFICATION
    // ============================================================

    // ----------------------------------------------------------------
    // GET /login — affiche la page de connexion
    // ----------------------------------------------------------------
    @GetMapping("/login")
    public String loginPage() {
        return "Login";
    }

    // ----------------------------------------------------------------
    // POST /login — traite le formulaire de connexion
    // ----------------------------------------------------------------
    @PostMapping("/login")
    public String loginPost(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        try {

            RestTemplate restTemplate = new RestTemplate();

            String url = "http://localhost:8085/auth/login";

            // Body JSON
            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity =
                    new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, String> response =
                    restTemplate.postForObject(
                            url,
                            entity,
                            Map.class
                    );

            // Stockage en session
            session.setAttribute(
                    "accessToken",
                    response.get("accessToken")
            );

            session.setAttribute("username", username);

            session.setAttribute(
                    "role",
                    response.get("role")
            );

            logger.info("Connexion réussie pour : {}", username);

            return "redirect:/";

        } catch (Exception e) {

            logger.error(
                    "Erreur de connexion : {}",
                    e.getMessage()
            );

            model.addAttribute(
                    "error",
                    "Identifiants incorrects. Vérifiez votre username et mot de passe."
            );

            return "Login";
        }
    }

    // ----------------------------------------------------------------
    // GET /register — affiche la page d'inscription
    // ----------------------------------------------------------------
    @GetMapping("/register")
    public String registerPage() {
        return "Register";
    }

    // ----------------------------------------------------------------
    // POST /register — traite le formulaire d'inscription
    // ----------------------------------------------------------------
    @PostMapping("/register")
    public String registerPost(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {

        try {

            RestTemplate restTemplate = new RestTemplate();

            String url = "http://localhost:8085/auth/register";

            Map<String, String> body = new HashMap<>();

            body.put("username", username);
            body.put("email", email);
            body.put("password", password);
            body.put("role", "ROLE_USER");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity =
                    new HttpEntity<>(body, headers);

            restTemplate.postForObject(
                    url,
                    entity,
                    String.class
            );

            model.addAttribute(
                    "success",
                    "Compte créé avec succès ! Connectez-vous maintenant."
            );

            return "Login";

        } catch (Exception e) {

            logger.error(
                    "Erreur d'inscription : {}",
                    e.getMessage()
            );

            model.addAttribute(
                    "error",
                    "Erreur lors de l'inscription. Ce username ou email existe peut-être déjà."
            );

            return "Register";
        }
    }

    // ----------------------------------------------------------------
    // GET /logout — déconnexion
    // ----------------------------------------------------------------
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        logger.info("Déconnexion effectuée");

        return "redirect:/login";
    }

    // ============================================================
    // PRODUITS
    // ============================================================

    /*
     * Étape (1)
     * Récupération de la liste des produits
     */
    @RequestMapping("/")
    public String accueil(Model model) {

        List<ProductBean> produits =
                ProduitsProxy.listeDesProduits();

        model.addAttribute("produits", produits);

        return "Accueil";
    }

    /*
     * Étape (2)
     * Détails d'un produit
     */
    @RequestMapping("/details-produit/{id}")
    public String ficheProduit(@PathVariable int id,
                               Model model) {

        ProductBean produit =
                ProduitsProxy.recupererUnProduit(id);

        model.addAttribute("produit", produit);

        return "FicheProduit";
    }

    // ============================================================
    // COMMANDES
    // ============================================================

    /*
     * Ancienne version GET
     */
    @RequestMapping(value = "/commander-produit/{idProduit}/{montant}")
    public String passerCommande(@PathVariable int idProduit,
                                 @PathVariable Double montant,
                                 Model model) {

        CommandeBean commande = new CommandeBean();

        commande.setProductId(idProduit);
        commande.setQuantite(1);
        commande.setDateCommande(new Date());

        CommandeBean commandeAjoutee =
                CommandesProxy.ajouterCommande(commande);

        model.addAttribute("commande", commandeAjoutee);
        model.addAttribute("montant", montant);

        return "Paiement";
    }

    /*
     * Nouvelle version POST
     */
    @PostMapping(value = "/commander-produit")
    public String passerCommandePost(@RequestParam int idProduit,
                                     @RequestParam Double montant,
                                     @RequestParam(defaultValue = "1") int quantite,
                                     Model model) {

        try {

            logger.info(
                    "Commande reçue: idProduit={}, montant={}, quantite={}",
                    idProduit,
                    montant,
                    quantite
            );

            CommandeBean commande = new CommandeBean();

            commande.setProductId(idProduit);
            commande.setQuantite(quantite);
            commande.setDateCommande(new Date());

            Double montantTotal = montant * quantite;

            logger.info(
                    "Appel du proxy commandes pour ajouter la commande"
            );

            CommandeBean commandeAjoutee =
                    CommandesProxy.ajouterCommande(commande);

            model.addAttribute("commande", commandeAjoutee);
            model.addAttribute("montant", montantTotal);

            logger.info(
                    "Commande ajoutée avec succès, id={}",
                    commandeAjoutee.getId()
            );

            return "Paiement";

        } catch (Exception e) {

            logger.error(
                    "Erreur lors de la commande: {}",
                    e.getMessage(),
                    e
            );

            model.addAttribute("statusCode", 500);

            model.addAttribute(
                    "errorMessage",
                    "Une erreur interne est survenue lors de la commande. Veuillez réessayer."
            );

            return "error";
        }
    }

    // ============================================================
    // PAIEMENT
    // ============================================================

    @RequestMapping(value = "/payer-commande/{idCommande}/{montantCommande}")
    public String payerCommandeGet(@PathVariable int idCommande,
                                   @PathVariable Double montantCommande,
                                   Model model) {

        PaiementBean paiementAExcecuter =
                new PaiementBean();

        paiementAExcecuter.setIdCommande(idCommande);
        paiementAExcecuter.setMontant(montantCommande);
        paiementAExcecuter.setNumeroCarte(numcarte());

        ResponseEntity<PaiementBean> paiement =
                paiementProxy.payerUneCommande(
                        paiementAExcecuter
                );

        boolean paiementAccepte = false;

        if (paiement.getStatusCode() == HttpStatus.CREATED) {
            paiementAccepte = true;
        }

        model.addAttribute("paiementOk", paiementAccepte);
        model.addAttribute("idCommande", idCommande);
        model.addAttribute("montantCommande", montantCommande);

        return "Confirmation";
    }

    @PostMapping(value = "/payer-commande")
    public String payerCommandePost(@RequestParam int idCommande,
                                    @RequestParam Double montantCommande,
                                    @RequestParam(required = false) String numeroCarte,
                                    Model model) {

        model.addAttribute("idCommande", idCommande);
        model.addAttribute("montantCommande", montantCommande);

        try {

            logger.info(
                    "Paiement reçu: idCommande={}, montant={}, numeroCarte={}",
                    idCommande,
                    montantCommande,
                    numeroCarte != null ? "****" : "null"
            );

            PaiementBean paiementAExcecuter =
                    new PaiementBean();

            paiementAExcecuter.setIdCommande(idCommande);
            paiementAExcecuter.setMontant(montantCommande);

            if (numeroCarte != null && !numeroCarte.isBlank()) {

                try {

                    paiementAExcecuter.setNumeroCarte(
                            Long.parseLong(
                                    numeroCarte.replaceAll("\\D", "")
                            )
                    );

                } catch (NumberFormatException e) {

                    paiementAExcecuter.setNumeroCarte(
                            numcarte()
                    );
                }

            } else {

                paiementAExcecuter.setNumeroCarte(
                        numcarte()
                );
            }

            logger.info("Appel du proxy paiement");

            ResponseEntity<PaiementBean> paiement =
                    paiementProxy.payerUneCommande(
                            paiementAExcecuter
                    );

            boolean paiementAccepte = false;

            if (paiement.getStatusCode() == HttpStatus.CREATED) {
                paiementAccepte = true;
            }

            model.addAttribute(
                    "paiementOk",
                    paiementAccepte
            );

            logger.info(
                    "Paiement traité: accepté={}",
                    paiementAccepte
            );

            return "Confirmation";

        } catch (Exception e) {

            logger.error(
                    "Erreur lors du paiement: {}",
                    e.getMessage(),
                    e
            );

            model.addAttribute("paiementOk", false);

            model.addAttribute(
                    "errorMessage",
                    "Une erreur est survenue: " + e.getMessage()
            );

            return "Confirmation";
        }
    }

    // ============================================================
    // MÉTHODE UTILITAIRE
    // ============================================================

    // Génère un numéro de carte aléatoire
    private Long numcarte() {

        return ThreadLocalRandom.current().nextLong(
                1000000000000000L,
                9000000000000000L
        );
    }
}