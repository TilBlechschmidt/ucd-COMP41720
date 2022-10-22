# Unfortunately, we can use neither a multi-stage build nor build dependency caching
# like we did in previous iterations of this assignment.
#
# The reason for this is that Akka behaves differently when compiled into a JAR than it does
# when executed through `mvn exec:java`. I could not find any sufficient documentation on this
# to solve the problem but it appears to boil down to Akka not applying our config values on-top
# of the default values when bundled into a jar — something it _does_ while executed through `exec:java`.
#
# This is likely a production vs. dev mode kinda thing where they want to enforce you to think about
# the settings you use. However, despite chasing config error messages for half an hour, I sadly could not
# produce a fully-functioning configuration file. There are reference configs available online but they
# do not appear to match what Akka internally uses in dev mode.
#
# Additionally, it appears that Akka expects a lot of config keys to be present even if the corresponding
# feature has been disabled. This makes assembling an operational config file almost impossible without
# knowing how most features of Akka work. Something that is beyond the scope of this assignment.
#
# To work around this problem, we are just using a slow and inefficient but simple Dockerfile which
# runs the compilation during image-build time and uses the maven image for actual execution with `exec:java`.
# This is very crude and _should not_ be used in production but for this assignment it will do.
#
# NOTE: While this Dockerfile works, the Akka configuration included does not. Nodes are dropping messages
#       because they are for a non-local recipient. This is probably related to the Akka cluster setup which
#       in our case is not yet correct.
#
#       The reason for this appears to be that the `remote.classic.netty.tcp.hostname` key is not just there to
#       determine which interface to bind to but instead it is used to match against what appears to be the equivalent
#       of a `Host` header in HTTP. If it does not match, Akka will just drop the messages.
#       Since the assignment asks for a localhost+mvn compatible solution, the property files in this repo
#       are using localhost instead of the hostname that would be required in a Docker setup.
#
#       As this is not part of the assignment, I will not investigate further.
#       This file is included regardless as information for Rem in case he wants to go down this rabbit-hole.

FROM maven:3.8.6-jdk-8

WORKDIR /build
COPY . .
RUN mvn clean install -Dmaven.test.skip=true

ENTRYPOINT ["/usr/bin/mvn", "exec:java", "-pl"]
