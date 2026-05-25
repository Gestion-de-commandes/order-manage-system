package co.essejacques.gatewayserver.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8085")
            .build();

    /**
     * URLs TOUJOURS publiques — pas de token requis.
     * /auth/** : endpoints d'authentification
     * /actuator : monitoring
     */
    private static final List<String> PUBLIC_URLS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/auth/validate",
            "/auth/me",
            "/actuator"
    );

    /**
     * URLs accessibles depuis le clientui (communication interne).
     * Ces routes nécessitent soit un token JWT soit le header interne X-Internal-Request.
     * Le clientui envoie X-Internal-Request: clientui dans chaque appel Feign.
     */
    private static final List<String> INTERNAL_URLS = List.of(
            "/microservice-produits",
            "/microservice-commandes",
            "/microservice-paiement"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        logger.info("Gateway reçoit requête : {}", path);

        // 1. URL toujours publique → laisse passer sans vérification
        if (isPublicUrl(path)) {
            logger.info("URL publique, laisse passer : {}", path);
            return chain.filter(exchange);
        }

        // 2. Vérifie si c'est une requête interne du clientui
        // Le header X-Internal-Request est ajouté par FeignConfig du clientui
        String internalHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Internal-Request");

        if (internalHeader != null && internalHeader.equals("clientui")
                && isInternalUrl(path)) {
            logger.info("Requête interne clientui, laisse passer : {}", path);
            return chain.filter(exchange);
        }

        // 3. Requête externe → exige un token JWT
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Token JWT manquant pour : {}", path);
            return rejectRequest(exchange, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        // 4. Délègue la validation au microservice-auth
        return webClient.get()
                .uri("/auth/validate?token=" + token)
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        logger.info("Token validé par microservice-auth ✓");
                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(exchange.getRequest().mutate()
                                        .header("X-Auth-Token", token)
                                        .build())
                                .build();
                        return chain.filter(modifiedExchange);
                    } else {
                        logger.warn("Token rejeté par microservice-auth");
                        return rejectRequest(exchange, HttpStatus.UNAUTHORIZED);
                    }
                })
                .onErrorResume(error -> {
                    logger.error("Microservice-auth inaccessible : {}",
                            error.getMessage());
                    return rejectRequest(exchange, HttpStatus.SERVICE_UNAVAILABLE);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private boolean isPublicUrl(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::startsWith);
    }

    private boolean isInternalUrl(String path) {
        return INTERNAL_URLS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> rejectRequest(ServerWebExchange exchange,
                                      HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}