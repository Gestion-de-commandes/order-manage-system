package com.clientui.microserviceclientui.proxies;

import com.clientui.microserviceclientui.beans.PaiementBean;
import com.clientui.microserviceclientui.configuration.FeignConfig;// Import de votre configuration de sécurité
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 1. configuration = FeignConfig.class : injecte l'intercepteur Basic Auth (admin/admin).
 * 2. contextId : évite les conflits de beans dans le contexte Spring.
 * 3. name : On peut utiliser "gateway-server" pour passer par la Gateway (recommandé).
 */
@FeignClient(
    name = "gateway-server", 
    contextId = "microservicePaiementProxy", 
    configuration = FeignConfig.class
)
public interface MicroservicePaiementProxy {

    // L'URL via la Gateway doit inclure le nom du service enregistré dans Eureka
    @PostMapping(value = "/microservice-paiement/paiement")
    ResponseEntity<PaiementBean> payerUneCommande(@RequestBody PaiementBean paiement);

}