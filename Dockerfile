FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
COPY target/news_agent-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]