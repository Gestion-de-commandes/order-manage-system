package com.clientui.microserviceclientui.proxies;

import com.clientui.microserviceclientui.beans.CommandeBean;
import com.clientui.microserviceclientui.configuration.FeignConfig; // Assurez-vous que l'import correspond à votre package config
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * On ajoute :
 * 1. configuration = FeignConfig.class -> pour passer le badge admin/admin
 * 2. contextId -> pour éviter les conflits si vous avez plusieurs proxies pointant vers gateway-server
 * 3. L'URL vers la gateway inclut généralement le nom du service cible
 */
@FeignClient(
    name = "gateway-server", 
    contextId = "microserviceCommandeProxy", 
    configuration = FeignConfig.class
)
public interface MicroserviceCommandeProxy {

    // Note : Si vous passez par la Gateway, l'URL doit correspondre au mapping de la gateway
    // D'après vos tests PowerShell, c'est probablement : /microservice-commandes/commandes
    @PostMapping(value = "/microservice-commandes/commandes")
    CommandeBean ajouterCommande(@RequestBody CommandeBean commande);
}