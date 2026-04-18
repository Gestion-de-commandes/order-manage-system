package com.mproduits;

import com.mproduits.dao.ProductDao;
import com.mproduits.model.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import java.util.Arrays;

@SpringBootApplication
@EnableDiscoveryClient
public class MproduitsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MproduitsApplication.class, args);
	}

	@Bean
	public CommandLineRunner seedDatabase(ProductDao productDao) {
		return args -> {
			if (productDao.count() < 10) {
				productDao.deleteAll();
				productDao.saveAll(Arrays.asList(
					new Product(1, "Bougie fonctionnant au feu", "bougie qui fonctionne comme une ampoule mais sans électricité !", "https://github.com/OpenClassrooms-Student-Center/4668216-Optimisez-votre-architecture-Microservices/blob/master/images/Bougie.PNG?raw=true", 22.0),
					new Product(2, "Chaise pour s'assoire", "Chaise rare avec non pas 1 ni 2 mais 3 pieds", "https://github.com/OpenClassrooms-Student-Center/4668216-Optimisez-votre-architecture-Microservices/blob/master/images/Chaise.PNG?raw=true", 95.0),
					new Product(3, "Cheval pour nains", "Ce cheval ne portera certainement pas blanche neige, mais sans problème les nains", "https://github.com/OpenClassrooms-Student-Center/4668216-Optimisez-votre-architecture-Microservices/blob/master/images/Cheval.PNG?raw=true", 360.0),
					new Product(4, "Coq of steel", "Le superman des volailles. Ne passe pas au four", "https://github.com/OpenClassrooms-Student-Center/4668216-Optimisez-votre-architecture-Microservices/blob/master/images/coq.PNG?raw=true", 620.0),
					new Product(5, "Flacon à frotter", "Vous donne droit à l'équivalent de 3/0 voeux", "https://github.com/OpenClassrooms-Student-Center/4668216-Optimisez-votre-architecture-Microservices/blob/master/images/lampe.PNG?raw=true", 1200.0),
					new Product(6, "Horloge quantique", "Donne l'heure, les minutes et même les secondes.", "https://github.com/OpenClassrooms-Student-Center/4668216-Optimisez-votre-architecture-Microservices/blob/master/images/Horloge.PNG?raw=true", 180.0),
					new Product(7, "Table d'opération Hamsters", "Pour réaliser vos opérations chirugicales sur votre Hamster!", "https://github.com/OpenClassrooms-Student-Center/4668216-Optimisez-votre-architecture-Microservices/blob/master/images/table%20d'op%C3%A9ration.PNG?raw=true", 210.0),
					new Product(8, "Vase de Zeus", "Risque de choc électrique", "https://github.com/OpenClassrooms-Student-Center/4668216-Optimisez-votre-architecture-Microservices/blob/master/images/Vase.PNG?raw=true", 730.0),
					new Product(9, "Montre connectée", "Montre intelligente avec suivi d'activité.", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80", 249.0),
					new Product(10, "Sac à dos urbain", "Sac à dos imperméable pour la ville.", "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=800&q=80", 89.0),
					new Product(11, "Lampe solaire", "Lampe d'extérieur à énergie solaire.", "https://images.unsplash.com/photo-1509395176047-4a66953fd231?auto=format&fit=crop&w=800&q=80", 79.0),
					new Product(12, "Enceinte Bluetooth", "Enceinte sans fil avec son puissant.", "https://images.unsplash.com/photo-1519671482749-fd09be7ccebf?auto=format&fit=crop&w=800&q=80", 129.0),
					new Product(13, "Casque audio premium", "Casque circum-aural avec réduction de bruit.", "https://images.unsplash.com/photo-1510070009289-b5bc34383727?auto=format&fit=crop&w=800&q=80", 199.0),
					new Product(14, "Appareil photo Reflex", "Capturez des moments inoubliables.", "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80", 850.0),
					new Product(15, "Clavier Gamer", "Touches réactives et rétroéclairage RGB.", "https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?auto=format&fit=crop&w=800&q=80", 145.0),
					new Product(16, "Chaise ergonomique", "Confort optimal pour le travail.", "https://images.unsplash.com/photo-1505797149-43b0069ec26b?auto=format&fit=crop&w=800&q=80", 299.0),
					new Product(17, "Machine à café", "Savourez un café de qualité professionnelle.", "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?auto=format&fit=crop&w=800&q=80", 450.0),
					new Product(18, "Drone 4K", "Prenez de la hauteur.", "https://images.unsplash.com/photo-1507582020474-9a35b7d455d9?auto=format&fit=crop&w=800&q=80", 599.0),
					new Product(19, "Vélo électrique", "Déplacez-vous sans effort.", "https://images.unsplash.com/photo-1571068316344-75bc76f77891?auto=format&fit=crop&w=800&q=80", 1200.0),
					new Product(20, "Projecteur Cinéma", "Transformez votre salon.", "https://images.unsplash.com/photo-1535016120720-40c646bebbbb?auto=format&fit=crop&w=800&q=80", 350.0)
				));
			}
		};
	}
}
