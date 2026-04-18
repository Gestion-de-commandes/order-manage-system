package com.mproduits.web.controller;

import com.mproduits.configurations.ApplicationPropertiesConfiguration;
import com.mproduits.dao.ProductDao;
import com.mproduits.model.Product;
import com.mproduits.web.exceptions.ProductNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/produits")
public class ProductController {

    private final ProductDao productDao;
    private final ApplicationPropertiesConfiguration appProperties;

    public ProductController(ProductDao productDao, ApplicationPropertiesConfiguration applicationPropertiesConfiguration) {
        this.productDao = productDao;
        this.appProperties = applicationPropertiesConfiguration;
    }

    // Affiche la liste de tous les produits disponibles
    @GetMapping
    @CircuitBreaker(name="inventory", fallbackMethod = "fallBackMethod")
    @TimeLimiter(name="inventory")
    @Retry(name ="inventory") // Correction du "ï"
    public CompletableFuture<List<Product>> listeDesProduits(){
        return CompletableFuture.supplyAsync(() -> {
            List<Product> products = productDao.findAll();

            if(products.isEmpty()) {
                throw new ProductNotFoundException("Aucun produit n'est disponible à la vente");
            }

            // Calcul de la limite pour éviter l'IndexOutOfBoundsException
            int limit = Math.min(products.size(), appProperties.getLimitDeProduits());
            return products.subList(0, limit);
        });
    }

    /**
     * Fallback avec le MÊME type de retour (List<Product>)
     * Cela évite l'erreur de conversion (Content-Type text/plain) dans ClientUI
     */
    public CompletableFuture<List<Product>> fallBackMethod(Exception e){
        return CompletableFuture.supplyAsync(() -> {
            // On retourne une liste vide au lieu d'un String
            // Le ClientUI verra juste une page d'accueil sans produits au lieu d'une erreur 500
            return new ArrayList<>(); 
        });
    }

    // Récupérer un produit par son id
    @GetMapping(value = "/{id}")
    public Optional<Product> recupererUnProduit(@PathVariable int id) {
        Optional<Product> product = productDao.findById(id);
        if(!product.isPresent()) {
            throw new ProductNotFoundException("Le produit correspondant à l'id " + id + " n'existe pas");
        }
        return product;
    }

    @PostMapping
    public Product ajouterUnProduit(@RequestBody Product product) {
        productDao.save(product);
        return product;
    }
}