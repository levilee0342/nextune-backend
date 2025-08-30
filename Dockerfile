# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# cache deps
COPY nextune-backend/pom.xml .
RUN mvn -B dependency:go-offline

# copy source
COPY nextune-backend .
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG JAR_PATH=/app/target
COPY --from=builder ${JAR_PATH}/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
