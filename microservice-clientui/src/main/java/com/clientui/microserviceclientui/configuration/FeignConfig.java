package com.clientui.microserviceclientui.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Feign — ajoute des headers à chaque appel vers le Gateway.
 *
 * POURQUOI X-Internal-Request ?
 * Le Gateway a deux types de clients :
 *   1. Utilisateurs externes (navigateur, Postman) → doivent avoir un token JWT
 *   2. Microservices internes (clientui) → communication serveur-à-serveur
 *
 * Le clientui n'a pas de token JWT — il appelle les autres microservices
 * directement pour construire les pages HTML.
 * Ce header dit au Gateway : "je suis le clientui, laisse-moi passer".
 *
 * En production on utiliserait mTLS ou un token de service pour sécuriser
 * davantage cette communication interne.
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor internalRequestInterceptor() {
        return requestTemplate -> {
            // Identifie toutes les requêtes Feign comme venant du clientui
            requestTemplate.header("X-Internal-Request", "clientui");
        };
    }
}