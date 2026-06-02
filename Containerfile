FROM maven:3.8.6-openjdk-11 AS build

COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim

COPY --from=build /app/target/*.jar /app/uuid-as-a-service.jar

EXPOSE 8080

USER root

CMD ["java", "-jar", "/app/uuid-as-a-service.jar"]
