# QuoCo | Your friendly neighbourhood quote provider

This repository contains a [quote broker](./broker/) which will query [multiple](./auldfellas/) [quote](./dodgydrivers/) [providers](./girlpower/) discovered via mDNS
and returns a culminated overview of all the available quotes. There is also a [client](./client/) available which requests and displays said quotes in a tabular view.

## Architecture overview

There are three quoting services and a broker which queries them. The broker is in turn invoked by the client. The broker discovers quotation services through mDNS and looks for the `_quocows._tcp.local.` type. The client does not use mDNS and instead takes the broker URL directly as a CLI argument or via the `BROKER` environment variable.

![Architecture overview](./architecture.jpeg)

## Usage

There are multiple different ways in which you run the quoting service, detailed below ranking from simplest to hardest.

### Docker Compose

If you have a reasonably recent version of Docker installed, you can start all the modules including the client with a single command:

```bash
docker compose up --build
```

Note that there is a potential race condition which may result in the client starting before all the quoting services are connected. For more details on the problem and how to resolve it, see the [docker-compose.yml](./docker-compose.yml).

### Docker

You can build and run the images manually using the following commands:

```bash
# Builds all images with a `quoco-ws-` prefix
./build-images.sh

# Create a network for the containers
docker network create quoco-ws

# Run these commands in separate terminals
docker run --rm -it --network quoco-ws --name broker quoco-ws-broker
docker run --rm -it --network quoco-ws quoco-ws-auldfellas
docker run --rm -it --network quoco-ws quoco-ws-dodgydrivers
docker run --rm -it --network quoco-ws quoco-ws-girlpower

docker run --rm -it --network quoco-ws -e BROKER=http://broker:9000/quotations quoco-ws-client
```

### Maven

This method requires you to have Maven and Java 8 installed on your machine. The commands required are as follows:

```bash
# 1. Build all the code
mvn clean install package

# 2. Launch the broker
mvn exec:java -pl broker

# 3. Connect the quoting services (in different terminals)
QUOTER_PORT=9001 mvn exec:java -pl auldfellas
QUOTER_PORT=9002 mvn exec:java -pl dodgydrivers
QUOTER_PORT=9003 mvn exec:java -pl girlpower

# 4. Run the client (in yet another terminal)
BROKER=http://localhost:9000/quotations mvn exec:java -pl client
```

## Service degradation

When a quoting service is registered but does not reply or is "gone", the broker service will print an error and fall back to just returning the quotes that are available.
