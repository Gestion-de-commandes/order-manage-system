FROM openjdk:11-jre-slim

WORKDIR /app

# Correction : le vrai nom du JAR généré par Maven
# Format : artifactId-version.jar
# artifactId = "config-server" (dans pom.xml)
# version = "0.0.1-SNAPSHOT" (dans pom.xml)
COPY target/config-server-0.0.1-SNAPSHOT.jar /app/config-server.jar

CMD ["java", "-jar", "/app/config-server.jar"]
