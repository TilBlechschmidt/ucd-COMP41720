#!/bin/sh
# Simple script which runs the docker build command multiple times, once for each modules
# Useful if you do not intend to use docker compose.

# Common image name prefix
PREFIX=quoco-rmi

# Build the quotation services
docker build -t "$PREFIX"-girlpower --build-arg MODULE=girlpower .
docker build -t "$PREFIX"-auldfellas --build-arg MODULE=auldfellas .
docker build -t "$PREFIX"-dodgydrivers --build-arg MODULE=dodgydrivers .

# Build the broker and client
docker build -t "$PREFIX"-broker --build-arg MODULE=broker .
docker build -t "$PREFIX"-client --build-arg MODULE=client .
