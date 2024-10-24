FROM maven:3.8-eclipse-temurin-21-alpine as builder

WORKDIR /home

# Copy only the files needed to avoid putting all sorts of junk from your local env on to the image
COPY ./jpo-geojsonconverter/jpo-ode/pom.xml ./jpo-ode/
COPY ./jpo-geojsonconverter/jpo-ode/jpo-ode-common/pom.xml ./jpo-ode/jpo-ode-common/
COPY ./jpo-geojsonconverter/jpo-ode/jpo-ode-common/src ./jpo-ode/jpo-ode-common/src
COPY ./jpo-geojsonconverter/jpo-ode/jpo-ode-plugins/pom.xml ./jpo-ode/jpo-ode-plugins/
COPY ./jpo-geojsonconverter/jpo-ode/jpo-ode-plugins/src ./jpo-ode/jpo-ode-plugins/src
COPY ./jpo-geojsonconverter/jpo-ode/jpo-ode-core/pom.xml ./jpo-ode/jpo-ode-core/
COPY ./jpo-geojsonconverter/jpo-ode/jpo-ode-core/src ./jpo-ode/jpo-ode-core/src/
COPY ./jpo-geojsonconverter/jpo-ode/jpo-ode-svcs/pom.xml ./jpo-ode/jpo-ode-svcs/
COPY ./jpo-geojsonconverter/jpo-ode/jpo-ode-svcs/src ./jpo-ode/jpo-ode-svcs/src

COPY ./jpo-geojsonconverter/jpo-geojsonconverter/pom.xml ./jpo-geojsonconverter/
COPY ./jpo-geojsonconverter/jpo-geojsonconverter/src ./jpo-geojsonconverter/src

COPY ./jpo-conflictmonitor/pom.xml ./jpo-conflictmonitor/
COPY ./jpo-conflictmonitor/src ./jpo-conflictmonitor/src

WORKDIR /home/jpo-ode

RUN mvn install -DskipTests

WORKDIR /home/jpo-geojsonconverter

RUN mvn clean install -DskipTests

WORKDIR /home/jpo-conflictmonitor

RUN mvn clean package -DskipTests

FROM amazoncorretto:21

WORKDIR /home

COPY --from=builder /home/jpo-conflictmonitor/src/main/resources/application.yaml /home
COPY --from=builder /home/jpo-conflictmonitor/src/main/resources/logback.xml /home
COPY --from=builder /home/jpo-conflictmonitor/target/jpo-conflictmonitor.jar /home



ENTRYPOINT ["java", \
	"-Djava.rmi.server.hostname=$DOCKER_HOST_IP", \
	"-Dcom.sun.management.jmxremote.port=9090", \
	"-Dcom.sun.management.jmxremote.rmi.port=9090", \
	"-Dcom.sun.management.jmxremote", \
	"-Dcom.sun.management.jmxremote.local.only=true", \
	"-Dcom.sun.management.jmxremote.authenticate=false", \
	"-Dcom.sun.management.jmxremote.ssl=false", \
	"-Dlogback.configurationFile=/home/logback.xml", \
	"-jar", \
	"/home/jpo-conflictmonitor.jar"]

# ENTRYPOINT ["tail", "-f", "/dev/null"]
