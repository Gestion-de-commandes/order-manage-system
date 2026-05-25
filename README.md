# Order Management System — Architecture Microservices

Projet académique de cours d'**Intergiciel** (Middleware) — Système de gestion de commandes e-commerce basé sur une architecture microservices Spring Cloud, avec authentification JWT, communication asynchrone RabbitMQ, et exposition de services REST et SOAP.

---

## Architecture générale

```
                        ┌─────────────────────────────────────┐
                        │         CLIENT / NAVIGATEUR          │
                        └──────────────────┬──────────────────┘
                                           │ HTTP
                        ┌──────────────────▼──────────────────┐
                        │         GATEWAY SERVER :8081         │
                        │   JwtAuthenticationFilter            │
                        │   → délègue validation à auth:8085   │
                        └──┬──────────┬──────────┬────────────┘
                           │          │          │
              ┌────────────▼──┐  ┌────▼───┐  ┌──▼──────────┐
              │ ms-auth :8085 │  │ms-prod │  │ ms-commandes│
              │ JWT + BCrypt  │  │ :8082  │  │   :8083     │
              │ PostgreSQL    │  │  JPA   │  │    JPA      │
              └───────────────┘  └────────┘  └─────────────┘
                                                     │ RabbitMQ
                        ┌────────────────────────────▼────────┐
                        │      microservice-paiement :9213     │
                        │      REST + SOAP + WS-Security       │
                        │      PostgreSQL + RabbitMQ           │
                        └─────────────────────────────────────┘

Infrastructure :
  Eureka Server     :9201   — Service Discovery
  Config Server     :9200   — Configuration centralisée
  PostgreSQL        :5432   — Bases de données (Docker)
  RabbitMQ          :5672   — Broker de messages (Docker)
  RabbitMQ UI       :15672  — Interface de gestion RabbitMQ
```

---

## Microservices

| Service | Port | Rôle | Technologie clé |
|---|---|---|---|
| `config-server` | 9200 | Configuration centralisée | Spring Cloud Config |
| `eureka-server` | 9201 | Service Discovery | Netflix Eureka |
| `gateway-server` | 8081 | Point d'entrée unique + sécurité | Spring Cloud Gateway + JWT |
| `microservice-auth` | 8085 | Authentification + Autorisation | JWT + BCrypt + Swagger |
| `microservice-produits` | 8082 | Catalogue produits | Spring Data JPA |
| `microservice-commandes` | 8083 | Gestion des commandes | Spring Data JPA + Feign |
| `microservice-paiement` | 9213 | Traitement paiements | REST + SOAP + RabbitMQ |
| `microservice-clientui` | 8082 | Interface utilisateur web | Thymeleaf + Feign |

---

## Prérequis

- Java 11
- Maven 3.6+
- Docker + Docker Compose
- PostgreSQL (via Docker)
- RabbitMQ (via Docker)

---

## Démarrage rapide

### 1. Cloner le projet

```bash
git clone <url-du-repo>
cd order-manage-system
```

### 2. Lancer PostgreSQL et RabbitMQ

```bash
docker start postgres-db rabbitmq

# Si première fois, créer les containers :
docker run -d \
  --name postgres-db \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  postgres:13

docker run -d \
  --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management

# Créer les bases de données nécessaires
docker exec -it postgres-db psql -U postgres -c "CREATE DATABASE db_auth;"
docker exec -it postgres-db psql -U postgres -c "CREATE DATABASE db_commandes;"
docker exec -it postgres-db psql -U postgres -c "CREATE DATABASE db_produits;"
docker exec -it postgres-db psql -U postgres -c "CREATE DATABASE db_paiement;"
```

### 3. Lancement automatique (script)

```bash
chmod +x start-all.sh
./start-all.sh
```

### 4. Lancement manuel (dans l'ordre strict)

Ouvrir 8 terminaux séparés et lancer dans cet ordre :

```bash
# Terminal 1 — Config Server (attends "Tomcat started on port 9200")
cd config-server && mvn spring-boot:run

# Terminal 2 — Eureka (attends "Tomcat started on port 9201")
cd eureka-server && mvn spring-boot:run

# Terminal 3 — microservice-auth
cd microservice-auth && mvn spring-boot:run

# Terminal 4 — Gateway
cd gateway-server && mvn spring-boot:run

# Terminal 5 — microservice-produits
cd microservice-produits && mvn spring-boot:run

# Terminal 6 — microservice-commandes
cd microservice-commandes && mvn spring-boot:run

# Terminal 7 — microservice-paiement
cd microservice-paiement && mvn spring-boot:run

# Terminal 8 — microservice-clientui
cd microservice-clientui && mvn spring-boot:run
```

---

## Interfaces disponibles après démarrage

| Interface | URL | Description |
|---|---|---|
| Boutique client | http://localhost:8082 | Interface e-commerce complète |
| Eureka Dashboard | http://localhost:9201 | Liste des services enregistrés |
| Swagger Auth API | http://localhost:8085/swagger-ui/index.html | Documentation API authentification |
| RabbitMQ Manager | http://localhost:15672 | Interface RabbitMQ (guest/guest) |

---

## Fonctionnalités du microservice-auth

Le microservice d'authentification implémente un système JWT complet avec :

- **BCrypt** pour le hachage des mots de passe
- **Access Token** (24h) + **Refresh Token** (7 jours)
- **Rôles RBAC** : `ROLE_USER` et `ROLE_ADMIN`
- **Validation déléguée** : le Gateway appelle `/auth/validate` au lieu de connaître le secret JWT
- **Swagger UI** pour la documentation automatique

### Endpoints Auth

| Méthode | Endpoint | Accès | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Inscription |
| POST | `/auth/login` | Public | Connexion → tokens JWT |
| POST | `/auth/refresh` | Public | Renouveler l'access token |
| GET | `/auth/validate?token=` | Public | Vérifier un token (utilisé par Gateway) |
| GET | `/auth/me?token=` | Public | Profil depuis token |
| GET | `/auth/admin/users?token=` | ADMIN only | Liste tous les utilisateurs |

---

## Tests avec Postman

### Inscription

**Créer un utilisateur normal**
```
POST http://localhost:8085/auth/register
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@mcommerce.com",
  "password": "alice123",
  "role": "ROLE_USER"
}
```

**Créer un administrateur**
```
POST http://localhost:8085/auth/register
Content-Type: application/json

{
  "username": "superadmin",
  "email": "admin@mcommerce.com",
  "password": "Admin@2026",
  "role": "ROLE_ADMIN"
}
```

### Connexion

```
POST http://localhost:8085/auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "alice123"
}
```

Réponse :
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "alice",
  "role": "ROLE_USER"
}
```

### Démonstration de la sécurité JWT via Gateway

**Sans token → 401 Unauthorized**
```
GET http://localhost:8081/microservice-produits/produits
```

**Avec token → 200 OK**
```
GET http://localhost:8081/microservice-produits/produits
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Démonstration de l'autorisation par rôle

**Avec token ADMIN → 200 OK + liste utilisateurs**
```
GET http://localhost:8085/auth/admin/users?token=TOKEN_ADMIN
```

**Avec token USER → 403 Forbidden**
```
GET http://localhost:8085/auth/admin/users?token=TOKEN_USER

Réponse : "Accès refusé — cet endpoint est réservé aux administrateurs. Votre rôle : ROLE_USER"
```

### Refresh Token

```
POST http://localhost:8085/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Valider un token (utilisé par le Gateway)

```
GET http://localhost:8085/auth/validate?token=eyJhbGciOiJIUzI1NiJ9...
```

---

## Tests microservice-paiement

### REST

**Payer une commande**
```
POST http://localhost:9213/paiement
Content-Type: application/json

{
  "idCommande": 1,
  "montant": 150.00,
  "numeroCarte": 4111111111111111
}
```

**Double paiement (protection)**
```
POST http://localhost:9213/paiement
Content-Type: application/json

{
  "idCommande": 1,
  "montant": 150.00,
  "numeroCarte": 4111111111111111
}
Réponse : 409 Conflict — "Cette commande est déjà payée"
```

### SOAP (via Postman ou SoapUI)

URL : `http://localhost:9213/ws`  
Headers : `Content-Type: text/xml`

**Paiement SOAP avec authentification WS-Security**
```xml
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soap="http://mpaiement.com/soap">
   <soapenv:Header>
      <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
         <wsse:UsernameToken>
            <wsse:Username>hope</wsse:Username>
            <wsse:Password>paiement2026</wsse:Password>
         </wsse:UsernameToken>
      </wsse:Security>
   </soapenv:Header>
   <soapenv:Body>
      <soap:payerCommandeRequest>
         <soap:idCommande>10</soap:idCommande>
         <soap:montant>299.99</soap:montant>
         <soap:numeroCarte>4111111111111111</soap:numeroCarte>
      </soap:payerCommandeRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**Mauvais mot de passe WS-Security → SOAP Fault**
```xml
<wsse:Password>MAUVAIS_MOT_DE_PASSE</wsse:Password>
```

---

## Parcours utilisateur complet (interface web)

1. Ouvrir `http://localhost:8082`
2. Cliquer **S'inscrire** → créer un compte
3. Se connecter avec les identifiants créés
4. Parcourir les produits → cliquer **Voir les détails**
5. Choisir une quantité → **Commander ce produit**
6. Saisir un numéro de carte (ou laisser vide pour auto) → **Confirmer le paiement**
7. Page de confirmation avec statut du paiement
8. Vérifier dans RabbitMQ (`http://localhost:15672`) → onglet Queues → `paiement.notification`

---

## Architecture de sécurité

### Flux d'authentification

```
Client
  │ POST /auth/login {username, password}
  ▼
Gateway :8081
  │ URL publique /auth/** → laisse passer
  ▼
microservice-auth :8085
  │ 1. Charge user depuis PostgreSQL (UserDetailsServiceImpl)
  │ 2. Compare password avec hash BCrypt
  │ 3. Génère access token (24h) + refresh token (7j)
  ▼
Client reçoit {accessToken, refreshToken, role}
```

### Flux d'accès aux ressources protégées

```
Client
  │ GET /microservice-produits/produits
  │ Authorization: Bearer eyJ...
  ▼
Gateway :8081
  │ JwtAuthenticationFilter intercepte
  │ Appelle GET http://localhost:8085/auth/validate?token=eyJ...
  ▼
microservice-auth :8085
  │ Vérifie signature HMAC-SHA256
  │ Vérifie expiration
  │ Retourne 200 OK
  ▼
Gateway
  │ Ajoute header X-Auth-Token
  │ Transmet au microservice-produits
  ▼
Client reçoit la liste des produits
```

### Communication interne (Zero Trust partiel)

```
microservice-clientui
  │ Appel Feign avec header X-Internal-Request: clientui
  ▼
Gateway :8081
  │ Reconnaît le header interne → laisse passer sans token
  ▼
microservice-produits / commandes / paiement
```

---

## Structure du projet

```
order-manage-system/
├── start-all.sh                    ← Script de démarrage automatique
├── docker-compose.yml              ← Configuration Docker
├── config-server/                  ← Serveur de configuration
├── config-server-repo/             ← Fichiers de config par service
│   ├── microservice-auth.properties
│   ├── gateway-server.properties
│   ├── microservice-produits.properties
│   └── ...
├── eureka-server/                  ← Service Discovery
├── gateway-server/                 ← API Gateway + Filtre JWT
│   └── filter/JwtAuthenticationFilter.java
├── microservice-auth/              ← Authentification JWT
│   ├── controller/AuthController.java
│   ├── security/
│   │   ├── JwtUtils.java
│   │   ├── SecurityConfig.java
│   │   └── UserDetailsServiceImpl.java
│   ├── model/ (User, Role)
│   └── repository/ (UserRepository, RoleRepository)
├── microservice-produits/          ← Catalogue
├── microservice-commandes/         ← Commandes
├── microservice-paiement/          ← Paiements REST + SOAP
│   ├── web/controller/PaiementController.java
│   ├── soap/endpoint/PaiementEndpoint.java
│   ├── soap/security/SimplePasswordValidator.java
│   └── configuration/RabbitMQConfig.java
└── microservice-clientui/          ← Interface web Thymeleaf
    ├── controller/ClientController.java
    └── templates/ (Accueil, Login, Register, Paiement, Confirmation)
```

---

## Technologies utilisées

| Catégorie | Technologie | Usage |
|---|---|---|
| Framework | Spring Boot 2.7.9 | Base de tous les microservices |
| Cloud | Spring Cloud 2021.0.5 | Config, Discovery, Gateway |
| Sécurité | Spring Security + JWT (jjwt 0.11.5) | Authentification/Autorisation |
| Hachage | BCrypt | Sécurisation des mots de passe |
| Base de données | PostgreSQL 13 + Spring Data JPA | Persistance |
| Messagerie | RabbitMQ 3 | Communication asynchrone |
| Services Web | Spring-WS + JAXB | Exposition SOAP |
| Documentation | SpringDoc OpenAPI (Swagger) | Documentation API auto |
| Frontend | Thymeleaf + Bootstrap 5 | Interface utilisateur |
| Discovery | Netflix Eureka | Service Discovery |
| Containerisation | Docker | Infrastructure |

---

## Concepts d'intergiciel démontrés

- **Service Discovery** : Eureka enregistre dynamiquement chaque microservice
- **API Gateway** : point d'entrée unique avec filtrage JWT centralisé
- **Config centralisée** : Spring Cloud Config Server distribue la configuration
- **Communication synchrone** : REST (Feign Client) entre microservices
- **Communication asynchrone** : RabbitMQ pour les notifications de paiement
- **Multi-protocole** : REST et SOAP exposés par le même microservice
- **JWT stateless** : authentification sans session serveur
- **RBAC** : contrôle d'accès basé sur les rôles (USER/ADMIN)
- **Zero Trust partiel** : validation systématique de chaque requête

---

## Auteurs

Projet réalisé dans le cadre du cours d'Intergiciel — 2ème Semestre 2026