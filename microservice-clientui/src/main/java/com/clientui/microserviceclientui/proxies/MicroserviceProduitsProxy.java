package com.clientui.microserviceclientui.proxies;

import com.clientui.microserviceclientui.beans.ProductBean;
import com.clientui.microserviceclientui.configuration.FeignConfig; // Vérifiez bien ce package !
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
    name = "gateway-server", 
    contextId = "microserviceProduitsProxy",
    configuration = FeignConfig.class  // <-- AJOUTEZ CETTE LIGNE ICI
)
public interface MicroserviceProduitsProxy {

    @GetMapping(value = "/microservice-produits/produits")
    List<ProductBean> listeDesProduits();

    @GetMapping(value = "/microservice-produits/produits/{id}")
    ProductBean recupererUnProduit(@PathVariable("id") int id);
}