package com.mpaiement.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cette classe configure RabbitMQ pour le microservice paiement.
 *
 * CONCEPTS CLÉS à retenir pour le prof :
 *
 * QUEUE (file d'attente) = boîte aux lettres dans RabbitMQ.
 * Les messages sont déposés dedans par le producteur (paiement)
 * et lus par le consommateur (notification).
 *
 * durable=true = si RabbitMQ redémarre, la queue n'est pas perdue.
 * C'est important pour ne pas perdre des notifications de paiement.
 */
@Configuration
public class RabbitMQConfig {

    // Nom de la file — doit être IDENTIQUE dans microservice-notification
    // C'est le "nom de la boîte aux lettres" partagée
    public static final String QUEUE_PAIEMENT = "paiement.notification";

    /**
     * Crée la queue dans RabbitMQ au démarrage.
     * Si la queue existe déjà, Spring ne la recrée pas.
     * durable=true : survive aux redémarrages de RabbitMQ
     */
    @Bean
    public Queue queuePaiement() {
        return new Queue(QUEUE_PAIEMENT, true);
    }

    /**
     * Convertisseur de messages : Java → JSON → RabbitMQ
     *
     * Sans ça, Spring envoie les objets Java en format binaire
     * illisible. Avec Jackson2JsonMessageConverter, les messages
     * sont en JSON — lisibles et compréhensibles par tous les
     * microservices, peu importe leur langage.
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
