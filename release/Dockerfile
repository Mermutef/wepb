FROM eclipse-temurin:21
COPY *-all.jar app.jar
COPY app.properties app.properties
COPY .env .env
ENTRYPOINT ["java", "-jar", "app.jar"]