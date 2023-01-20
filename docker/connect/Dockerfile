FROM confluentinc/cp-kafka-connect:latest

COPY connect_start.sh /scripts/connect_start.sh

RUN confluent-hub install --no-prompt mongodb/kafka-connect-mongodb:latest
RUN confluent-hub install --no-prompt confluentinc/connect-transforms:latest