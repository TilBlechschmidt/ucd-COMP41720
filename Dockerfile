# This is a multi-stage Dockerfile which first builds all sub-projects in one go (in Docker)
# and then builds one container image for one of the services based on a build arg.
# There are no local pre-requisites other than having Docker installed. No Maven or Java needed.

# --------------------------------- Builder image ---------------------------------
# Uses the public maven image with JDK8 to build all modules.
# Additionally makes use of a new and shiny Buildkit feature (`--mount=type=cache`)
# to cache dependency downloads for faster rebuilds and that sweet sweet DX :)

FROM maven:3.8.6-jdk-8 AS builder
WORKDIR /build
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean package -Dmaven.test.skip=true

# --------------------------------- Module image ----------------------------------
# Copies one of the module JAR files into a OpenJDK-JRE8 image and sets it as the entrypoint.
# The build argument MODULE should be one of client, broker, auldfellas, girlpower, dodgydrivers.

FROM openjdk:8-jre-alpine AS auldfellas

ARG MODULE
ARG VERSION=0.0.1

COPY --from=builder /build/$MODULE/target/$MODULE-$VERSION.jar /main.jar
ENTRYPOINT ["/usr/bin/java", "-jar", "/main.jar"]
