FROM openjdk:11
COPY target/recipes-service-0.0.1.jar recipes-service.jar
EXPOSE 8080
ENTRYPOINT java -jar recipes-service.jar