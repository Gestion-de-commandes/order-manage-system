# Image de base Java 11 — même version que les autres microservices
FROM openjdk:11-jre-slim

# Dossier de travail dans le conteneur
WORKDIR /app

# Copie le JAR compilé dans le conteneur
# eureka-server-0.0.1-SNAPSHOT.jar = nom généré par Maven
COPY target/eureka-server-0.0.1-SNAPSHOT.jar /app/eureka-server.jar

# Commande pour démarrer le microservice
CMD ["java", "-jar", "/app/eureka-server.jar"]
