FROM maven:3.8-eclipse-temurin-21-alpine AS builder

WORKDIR /home

ARG MAVEN_GITHUB_TOKEN
ARG MAVEN_GITHUB_ORG

ENV MAVEN_GITHUB_TOKEN=$MAVEN_GITHUB_TOKEN
ENV MAVEN_GITHUB_ORG=$MAVEN_GITHUB_ORG

COPY ./jpo-conflictmonitor/pom.xml ./jpo-conflictmonitor/
COPY ./settings.xml ./jpo-conflictmonitor/

# Download dependencies alone to cache them first
WORKDIR /home/jpo-conflictmonitor
RUN mvn -s settings.xml dependency:resolve

# Copy the source code and build the conflict monitor
COPY ./jpo-conflictmonitor/src ./src
RUN mvn -s settings.xml install -DskipTests

FROM amazoncorretto:21

WORKDIR /home

COPY --from=builder /home/jpo-conflictmonitor/src/main/resources/application.yaml /home
COPY --from=builder /home/jpo-conflictmonitor/src/main/resources/logback.xml /home
COPY --from=builder /home/jpo-conflictmonitor/target/jpo-conflictmonitor.jar /home



# Use jemalloc for RocksDB per Confluent recommendation:
# https://docs.confluent.io/platform/current/streams/developer-guide/memory-mgmt.html#rocksdb
RUN amazon-linux-extras install -y epel && \
     yum install -y jemalloc-devel
ENV LD_PRELOAD="/usr/lib64/libjemalloc.so"

# Entrypoint for prod: JMX not exposed.
# GC settings similar to Kafka recommendations, see: https://kafka.apache.org/documentation.html#java
# Set max Java heap usage as percentage of total available memory.
ENTRYPOINT ["java", \
	"-Dlogback.configurationFile=/home/logback.xml", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=20", \
    "-XX:InitiatingHeapOccupancyPercent=35", \
    "-XX:MetaspaceSize=96m", \
    "-XX:MinMetaspaceFreeRatio=50", \
    "-XX:MaxMetaspaceFreeRatio=80", \
    "-XX:+ExplicitGCInvokesConcurrent", \
    "-XX:InitialRAMPercentage=5.0", \
    "-XX:MaxRAMPercentage=50.0", \
	"-jar", \
	"/home/jpo-conflictmonitor.jar"]

# Entrypoint for testing: enables nonlocal JMX on port 10090
#ENTRYPOINT ["java", \
#	"-Dcom.sun.management.jmxremote=true", \
#	"-Dcom.sun.management.jmxremote.local.only=false", \
#	"-Dcom.sun.management.jmxremote.authenticate=false", \
#	"-Dcom.sun.management.jmxremote.ssl=false", \
#    "-Dcom.sun.management.jmxremote.port=10090", \
#    "-Dcom.sun.management.jmxremote.rmi.port=10090", \
#    "-Djava.rmi.server.hostname=localhost", \
#    "-Dlogback.configurationFile=/home/logback.xml", \
#    "-XX:+UseG1GC", \
#    "-XX:MaxGCPauseMillis=20", \
#    "-XX:InitiatingHeapOccupancyPercent=35", \
#    "-XX:MetaspaceSize=96m", \
#    "-XX:MinMetaspaceFreeRatio=50", \
#    "-XX:MaxMetaspaceFreeRatio=80", \
#    "-XX:+ExplicitGCInvokesConcurrent", \
#    "-XX:InitialRAMPercentage=5.0", \
#    "-XX:MaxRAMPercentage=50.0", \
#    "-jar", \
#	"/home/jpo-conflictmonitor.jar"]
