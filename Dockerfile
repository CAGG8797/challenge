FROM openjdk:21-jdk-slim
WORKDIR /app
COPY . .

# Install maven
RUN apt-get update \
 && DEBIAN_FRONTEND=noninteractive \
    apt-get install -y maven \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

# Build the project
RUN mvn clean package

# Expose the port the application runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/api-0.0.1-SNAPSHOT.jar"]