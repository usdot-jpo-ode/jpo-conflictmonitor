# bin/bash

echo "------------------------------------------"
echo "Kafka connector creation started."
echo "------------------------------------------"


declare -A OdeRawEncodedBSMJson=([name]="topic.OdeRawEncodedBSMJson" [collection]="OdeRawEncodedBSMJson"
    [timefield]="metadata.odeReceivedAt")
declare -A OdeBsmJson=([name]="topic.OdeBsmJson" [collection]="OdeBsmJson"
    [timefield]="")

declare -A OdeMapJson=([name]="topic.OdeMapJson" [collection]="OdeMapJson"
    [timefield]="")
declare -A ProcessedMap=([name]="topic.ProcessedMap" [collection]="ProcessedMap"
    [timefield]="")
declare -A OdeRawEncodedMAPJson=([name]="topic.OdeRawEncodedMAPJson" [collection]="OdeRawEncodedMAPJson"
    [timefield]="")

declare -A OdeRawEncodedSPATJson=([name]="topic.OdeRawEncodedSPATJson" [collection]="OdeRawEncodedSPATJson"
    [timefield]="")
declare -A OdeSpatJson=([name]="topic.OdeSpatJson" [collection]="OdeSpatJson"
    [timefield]="")
declare -A ProcessedSpat=([name]="topic.ProcessedSpat" [collection]="ProcessedSpat"
    [timefield]="")

declare -A CmSignalStopEvent=([name]="topic.CmSignalStopEvent" [collection]="CmSignalStopEvent"
    [timefield]="")
declare -A CmSignalStateConflictEvents=([name]="topic.CmSignalStateConflictEvents" [collection]="CmSignalStateConflictEvents"
    [timefield]="")
declare -A CmIntersectionReferenceAlignmentEvents=([name]="topic.CmIntersectionReferenceAlignmentEvents" [collection]="CmIntersectionReferenceAlignmentEvents"
    [timefield]="")
declare -A CmSignalGroupAlignmentEvents=([name]="topic.CmSignalGroupAlignmentEvents" [collection]="CmSignalGroupAlignmentEvents"
    [timefield]="")
declare -A CmConnectionOfTravelEvent=([name]="topic.CmConnectionOfTravelEvent" [collection]="CmConnectionOfTravelEvent"
    [timefield]="")
declare -A CmLaneDirectionOfTravelEvent=([name]="topic.CmLaneDirectionOfTravelEvent" [collection]="CmLaneDirectionOfTravelEvent"
    [timefield]="")
declare -A CmSignalStateEvent=([name]="topic.CmSignalStateEvent" [collection]="CmSignalStateEvent"
    [timefield]="")
declare -A CmSpatTimeChangeDetailsEvent=([name]="topic.CmSpatTimeChangeDetailsEvent" [collection]="CmSpatTimeChangeDetailsEvent"
    [timefield]="")
declare -A CmMapBroadcastRateEvents=([name]="topic.CmMapBroadcastRateEvents" [collection]="CmMapBroadcastRateEvents"
    [timefield]="")
declare -A CmLaneDirectionOfTravelAssessment=([name]="topic.CmLaneDirectionOfTravelAssessment" [collection]="CmLaneDirectionOfTravelAssessment"
    [timefield]="")
declare -A CmSpatBroadcastRateEvents=([name]="topic.CmSpatBroadcastRateEvents" [collection]="CmSpatBroadcastRateEvents"
    [timefield]="")

function createSink() {
    local -n topic=$1
    echo "Creating sink connector for:"
    for val in "${topic[@]}"; do echo $val; done

    local name=${topic[name]}
    local collection=${topic[collection]}
    local timefield=${topic[timefield]}

    echo "name=$name"
    echo "collection=$collection"
    echo "timefield=$timefield"

#
# Error handling for all sinks: don't stop on errors and write errors to a dead letter queue
# and include failed record header info.
# See https://docs.mongodb.com/kafka-connector/master/sink-connector/fundamentals/error-handling-strategies/
# and https://docs.confluent.io/platform/current/connect/concepts.html#dead-letter-queue
#
    local connectConfig=' {
        "group.id":"connector-consumer",
        "connector.class":"com.mongodb.kafka.connect.MongoSinkConnector",
        "tasks.max":3,
        "topics":"'$name'",
        "connection.uri":"mongodb://172.28.200.103:27017",
        "database":"ConflictMonitor",
        "collection":"'$collection'",
        "key.converter":"org.apache.kafka.connect.storage.StringConverter",
        "key.converter.schemas.enable":false,
        "value.converter":"org.apache.kafka.connect.json.JsonConverter",
        "value.converter.schemas.enable":false,
        "errors.tolerance": "all",
        "mongo.errors.tolerance": "all",
        "errors.deadletterqueue.topic.name": "dlq-'$collection'-sink",
        "errors.deadletterqueue.context.headers.enable": true,
        "errors.log.enable": true,
        "errors.log.include.messages": true,
        "errors.deadletterqueue.topic.replication.factor": 1
        }'    

        # "transforms": "TimestampConverter",
        # "transforms.TimestampConverter.field": "'$timefield'",
        # "transforms.TimestampConverter.type": "org.apache.kafka.connect.transforms.TimestampConverter$Value",
        # "transforms.TimestampConverter.target.type": "Timestamp"

    echo " Creating connector with Config : $connectConfig"

    curl -X PUT http://localhost:8083/connectors/mongo-sink-${name}/config -H "Content-Type: application/json" -d "$connectConfig"
}

createSink OdeRawEncodedBSMJson
createSink OdeBsmJson

createSink OdeMapJson
createSink ProcessedMap
createSink OdeRawEncodedMAPJson

createSink OdeRawEncodedSPATJson
createSink OdeSpatJson
createSink ProcessedSpat

createSink CmSignalStopEvent
createSink CmSignalStateConflictEvents
createSink CmIntersectionReferenceAlignmentEvents
createSink CmSignalGroupAlignmentEvents
createSink CmConnectionOfTravelEvent
createSink CmLaneDirectionOfTravelEvent
createSink CmSignalStateEvent
createSink CmSpatTimeChangeDetailsEvent
createSink CmMapBroadcastRateEvents
createSink CmLaneDirectionOfTravelAssessment
createSink CmSpatBroadcastRateEvents



echo "----------------------------------"
echo "Kafka connector creation complete!"
echo "----------------------------------"