# ---------- Étape 1 : compiler l'application avec Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Télécharger les dépendances d'abord (mis en cache entre deux déploiements)
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# Compiler le projet
COPY src ./src
RUN mvn -q -B package -DskipTests

# ---------- Étape 2 : image légère qui lance l'application ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Limiter la mémoire Java pour l'offre gratuite (512 Mo)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
