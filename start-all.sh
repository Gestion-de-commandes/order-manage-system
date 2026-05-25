#!/bin/bash
# ================================================================
# start-all.sh — Lance tous les microservices dans le bon ordre
# Usage : ./start-all.sh
# ================================================================

BASE=~/Documents/campus/2emeSemestre/intergiciel/projet_final/order-manage-system

echo "🐳 Démarrage Docker (PostgreSQL + RabbitMQ)..."
docker start postgres-db rabbitmq
sleep 5

echo "⚙️  Démarrage Config Server..."
cd $BASE/config-server && mvn spring-boot:run &
sleep 15

echo "🔍 Démarrage Eureka..."
cd $BASE/eureka-server && mvn spring-boot:run &
sleep 15

echo "🔐 Démarrage microservice-auth..."
cd $BASE/microservice-auth && mvn spring-boot:run &
sleep 10

echo "🌐 Démarrage Gateway..."
cd $BASE/gateway-server && mvn spring-boot:run &
sleep 10

echo "📦 Démarrage microservice-produits..."
cd $BASE/microservice-produits && mvn spring-boot:run &
sleep 8

echo "📋 Démarrage microservice-commandes..."
cd $BASE/microservice-commandes && mvn spring-boot:run &
sleep 8

echo "💳 Démarrage microservice-paiement..."
cd $BASE/microservice-paiement && mvn spring-boot:run &
sleep 8

echo "🖥️  Démarrage microservice-clientui..."
cd $BASE/microservice-clientui && mvn spring-boot:run &

echo ""
echo "✅ Tous les services démarrent en arrière-plan !"
echo "📊 Eureka : http://localhost:9201"
echo "🔐 Auth   : http://localhost:8085/swagger-ui/index.html"
echo "🖥️  Client : http://localhost:8082"