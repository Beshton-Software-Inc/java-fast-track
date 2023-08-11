# To build:
# docker build -t springboot-shopping .
# To run:
# docker run -p HOST_PORT:CONTAINER_PORT -d springboot-shopping
# e.g.: docker run -p 9090:8080 -d springboot-shopping
# Note that Springboot's default port is 8080
# Ref: https://www.docker.com/blog/kickstart-your-spring-boot-application-development/

FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
