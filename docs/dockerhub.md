# jpo-conflictmonitor

## GitHub Repository Link
https://github.com/usdot-jpo-ode/jpo-conflictmonitor

## Purpose
The JPO Conflict Monitor is a real time validation system to verify corresponding SPAT, MAP and BSM messages.

## How to pull the latest image
The latest image can be pulled using the following command:
> docker pull usdotjpoode/jpo-conflictmonitor:develop

## Required environment variables
	• DOCKER_HOST_IP
	• KAFKA_BOOTSTRAP_SERVERS
	• CONNECT_URL

## Direct Dependencies
	- MongoDB
	- Kafka Connect

## Indirect Dependencies
- The ODE must be running in order for the Conflict Monitor to consume BSM data to process.
- The Geojson Converter must be running in order for the Conflict Monitor to consume MAP & SPAT data to process.

## Example docker-compose.yml with direct dependencies:
```
version: '3.9'
services:
  conflictmonitor:
    image: usdotjpoode/jpo-conflictmonitor:latest
    restart: always
    depends_on:
      mongodb_container:
        condition: service_healthy
      connect:
        condition: service_healthy
    ports:
      - "8082:8082"
    environment:
      DOCKER_HOST_IP: ${DOCKER_HOST_IP:?error}
      KAFKA_BOOTSTRAP_SERVERS: ${KAFKA_BOOTSTRAP_SERVERS:?error}
      CONNECT_URL: ${CONNECT_URL:?error}
      spring.kafka.bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:?error}
    logging:
      options:
        max-size: "10m"
        max-file: "5"
    deploy:
      resources:
        limits:
          memory: 3G

  mongodb_container:
    image: mongo:6
    container_name: jpo-conflictmonitor-mongodb-container
    restart: always
    environment:
      - MONGO_REPLICA_SET_NAME=rs0
      - MONGO_IP=${MONGO_IP}
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data_container:/data/db
    healthcheck:
      test: | 
        test $$(mongosh --quiet --eval "try { rs.initiate({ _id: 'rs0', members: [{ _id: 0, host: '${MONGO_IP}' }] }).ok } catch (_) { rs.status().ok }") -eq 1
      interval: 10s
      start_period: 30s
    command: ["--replSet", "rs0", "--bind_ip_all"]
    logging:
      options:
        max-size: "10m"
        max-file: "5"
    deploy:
      resources:
        limits:
          memory: 3G

  connect:
    image: cp-kafka-connect:6.1.9
    build:
      context: ./docker/connect
      dockerfile: Dockerfile
    container_name: jpo-conflictmonitor-kafka-connect
    restart: always
    ports:
      - "8083:8083"
    depends_on:
      mongodb_container:
        condition: service_healthy
    environment:
      DOCKER_HOST_IP: ${DOCKER_HOST_IP}
      MONGO_IP: ${MONGO_IP}
      CONNECT_BOOTSTRAP_SERVERS: ${KAFKA_BROKER_IP}:9092
      CONNECT_REST_ADVERTISED_HOST_NAME: connect
      CONNECT_REST_PORT: 8083
      CONNECT_GROUP_ID: compose-connect-group
      CONNECT_CONFIG_STORAGE_TOPIC: CmConnectConfigs
      CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR: 1
      CONNECT_CONFIG_STORAGE_CLEANUP_POLICY: compact
      CONNECT_OFFSET_FLUSH_INTERVAL_MS: 10000
      CONNECT_OFFSET_STORAGE_TOPIC: CmConnectOffsets
      CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR: 1
      CONNECT_OFFSET_STORAGE_CLEANUP_POLICY: compact
      CONNECT_STATUS_STORAGE_TOPIC: CmConnectStatus
      CONNECT_STATUS_STORAGE_CLEANUP_POLICY: compact
      CONNECT_STATUS_STORAGE_REPLICATION_FACTOR: 1
      CONNECT_KEY_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_VALUE_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_INTERNAL_KEY_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_INTERNAL_VALUE_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_LOG4J_ROOT_LOGLEVEL: "ERROR"
      CONNECT_LOG4J_LOGGERS: "org.apache.kafka.connect.runtime.rest=ERROR,org.reflections=ERROR,com.mongodb.kafka=ERROR"
      CONNECT_PLUGIN_PATH: /usr/share/confluent-hub-components
      CONNECT_ZOOKEEPER_CONNECT: "zookeeper:2181"
    logging:
      options:
        max-size: "10m"
        max-file: "5"
    command:
      - bash
      - -c
      - |
        /etc/confluent/docker/run & 
        echo "Waiting for Kafka Connect to start listening on kafka-connect ❳"
        while [ $$(curl -s -o /dev/null -w %{http_code} http://${KAFKA_CONNECT_IP}:8083/connectors) -eq 000 ] ; do 
          echo -e $$(date) " Kafka Connect listener HTTP state: " $$(curl -s -o /dev/null -w %{http_code} http://${KAFKA_CONNECT_IP}:8083/connectors) " (waiting for 200)"
          sleep 5
        done
        sleep 10
        echo -e "\n--\n+> Creating Kafka Connect MongoDB sink"
        bash /scripts/connect_start.sh "mongodb://${DOCKER_HOST_IP}:27017/?replicaSet=rs0"
        sleep infinity
    deploy:
      resources:
        limits:
          memory: 3G
volumes:
  mongodb_data_container:
```

## Expected startup output
The latest logs should look something like this:
```
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmSignalStateConflictNotification, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmSignalGroupAlignmentNotification, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmIntersectionReferenceAlignmentNotification, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmLaneDirectionOfTravelAssessment, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact, retention.ms=300000})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmConnectionOfTravelAssessment, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact, retention.ms=300000})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmDefaultConfig, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmIntersectionConfig, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=delete, retention.ms=3000})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmIntersectionConfigTable, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmNotification, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmMapBoundingBox, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmEvent, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmAssessment, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmStopLineStopAssessment, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
jpo-conflictmonitor-conflictmonitor-1  | 2023-11-10 19:27:30 [main] INFO  KafkaConfiguration - New Topic: (name=topic.CmStopLineStopNotification, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs={cleanup.policy=compact})
```