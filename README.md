# recipes-service

This service provides APIs for working with user's recipes. The API spec can be found in `api-specs.yml` file. Because of free GitHub free version open the file in local IDE to render it. 

The architecture decisions are explained in the `architecture.md` file

A collection of test API calls can be found in `recipe-service.postman_collection.json`, use postman to import the collections

## Prerequisites

You need:
* Maven 3.6+
* Docker and make sure is running
* Java 11

## Installation

To build the jar file

```maven
mvn clean install
```

## Usage

To run with local configuration

```docker
docker-compose up --build
```
The service will be available on `http://localhost:8080`

Note: 
 A MongoDB instance should be available at `localhost:27017`.

