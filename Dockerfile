# Étape 1 : Utiliser une image de base Maven pour construire le projet
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app

# Copier le fichier pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier tout le code source et le compiler
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Utiliser une image légère de Java pour exécuter l'application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copier l'artefact .jar depuis l'étape de build
COPY --from=build /app/target/*.jar /app/app.jar

# Copier tous les fichiers de ressources nécessaires dans l'image finale
COPY --from=build /app/src/main/resources /app/src/main/resources

# Exposer le port sur lequel votre application va tourner (par défaut 8080 pour Spring Boot)
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
