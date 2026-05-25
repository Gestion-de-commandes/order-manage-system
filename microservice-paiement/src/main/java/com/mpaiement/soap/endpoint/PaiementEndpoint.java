package com.mpaiement.soap.endpoint;

import com.mpaiement.configuration.RabbitMQConfig;
import com.mpaiement.dao.PaiementDao;
import com.mpaiement.dto.PaiementMessage;
import com.mpaiement.model.Paiement;
import com.mpaiement.soap.PayerCommandeRequest;
import com.mpaiement.soap.PayerCommandeResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * @Endpoint = dit à Spring "cette classe est un service SOAP"
 * C'est l'équivalent de @RestController mais pour SOAP.
 *
 * POURQUOI un Endpoint séparé du RestController ?
 * → Le RestController gère les requêtes HTTP/REST (JSON)
 * → L'Endpoint gère les requêtes SOAP (XML)
 * → Les deux coexistent dans le même microservice !
 * → C'est exactement ce qu'on veut : REST + SOAP ensemble
 */
@Endpoint
public class PaiementEndpoint {

    /**
     * NAMESPACE = l'identifiant unique de ton service SOAP.
     * C'est comme un nom de domaine pour ton service.
     * DOIT correspondre exactement au targetNamespace dans le fichier XSD.
     * Si ça ne correspond pas → SOAP ne trouve pas le service → erreur.
     */
    private static final String NAMESPACE_URI = "http://mpaiement.com/soap";

    /**
     * PaiementDao = l'objet qui communique avec la base de données PostgreSQL.
     * @Autowired = Spring injecte automatiquement cet objet.
     * Tu n'as pas à faire "new PaiementDao()" → Spring le fait pour toi.
     */
    @Autowired
    private PaiementDao paiementDao;

    /**
     * RabbitTemplate = l'objet qui envoie des messages dans RabbitMQ.
     * @Autowired = Spring l'injecte automatiquement grâce à la dépendance
     * spring-boot-starter-amqp ajoutée dans pom.xml.
     *
     * C'est l'équivalent d'un "service postal" :
     * tu lui donnes une lettre (message) et une adresse (queue),
     * il se charge de la livraison.
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @PayloadRoot = définit QUELLE requête SOAP cette méthode traite.
     *
     * namespace → doit correspondre au targetNamespace du XSD
     * localPart → doit correspondre au nom de l'élément dans le XSD
     *
     * @RequestPayload = JAXB convertit XML → objet Java PayerCommandeRequest
     * @ResponsePayload = JAXB convertit objet Java → XML pour la réponse
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "payerCommandeRequest")
    @ResponsePayload
    public PayerCommandeResponse payerCommande(@RequestPayload PayerCommandeRequest request) {

        // ── ÉTAPE 1 : Lire les données de la requête ──────────────────────
        // JAXB a déjà converti le XML en objet Java pour nous
        int idCommande = request.getIdCommande();
        double montant = request.getMontant();
        long numeroCarte = request.getNumeroCarte();

        // ── ÉTAPE 2 : Vérifier si la commande est déjà payée ──────────────
        // On ne doit pas payer deux fois la même commande !
        boolean dejaPayee = paiementDao.findByidCommande(idCommande) != null;

        // ── ÉTAPE 3 : Préparer la réponse SOAP ────────────────────────────
        PayerCommandeResponse response = new PayerCommandeResponse();
        response.setIdCommande(idCommande);
        response.setMontant(montant);

        if (dejaPayee) {
            // Commande déjà payée → on refuse
            response.setIdPaiement(-1);
            response.setStatut("REFUSE - Commande déjà payée");

            // ── NOTIFICATION REFUS dans RabbitMQ ──────────────────────────
            // On notifie aussi les refus — utile pour les logs et alertes
            // PaiementMessage = notre DTO (objet de transfert de données)
            // Il sera converti en JSON par Jackson2JsonMessageConverter
            PaiementMessage messageRefus = new PaiementMessage(
                idCommande, montant, "REFUSE - Commande déjà payée"
            );
            // convertAndSend() = convertit l'objet Java en JSON
            //                    puis dépose le message dans la queue
            // Argument 1 : nom de la queue (défini dans RabbitMQConfig)
            // Argument 2 : le message à envoyer
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.QUEUE_PAIEMENT, messageRefus
            );

        } else {
            // ── ÉTAPE 4 : Sauvegarder le paiement en base ─────────────────
            Paiement paiement = new Paiement();
            paiement.setIdCommande(idCommande);
            paiement.setMontant(montant);
            paiement.setNumeroCarte(numeroCarte);

            // INSERT INTO paiement (...) VALUES (...)
            Paiement paiementSauvegarde = paiementDao.save(paiement);

            // ── ÉTAPE 5 : Retourner la réponse de succès ──────────────────
            response.setIdPaiement(paiementSauvegarde.getId());
            response.setStatut("ACCEPTE");

            // ── ÉTAPE 6 : Publier dans RabbitMQ ───────────────────────────
            // APRÈS avoir sauvegardé → on notifie RabbitMQ
            // L'ordre est important : d'abord sauvegarder en BDD,
            // ensuite notifier. Si on notifie avant et que la BDD plante,
            // on aurait une notification sans paiement réel → incohérence.
            PaiementMessage messageAccepte = new PaiementMessage(
                idCommande, montant, "ACCEPTE"
            );
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.QUEUE_PAIEMENT, messageAccepte
            );
        }

        // Spring-WS + JAXB convertissent cet objet Java en XML SOAP
        return response;
    }
}