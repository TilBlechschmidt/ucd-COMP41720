# QuoCo | Your friendly neighbourhood quote provider

This repository contains a [quote broker](./broker/) which will query [multiple](./auldfellas/) [quote](./dodgydrivers/) [providers](./girlpower/)
and returns a culminated overview of all the available quotes. There is also a [client](./client/) available which requests and displays said quotes in a tabular view.

## Architecture overview

There are three quoting services and a broker which queries them. The broker is in turn invoked by the client. The modules communicate through the Akka actor framework. For more details see the [assignment sheet](Assignment.pdf).

## Usage

This time around there is only one working way to get this setup running. The note below explains what happened to the other ones.

### Maven

This method requires you to have Maven and Java installed on your machine. The commands required are as follows:

```bash
# 1. Build all the code
mvn clean install package

# 2. Launch the broker
mvn compile exec:java -pl broker

# 3. Run the quoting services (in different terminals)
BROKER=127.0.0.1:2551 mvn compile exec:java -pl auldfellas
BROKER=127.0.0.1:2551 mvn compile exec:java -pl girlpower
BROKER=127.0.0.1:2551 mvn compile exec:java -pl dodgydrivers

# 4. Run the client (in yet another terminal)
BROKER=127.0.0.1:2551 mvn compile exec:java -pl client
```

### A note regarding Docker

Even though it is not part of the assignment, I attempted to get the setup working in Docker. This was unfortunately unsuccessful, though the findings have been documented in the [Dockerfile](./Dockerfile) and might be of interest for anyone trying to get it working in the future.

## Service degradation

The broker waits until a pre-determined deadline is reached. Any responses received within this timeframe will be forwarded.
