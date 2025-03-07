# Technical Challenge

## Prerequisites

- Docker
- Docker Compose
- Java 21
- Maven

## Building the Application

1. Clone the repository:
    ```sh
    git clone https://github.com/CAGG8797/challenge.git
    cd ./challenge
    ```

2. Build the application using Maven:
    ```sh
    mvn clean package
    ```

## Running the Application

1. Start the application using Docker Compose:
    ```sh
    docker-compose up --build
    ```

2. The application will be available at `http://localhost:8080`.

## Testing the Application

1. Run the tests using Maven:
    ```sh
    mvn test
    ```

## Configuration

The application configuration is located in `src/main/resources/application.yaml`. You can modify the database connection settings and other properties as needed.

## Docker Configuration

- `docker-compose.yaml`: Defines the services, including the PostgreSQL database and the Spring Boot application.
- `Dockerfile`: Builds the Docker image for the Spring Boot application.

## Swagger Documentation

The API documentation is available through Swagger. You can access it using the following links:

- [Swagger UI](http://localhost:8080/swagger-ui.html): Interactive API documentation.
- [OpenAPI JSON](http://localhost:8080/v3/api-docs): Raw OpenAPI JSON documentation.


## Additional Information

- The PostgreSQL database is configured to persist data in a Docker volume named `postgres_data`.
- The application connects to the PostgreSQL database using the environment variables defined in the `docker-compose.yaml` file.