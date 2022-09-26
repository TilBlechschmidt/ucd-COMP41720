# QuoCo | Your friendly neighbourhood quotation provider

This repository contains a [quotation broker](./broker/) which will query [multiple](./auldfellas/) [quotation](./dodgydrivers/) [providers](./girlpower/)
and returns a culminated overview of all the available quotations. There is also a [client](./client/) available which requests and displays said quotations in a tabular view.

## Architecture overview

There are three quotation services and a broker which queries them. The broker is in turn invoked by the client. All modules communicate through the Java Remote Invocation (`java.rmi`) interface and any module except for the client can both bind to or host a registry (see usage information below).

TODO INSERT ARCH OVERVIEW PICTURE

## Usage

There are multiple different ways in which you run the quotation service, detailed below ranking from simplest to hardest.

### Docker Compose

If you have a reasonably recent version of Docker installed, you can start all the modules including the client with a single command:

```bash
docker compose up --build
```

Note that there is a potential race condition which may result in the client starting before all the quotation services are connected. For more details on the problem and how to resolve it, see the [docker-compose.yml](./docker-compose.yml).

### Docker

You can build and run the images manually using the following commands:

```bash
# Builds all images with a `quoco-rmi-` prefix
./build-images.sh

# Create a network for the containers
docker network create quoco-rmi

# Run these commands in separate terminals
docker run --rm -it --network quoco-rmi --name broker quoco-rmi-broker
docker run --rm -it --network quoco-rmi -e REGISTRY=broker quoco-rmi-auldfellas
docker run --rm -it --network quoco-rmi -e REGISTRY=broker quoco-rmi-dodgydrivers
docker run --rm -it --network quoco-rmi -e REGISTRY=broker quoco-rmi-girlpower

docker run --rm -it --network quoco-rmi -e REGISTRY=broker quoco-rmi-client
```

### Maven

This method requires you to have Maven and Java 8 installed on your machine. The commands required are as follows:

```bash
# 1. Build all the code
mvn clean install package

# 2. Launch the broker
mvn exec:java -pl broker

# 3. Connect the quotation services (in different terminals)
REGISTRY=localhost mvn exec:java -pl auldfellas
REGISTRY=localhost mvn exec:java -pl dodgydrivers
REGISTRY=localhost mvn exec:java -pl girlpower

# 4. Run the client (in yet another terminal)
REGISTRY=localhost mvn exec:java -pl client
```

## Service degradation

When a quotation service is registered but does not reply or is "gone", the broker service will print an error and fall back to just returning the quotations that are available.
