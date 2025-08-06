# ---------- Build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .


RUN ./mvnw dependency:go-offline -B
COPY src src

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:21-jre
WORKDIR /app


COPY --from=builder /app/target/*.jar app.jar


EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]