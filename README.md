# Loyalty Services Sample Application

## Overview
Loyalty Services offers REST APIs for managing loyalty points for customers based on their purchases.

## Running loyalty service locally
Loyalty Services is a Spring Boot [3.0](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Release-Notes) application built using [Maven 3](https://spring.io/guides/gs/maven/). Kindly build a jar and run if from command line with [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or newer.
```
git clone https://github.com/sudarshanvrao/loyalty-service.git
cd loyalty-service
./mvnw package
java -jar target/*.jar
```
Application should be up at http://localhost:8080/

Or run from maven using an IDE
```
./mvnw spring-boot:run
```
## Building a Container

### Prerequisites  
Install [Docker](https://docs.docker.com/get-docker) 
### Steps
```
git clone https://github.com/sudarshanvrao/loyalty-service.git
cd loyalty-service
docker-compose up
```

## Project Dependencies
* Spring Web  
* Spring Data JPA  
* Spring-Actuator
* Springdoc-OpenAPI
* Jackson Datatype
* Lombok
* Spring Test
* Spring Boot Maven Plugin
* Jacoco Maven Plugin

## API Documentation/Actuator Links
[Swagger UI](http://localhost:8080/swagger-ui)  
[Postman Collection](https://github.com/sudarshanvrao/loyalty-service/blob/main/loyalty-service.postman_collection.json)  
[API Docs](http://localhost:8080/api-docs)  
[Health](http://localhost:8080/actuator/health) 

