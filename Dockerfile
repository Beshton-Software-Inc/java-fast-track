# Use the official OpenJDK image as the base image
FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the target directory into the container
COPY target/springboot-crud-restful-webservices-0.0.1-SNAPSHOT.jar app.jar
# Expose the port that your Spring Boot application listens on
EXPOSE 8080

# Command to run your Spring Boot application
CMD ["java", "-jar", "app.jar"]