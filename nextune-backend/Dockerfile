# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# copy pom trước để cache deps
COPY pom.xml .
RUN mvn -B dependency:go-offline

# copy source
COPY src ./src


RUN mvn -B clean package -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# copy jar build từ stage 1
COPY --from=builder /app/target/*.jar /app/app.jar

# cổng mặc định trong container (Spring sẽ đọc server.port từ env)
EXPOSE 8080

# cho phép truyền JAVA_OPTS từ bên ngoài (compose)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
