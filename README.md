<<<<<<< HEAD
# Spring Boot Microservices - Migration PostgreSQL

Ce projet est une architecture de microservices utilisant Spring Boot et Spring Cloud.

## Services
* **microservice-produits** : Gestion du catalogue produits.
* **microservice-commandes** : Gestion des commandes.
* **microservice-paiement** : Gestion des paiements.
* **microservice-clientui** : Interface utilisateur.

## Infrastructure Cloud
* **config-server** : Serveur de configuration centralisé.
* **eureka-server** : Serveur de découverte.
* **gateway-server** : API Gateway.

---

## Configuration de PostgreSQL

Chaque microservice métier (`produits`, `commandes`, `paiement`) utilise désormais une base de données PostgreSQL dédiée.

### 1. Prérequis
- Docker et Docker Compose installés.
- Ou une instance PostgreSQL locale en cours d'exécution.

### 2. Lancement avec Docker Compose
Le fichier `docker-compose.yml` à la racine permet de lancer une instance PostgreSQL avec les bases de données nécessaires créées automatiquement.

```bash
docker-compose up -d
```

Cela va :
- Lancer un container PostgreSQL sur le port `5432`.
- Créer les bases : `db_produits`, `db_commandes`, `db_paiement`.
- L'utilisateur par défaut est `postgres` avec le mot de passe `password`.

### 3. Configuration Manuelle (si pas de Docker)
Si vous utilisez votre propre instance PostgreSQL, assurez-vous de créer les bases de données suivantes :
```sql
CREATE DATABASE db_produits;
CREATE DATABASE db_commandes;
CREATE DATABASE db_paiement;
```

Les accès sont centralisés dans le répertoire `config-server-repo` au sein des fichiers `.properties` correspondants :
- `spring.datasource.url=jdbc:postgresql://localhost:5432/db_name`
- `spring.datasource.username=postgres`
- `spring.datasource.password=password`

### 4. Migration des données
La migration vers PostgreSQL est gérée automatiquement par Hibernate (`spring.jpa.hibernate.ddl-auto=update`).
Pour le microservice produits, le schéma initial est défini dans `microservice-produits/src/main/resources/schema.sql`.

### 5. Lancement des services
Assurez-vous de lancer les services dans l'ordre suivant :
1. `config-server` (port 9200)
2. `eureka-server` (port 9201)
3. `gateway-server` (port 8081)
4. Les microservices métiers.
=======
# Gestion-de-commande-backend
>>>>>>> e8ea82b50fe57523943f08998ab87f977a4b8039
