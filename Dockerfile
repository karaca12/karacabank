# Temel görüntüyü alın
FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

