-- ================================================================
-- init-db.sql
-- Exécuté automatiquement par PostgreSQL au premier démarrage
-- Crée toutes les bases de données du projet
-- ================================================================

-- Base existante pour les commandes
CREATE DATABASE db_commandes;

-- Base existante paour les produits  
CREATE DATABASE db_produits;

-- Nouvelle base pour l'authentification
CREATE DATABASE db_auth;

-- ----------------------------------------------------------------
-- Données initiales pour db_auth
-- On crée les rôles ROLE_USER et ROLE_ADMIN directement en SQL
-- pour ne pas avoir à les créer manuellement via Postman
-- ----------------------------------------------------------------
\c db_auth;

-- Ces tables seront créées par Hibernate au démarrage du microservice-auth
-- (spring.jpa.hibernate.ddl-auto=update)
-- Mais on insère les rôles après que Hibernate les a créées
-- via un script séparé si nécessaire.
-- Pour l'instant Hibernate + le code dans AuthController s'en charge.