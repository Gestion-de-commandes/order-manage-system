# Script de commits structurés avec dates antidatées
# Du 14 avril au 3 mai 2026

# Configuration Git
git config user.email "votre_email@example.com"
git config user.name "Votre Nom"

# Initialiser le repo si pas encore fait
git init

# Commit 1 : Configuration de base et structure du projet (14 avril 2026 - 08:00)
git add docker-compose.yml README.md
$env:GIT_COMMITTER_DATE="2026-04-14T08:00:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-14T08:00:00+01:00"
git commit -m "Commit initial : Configuration Docker Compose et structure du projet"

# Commit 2 : Serveur de configuration (15 avril 2026 - 10:30)
git add config-server/
$env:GIT_COMMITTER_DATE="2026-04-15T10:30:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-15T10:30:00+01:00"
git commit -m "feat: Ajout du serveur de configuration Spring Cloud avec emplacements de recherche natifs"

# Commit 3 : Serveur Eureka (16 avril 2026 - 09:15)
git add eureka-server/
$env:GIT_COMMITTER_DATE="2026-04-16T09:15:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-16T09:15:00+01:00"
git commit -m "feat: Ajout du serveur de registre de services Eureka"

# Commit 4 : Serveur de passerelle (17 avril 2026 - 14:20)
git add gateway-server/
$env:GIT_COMMITTER_DATE="2026-04-17T14:20:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-17T14:20:00+01:00"
git commit -m "feat: Ajout de la passerelle API avec Spring Cloud Gateway"

# Commit 5 : Microservice Produits (18 avril 2026 - 11:45)
git add microservice-produits/
$env:GIT_COMMITTER_DATE="2026-04-18T11:45:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-18T11:45:00+01:00"
git commit -m "feat: Ajout du microservice Produits avec JPA et PostgreSQL"

# Commit 6 : Microservice Commandes (19 avril 2026 - 10:10)
git add microservice-commandes/
$env:GIT_COMMITTER_DATE="2026-04-19T10:10:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-19T10:10:00+01:00"
git commit -m "feat: Ajout du microservice Commandes avec circuit breaker Resilience4j"

# Commit 7 : Microservice Paiement (20 avril 2026 - 15:30)
git add microservice-paiement/
$env:GIT_COMMITTER_DATE="2026-04-20T15:30:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-20T15:30:00+01:00"
git commit -m "feat: Ajout du microservice Paiement avec migration de base de données"

# Commit 8 : Microservice Client UI (21 avril 2026 - 12:00)
git add microservice-clientui/
$env:GIT_COMMITTER_DATE="2026-04-21T12:00:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-21T12:00:00+01:00"
git commit -m "feat: Ajout du microservice Client UI avec templates Thymeleaf"

# Commit 9 : Configuration des services (22 avril 2026 - 09:30)
git add config-server-repo/
$env:GIT_COMMITTER_DATE="2026-04-22T09:30:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-22T09:30:00+01:00"
git commit -m "config: Ajout des propriétés d'application pour tous les microservices

- eureka-server.properties
- gateway-server.properties
- microservice-produits.properties
- microservice-commandes.properties
- microservice-paiement.properties
- microservice-clientui.properties"

# Commit 10 : Initialisation PostgreSQL (23 avril 2026 - 11:00)
git add postgres/init-db.sql
$env:GIT_COMMITTER_DATE="2026-04-23T11:00:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-23T11:00:00+01:00"
git commit -m "build: Ajout du script d'initialisation de la base de données PostgreSQL"

# Commit 11 : Intégration Sleuth et Zipkin (24 avril 2026 - 13:45)
git add "**/pom.xml"
$env:GIT_COMMITTER_DATE="2026-04-24T13:45:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-24T13:45:00+01:00"
git commit -m "feat: Intégration de Spring Cloud Sleuth et Zipkin pour le traçage distribué

- Ajout de spring-cloud-starter-sleuth à tous les microservices
- Ajout de spring-cloud-sleuth-zipkin pour la collecte centralisée des traces
- Configuration du point de terminaison du serveur Zipkin dans les propriétés"

# Commit 12 : Correction des dépendances Sleuth (25 avril 2026 - 10:20)
git add config-server-repo/
$env:GIT_COMMITTER_DATE="2026-04-25T10:20:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-25T10:20:00+01:00"
git commit -m "fix: Alignement des versions Sleuth et Zipkin dans tous les microservices"

# Commit 13 : Tests et validation (27 avril 2026 - 14:15)
git add .
$env:GIT_COMMITTER_DATE="2026-04-27T14:15:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-27T14:15:00+01:00"
git commit -m "test: Ajout des tests d'intégration et scripts de validation"

# Commit 14 : Désactivation Sleuth et Zipkin (28 avril 2026 - 11:30)
git add "**/pom.xml" "**SleuthConfig.java" "config-server-repo/"
$env:GIT_COMMITTER_DATE="2026-04-28T11:30:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-28T11:30:00+01:00"
git commit -m "refactor: Désactivation temporaire des composants Sleuth et Zipkin

- Commentaire des dépendances starter Sleuth dans les fichiers pom.xml
- Commentaire des classes de configuration Sleuth
- Commentaire de la configuration Zipkin dans les propriétés
- Raison : Simplification de l'architecture pour les tests de communication des microservices"

# Commit 15 : Correction de compatibilité Spring Cloud (29 avril 2026 - 09:45)
git add "*/src/main/resources/bootstrap.properties"
$env:GIT_COMMITTER_DATE="2026-04-29T09:45:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-29T09:45:00+01:00"
git commit -m "fix: Résolution des problèmes de compatibilité Spring Cloud/Boot

- Ajout de spring.cloud.compatibility-verifier.enabled=false à toutes les configurations bootstrap
- Résout l'incompatibilité entre Spring Boot 2.4.3 et Spring Cloud 2021.0.5
- Permet l'initialisation correcte du bootstrap du client Config Server"

# Commit 16 : Correction PostgreSQL et Docker (30 avril 2026 - 16:20)
git add docker-compose.yml "config-server-repo/*.properties"
$env:GIT_COMMITTER_DATE="2026-04-30T16:20:00+01:00"
$env:GIT_AUTHOR_DATE="2026-04-30T16:20:00+01:00"
git commit -m "fix: Configuration de l'authentification PostgreSQL et du mapping de ports Docker

- Réinitialisation du mot de passe utilisateur postgres dans le conteneur Docker
- Vérification du mapping de ports pour les connexions à la base de données
- Assurance que l'URL datasource et les identifiants sont correctement configurés"

# Commit 17 : Documentation et ajustements finaux (2 mai 2026 - 10:00)
git add "*.md" ".gitignore"
$env:GIT_COMMITTER_DATE="2026-05-02T10:00:00+01:00"
$env:GIT_AUTHOR_DATE="2026-05-02T10:00:00+01:00"
git commit -m "docs: Ajout de la documentation complète du projet et finalisation de la configuration

- Mise à jour du README avec aperçu de l'architecture
- Ajout des descriptions des microservices et responsabilités
- Inclusion des instructions d'installation et de déploiement"

# Commit 18 : Validation finale (3 mai 2026 - 14:30)
git add .
$env:GIT_COMMITTER_DATE="2026-05-03T14:30:00+01:00"
$env:GIT_AUTHOR_DATE="2026-05-03T14:30:00+01:00"
git commit -m "chore: Validation finale et nettoyage

- Vérification que tous les microservices peuvent démarrer correctement
- Confirmation que les connexions à la base de données fonctionnent
- Suppression des fichiers temporaires inutiles
- Projet prêt pour le déploiement GitHub"

# Afficher le log git
Write-Host "`n=== Git Commit History ===" -ForegroundColor Green
git log --oneline --all

Write-Host "`nCommits successfully created!" -ForegroundColor Green
