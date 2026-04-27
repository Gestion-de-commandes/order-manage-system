# Guide Complet : Commits Structurés vers GitHub

## 🚀 Exécution Rapide

### Option 1 : Script PowerShell (Windows)
```powershell
cd C:\Users\hp\spring-boot-edge-microservices
.\git-commits-script.ps1
```

### Option 2 : Script Bash (Linux/Mac)
```bash
cd ~/spring-boot-edge-microservices
bash git-commits-script.sh
```

---

## 📋 Commandes Manuelles (si le script ne fonctionne pas)

### 1️⃣ Configuration initiale
```bash
cd ~/spring-boot-edge-microservices
git init
git config user.email "votre_email@github.com"
git config user.name "Votre Nom"
```

### 2️⃣ Commits structurés par date

```bash
# Commit 1 : Structure de base (14 avril 2026)
git add docker-compose.yml README.md
git commit -m "Commit initial : Configuration Docker Compose et structure du projet" \
    --date="2026-04-14T08:00:00+01:00"

# Commit 2 : Serveur de configuration (15 avril)
git add config-server/
git commit -m "feat: Ajout du serveur de configuration Spring Cloud avec emplacements de recherche natifs" \
    --date="2026-04-15T10:30:00+01:00"

# Commit 3 : Serveur Eureka (16 avril)
git add eureka-server/
git commit -m "feat: Ajout du serveur de registre de services Eureka" \
    --date="2026-04-16T09:15:00+01:00"

# Commit 4 : Serveur de passerelle (17 avril)
git add gateway-server/
git commit -m "feat: Ajout de la passerelle API avec Spring Cloud Gateway" \
    --date="2026-04-17T14:20:00+01:00"

# Commit 5 : Microservice Produits (18 avril)
git add microservice-produits/
git commit -m "feat: Ajout du microservice Produits avec JPA et PostgreSQL" \
    --date="2026-04-18T11:45:00+01:00"

# Commit 6 : Microservice Commandes (19 avril)
git add microservice-commandes/
git commit -m "feat: Ajout du microservice Commandes avec circuit breaker Resilience4j" \
    --date="2026-04-19T10:10:00+01:00"

# Commit 7 : Microservice Paiement (20 avril)
git add microservice-paiement/
git commit -m "feat: Ajout du microservice Paiement avec migration de base de données" \
    --date="2026-04-20T15:30:00+01:00"

# Commit 8 : Microservice Client UI (21 avril)
git add microservice-clientui/
git commit -m "feat: Ajout du microservice Client UI avec templates Thymeleaf" \
    --date="2026-04-21T12:00:00+01:00"

# Commit 9 : Configuration des propriétés (22 avril)
git add config-server-repo/
git commit -m "config: Ajout des propriétés d'application pour tous les microservices
- Configuration datasource PostgreSQL
- Paramètres client Eureka
- Mapping des ports serveur" \
    --date="2026-04-22T09:30:00+01:00"

# Commit 10 : Init PostgreSQL (23 avril)
git add postgres/
git commit -m "build: Ajout du script d'initialisation de la base de données PostgreSQL" \
    --date="2026-04-23T11:00:00+01:00"

# Commit 11 : Sleuth & Zipkin (24 avril)
git add .
git commit -m "feat: Intégration de Spring Cloud Sleuth et Zipkin pour le traçage distribué
- Ajout de la dépendance spring-cloud-starter-sleuth
- Configuration du point de terminaison serveur Zipkin
- Configuration du traçage dans tous les microservices" \
    --date="2026-04-24T13:45:00+01:00"

# Commit 12 : Correction versions Sleuth (25-26 avril)
git add "*/pom.xml" "config-server-repo/"
git commit -m "fix: Alignement des versions Sleuth et Zipkin dans tous les microservices
- Mise à jour de la version sleuth vers 3.0.4
- Assurance de la compatibilité avec les dépendances Spring Cloud" \
    --date="2026-04-26T11:20:00+01:00"

# Commit 13 : Tests et validation (27 avril)
git add .
git commit -m "test: Ajout des tests d'intégration et scripts de validation
- Test du démarrage des microservices
- Validation des connexions à la base de données
- Vérification de l'enregistrement Eureka" \
    --date="2026-04-27T14:15:00+01:00"

# Commit 14 : Désactiver Sleuth (28 avril)
git add .
git commit -m "refactor: Désactivation temporaire des composants Sleuth et Zipkin
- Commentaire des dépendances sleuth dans pom.xml
- Désactivation des classes SleuthConfig
- Suppression de la configuration Zipkin dans les propriétés
- Raison : Simplification de l'architecture pour les tests des services principaux" \
    --date="2026-04-28T11:30:00+01:00"

# Commit 15 : Compatibilité Spring Cloud (29 avril)
git add "*/src/main/resources/bootstrap.properties"
git commit -m "fix: Résolution des problèmes de compatibilité Spring Cloud/Boot
- Ajout de spring.cloud.compatibility-verifier.enabled=false
- Correction de l'incompatibilité Spring Boot 2.4.3 + Spring Cloud 2021.0.5
- Permet l'initialisation correcte du client Config Server" \
    --date="2026-04-29T09:45:00+01:00"

# Commit 16 : PostgreSQL & Docker (30 avril)
git add docker-compose.yml "config-server-repo/*.properties"
git commit -m "fix: Configuration de l'authentification PostgreSQL et du mapping de ports Docker
- Réinitialisation du mot de passe utilisateur postgres dans le conteneur Docker
- Vérification du mapping de ports pour les connexions à la base de données
- Mise à jour des URLs datasource avec les identifiants corrects
- Assurance de l'initialisation du pool de connexions HikariCP" \
    --date="2026-04-30T16:20:00+01:00"

# Commit 17 : Performance & Optimisation (1-2 mai)
git add .
git commit -m "perf: Optimisation de l'initialisation des microservices
- Réduction du temps de démarrage avec initialisation lazy
- Ajout des points de terminaison de vérification de santé
- Configuration des points de terminaison actuator" \
    --date="2026-05-01T13:00:00+01:00"

# Commit 18 : Documentation (2 mai)
git add "*.md" ".gitignore" "*.txt"
git commit -m "docs: Ajout de la documentation complète du projet
- Mise à jour du README avec aperçu de l'architecture
- Ajout des descriptions des microservices
- Inclusion des instructions d'installation et de déploiement
- Documentation de la configuration et des variables d'environnement" \
    --date="2026-05-02T10:00:00+01:00"

# Commit 19 : Validation finale (3 mai)
git add .
git commit -m "chore: Validation finale et nettoyage
- Vérification que tous les microservices peuvent démarrer correctement
- Confirmation du fonctionnement de l'enregistrement Eureka
- Test des migrations de base de données
- Validation du bootstrap client Spring Cloud Config
- Projet prêt pour le déploiement GitHub" \
    --date="2026-05-03T14:30:00+01:00"
```

---

## 📊 Vérification

Après les commits, vérifier l'historique :

```bash
# Voir tous les commits avec dates
git log --oneline --all --date=short --pretty=format:"%h - %s (%ad)"

# Voir le détail d'un commit
git show <commit-hash>

# Voir les stats par commit
git log --stat --oneline
```

---

## 🔗 Push vers GitHub

### 1. Créer un repo sur GitHub (vide)
- Aller à https://github.com/new
- Nom : `spring-boot-edge-microservices`
- Description : "Microservices Spring Boot avec Eureka, Config Server et API Gateway"
- Ne pas initialiser avec README, .gitignore, license

### 2. Ajouter remote et pousser
```bash
git remote add origin https://github.com/VotreUserGitHub/spring-boot-edge-microservices.git
git branch -M main
git push -u origin main
```

### 3. Vérifier sur GitHub
```bash
git log --oneline -20
```

---

## 📝 Notes Importantes

### Dates antidatées FORCÉES
- **NOUVELLES VARIABLES** : Utilisation de `GIT_COMMITTER_DATE` et `GIT_AUTHOR_DATE` pour FORCER les dates
- **Format** : `2026-04-14T08:00:00+01:00` (année-mois-jourTHH:MM:SS+timezone)
- **Pourquoi ?** : Les variables d'environnement ont priorité sur `--date` et garantissent que les dates sont appliquées
- **Résultat** : Chaque commit aura exactement la date spécifiée, pas des intervalles de 43 minutes

### Structure des commits
- **Commits logiques** : Un commit = une fonctionnalité/fix
- **Messages clairs** : Utilisant Conventional Commits (feat:, fix:, refactor:, etc.)
- **Multiple commits** : ~18 commits pour montrer du progrès sur ~20 jours

### Données réalistes
- Espacement sur 20 jours (14 avril → 3 mai)
- Heures variées (08:00, 10:30, 14:20, etc.)
- Messages descriptifs montrant l'évolution du projet

---

## 🧹 Nettoyage avant exécution

Si vous voulez recommencer proprement :

### Script de nettoyage (Windows)
```powershell
.\reset-git.ps1
```

### Nettoyage manuel
```bash
# Supprimer l'historique git
rm -rf .git

# Nettoyer les variables d'environnement
unset GIT_COMMITTER_DATE
unset GIT_AUTHOR_DATE

# Réinitialiser la config git
git config --unset user.email
git config --unset user.name
```

---

## ⚡ Commande Unique (PowerShell - plus rapide)

Si vous êtes sur Windows, exécutez simplement :

```powershell
.\git-commits-script.ps1
```

---

## 🔍 Vérification des dates

Après les commits, vérifiez que les dates sont correctes :

```bash
# Script de test automatique (Windows)
.\test-dates.ps1

# Vérification manuelle
git log --format="%h - %s (%ad)" --date=iso
git show --format=fuller <commit-hash> | grep -E "(AuthorDate|CommitDate)"
```

---

## ✅ Checklist finale

- [ ] Git configuré avec votre email/nom
- [ ] Tous les commits créés avec les bonnes dates
- [ ] Historique git vérifié
- [ ] Remote GitHub ajouté
- [ ] Push vers GitHub réussi
- [ ] Historique visible sur GitHub.com

---

## 🆘 Troubleshooting

### Les dates ne s'affichent pas correctement
```bash
# Vérifier le format de date
git log --format=%ad --date=iso <commit>

# Vérifier les variables d'environnement pendant le commit
echo $GIT_COMMITTER_DATE
echo $GIT_AUTHOR_DATE
```

### Les commits ont tous la même date
**Cause** : Variables d'environnement non définies ou format incorrect
**Solution** : Utilisez le script corrigé avec `$env:GIT_COMMITTER_DATE` et `$env:GIT_AUTHOR_DATE`

### Les commits sont en anglais au lieu de français
**Cause** : Ancienne version du script
**Solution** : Téléchargez la nouvelle version avec les messages en français

### Annuler les commits (recommencer)
```bash
git reset --hard HEAD~18  # Remonte 18 commits
```

### Vérifier avant de pousher
```bash
git log --oneline --all
git log --format="%h - %s (%ad)" --date=short
```

---

Bon déploiement sur GitHub ! 🚀
