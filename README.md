# QuoCo | Your friendly neighbourhood quote provider

This repository contains a [quote broker](./broker/) which will query [multiple](./auldfellas/) [quote](./dodgydrivers/) [providers](./girlpower/)
and returns a culminated overview of all the available quotes. There is also a [client](./client/) available which requests and displays said quotes in a tabular view.

## Architecture overview

There are three quoting services and a broker which queries them. The broker is in turn invoked by the client. The modules communicate through a RESTful HTTP interface built on-top of Spring Boot. For more details see the [assignment sheet](Assignment.pdf).

![Architecture overview](./architecture.jpeg)

## Usage

There are multiple different ways in which you run the quoting service, detailed below ranking from simplest to hardest.

### Docker Compose

If you have a reasonably recent version of Docker installed, you can start all the modules including the client with a single command:

```bash
docker compose up --build
```

The start of the client has been delayed by a couple of seconds to allow all the other components to start up first. Technically Docker health-checks could have been used but this is the simpler solution for demonstration purposes.

### Docker

You can build and run the images manually using the following commands:

```bash
# Builds all images with a `quoco-rest-` prefix
./build-images.sh

# Create a network for the containers
docker network create quoco-rest

# Run these commands in separate terminals
docker run --rm -it --network quoco-rest --name broker -e BROKER_ENDPOINTS=http://auldfellas:8080/quotations,http://dodgydrivers:8080/quotations,http://girlpower:8080/quotations quoco-rest-broker
docker run --rm -it --network quoco-rest --name auldfellas quoco-rest-auldfellas
docker run --rm -it --network quoco-rest --name dodgydrivers quoco-rest-dodgydrivers
docker run --rm -it --network quoco-rest --name girlpower quoco-rest-girlpower

docker run --rm -it --network quoco-rest -e BROKER=http://broker:8080/applications quoco-rest-client
```

### Maven

This method requires you to have Maven and Java installed on your machine. The commands required are as follows:

```bash
# 1. Build all the code
mvn clean install package

# 2. Launch the broker
java -jar broker/target/broker-0.0.1.jar --broker.endpoints=http://localhost:8081/quotations,http://localhost:8082/quotations,http://localhost:8083/quotations

# 3. Run the quoting services (in different terminals)
java -jar auldfellas/target/auldfellas-0.0.1.jar --server.port=8081
java -jar girlpower/target/girlpower-0.0.1.jar  --server.port=8082
java -jar dodgydrivers/target/dodgydrivers-0.0.1.jar  --server.port=8083

# 4. Run the client (in yet another terminal)
java -jar client/target/client-0.0.1-jar-with-dependencies.jar http://localhost:8080/applications
```

## Service degradation

The broker will attempt one request per quoter service for each application. If this request fails, an error will be logged and only the successfully generated quotes will be returned.