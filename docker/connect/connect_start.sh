# bin/bash
DOCKER_HOST_IP = $1

echo "------------------------------------------"
echo "Kafka connector creation started."
echo "Provided Docker Host IP: $DOCKER_HOST_IP"
echo "------------------------------------------"




declare -A OdeRawEncodedBSMJson=([name]="topic.OdeRawEncodedBSMJson" [collection]="OdeRawEncodedBSMJson"
    [convert_timestamp]=false [timefield]="")
declare -A OdeBsmJson=([name]="topic.OdeBsmJson" [collection]="OdeBsmJson"
    [convert_timestamp]=false [timefield]="")

declare -A OdeMapJson=([name]="topic.OdeMapJson" [collection]="OdeMapJson"
    [convert_timestamp]=false [timefield]="")
declare -A ProcessedMap=([name]="topic.ProcessedMap" [collection]="ProcessedMap"
    [convert_timestamp]=false [timefield]="")
declare -A OdeRawEncodedMAPJson=([name]="topic.OdeRawEncodedMAPJson" [collection]="OdeRawEncodedMAPJson"
    [convert_timestamp]=false [timefield]="")

declare -A OdeRawEncodedSPATJson=([name]="topic.OdeRawEncodedSPATJson" [collection]="OdeRawEncodedSPATJson"
    [convert_timestamp]=false [timefield]="")
declare -A OdeSpatJson=([name]="topic.OdeSpatJson" [collection]="OdeSpatJson"
    [convert_timestamp]=false [timefield]="")
declare -A ProcessedSpat=([name]="topic.ProcessedSpat" [collection]="ProcessedSpat"
    [convert_timestamp]=false [timefield]="")

declare -A CmSignalStopEvent=([name]="topic.CmSignalStopEvent" [collection]="CmSignalStopEvent"
    [convert_timestamp]=false [timefield]="")
declare -A CmSignalStateConflictEvents=([name]="topic.CmSignalStateConflictEvents" [collection]="CmSignalStateConflictEvents"
    [convert_timestamp]=true [timefield]="eventGeneratedAt")
declare -A CmIntersectionReferenceAlignmentEvents=([name]="topic.CmIntersectionReferenceAlignmentEvents" [collection]="CmIntersectionReferenceAlignmentEvents"
    [convert_timestamp]=true [timefield]="eventGeneratedAt")
declare -A CmSignalGroupAlignmentEvents=([name]="topic.CmSignalGroupAlignmentEvents" [collection]="CmSignalGroupAlignmentEvents"
    [convert_timestamp]=true [timefield]="eventGeneratedAt")
declare -A CmConnectionOfTravelEvent=([name]="topic.CmConnectionOfTravelEvent" [collection]="CmConnectionOfTravelEvent"
    [convert_timestamp]=true [timefield]="eventGeneratedAt")
declare -A CmLaneDirectionOfTravelEvent=([name]="topic.CmLaneDirectionOfTravelEvent" [collection]="CmLaneDirectionOfTravelEvent"
    [convert_timestamp]=true [timefield]="eventGeneratedAt")
declare -A CmSignalStateEvent=([name]="topic.CmSignalStateEvent" [collection]="CmSignalStateEvent"
    [convert_timestamp]=true [timefield]="eventGeneratedAt")
declare -A CmSpatTimeChangeDetailsEvent=([name]="topic.CmSpatTimeChangeDetailsEvent" [collection]="CmSpatTimeChangeDetailsEvent"
    [convert_timestamp]=true [timefield]="eventGeneratedAt")
declare -A CmSpatMinimumDataEvents=([name]="topic.CmSpatMinimumDataEvents" [collection]="CmSpatMinimumDataEvents"
    [convert_timestamp]=false [timefield]="eventGeneratedAt")
declare -A CmMapBroadcastRateEvents=([name]="topic.CmMapBroadcastRateEvents" [collection]="CmMapBroadcastRateEvents"
    [convert_timestamp]=false [timefield]="eventGeneratedAt")
declare -A CmMapMinimumDataEvents=([name]="topic.CmMapMinimumDataEvents" [collection]="CmMapMinimumDataEvents"
    [convert_timestamp]=false [timefield]="eventGeneratedAt")
declare -A CMBsmEvents=([name]="topic.CMBsmEvents" [collection]="CMBsmEvents"
    [convert_timestamp]=false [timefield]="")
declare -A CmLaneDirectionOfTravelAssessment=([name]="topic.CmLaneDirectionOfTravelAssessment" [collection]="CmLaneDirectionOfTravelAssessment"
    [convert_timestamp]=false [timefield]="assessmentGeneratedAt")
declare -A CmLaneDirectionOfTravelAssessment=([name]="topic.CmConnectionOfTravelAssessment" [collection]="CmConnectionOfTravelAssessment"
    [convert_timestamp]=false [timefield]="assessmentGeneratedAt")
declare -A CmLaneDirectionOfTravelAssessment=([name]="topic.CmsignalStateEventAssessment" [collection]="CmsignalStateEventAssessment"
    [convert_timestamp]=false [timefield]="assessmentGeneratedAt")
declare -A CmSpatBroadcastRateEvents=([name]="topic.CmSpatBroadcastRateEvents" [collection]="CmSpatBroadcastRateEvents"
    [convert_timestamp]=false [timefield]="")
declare -A CmSpatTimeChangeDetailsNotification=([name]="topic.CmSpatTimeChangeDetailsNotification" [collection]="CmSpatTimeChangeDetailsNotification"
    [convert_timestamp]=false [timefield]="notificationGeneratedAt")
declare -A CmLaneDirectionOfTravelNotification=([name]="topic.CmLaneDirectionOfTravelNotification" [collection]="CmLaneDirectionOfTravelNotification"
    [convert_timestamp]=false [timefield]="notificationGeneratedAt")
declare -A CmConnectionOfTravelNotification=([name]="topic.CmConnectionOfTravelNotification" [collection]="CmConnectionOfTravelNotification"
    [convert_timestamp]=false [timefield]="notificationGeneratedAt")
declare -A CmAppHealthNotifications=([name]="topic.CmAppHealthNotifications" [collection]="CmAppHealthNotifications"
    [convert_timestamp]=false [timefield]="notificationGeneratedAt")
declare -A CmSignalStateConflictNotification=([name]="topic.CmSignalStateConflictNotification" [collection]="CmSignalStateConflictNotification"
    [convert_timestamp]=false [timefield]="notificationGeneratedAt")
declare -A CmSignalGroupAlignmentNotification=([name]="topic.CmSignalGroupAlignmentNotification" [collection]="CmSignalGroupAlignmentNotification"
    [convert_timestamp]=false [timefield]="notificationGeneratedAt")

function createSink() {
    local -n topic=$1
    echo "Creating sink connector for:"
    for val in "${topic[@]}"; do echo $val; done

    local name=${topic[name]}
    local collection=${topic[collection]}
    local timefield=${topic[timefield]}
    local convert_timestamp=${topic[convert_timestamp]}

    echo "name=$name"
    echo "collection=$collection"
    echo "timefield=$timefield"
    echo "convert_timestamp=$convert_timestamp"

    local connectConfig=' {
        "group.id":"connector-consumer",
        "connector.class":"com.mongodb.kafka.connect.MongoSinkConnector",
        "tasks.max":3,
        "topics":"'$name'",
        "connection.uri":"mongodb://'$DOCKER_HOST_IP':27017",
        "database":"ConflictMonitor",
        "collection":"'$collection'",
        "key.converter":"org.apache.kafka.connect.storage.StringConverter",
        "key.converter.schemas.enable":false,
        "value.converter":"org.apache.kafka.connect.json.JsonConverter",
        "value.converter.schemas.enable":false,
        "errors.tolerance": "all",
        "mongo.errors.tolerance": "all",
        "errors.deadletterqueue.topic.name": "dlq.'$collection'.sink",
        "errors.deadletterqueue.context.headers.enable": true,
        "errors.log.enable": true,
        "errors.log.include.messages": true,
        "errors.deadletterqueue.topic.replication.factor": 1'    


    if [ "$convert_timestamp" == true ]
    then
        local connectConfig=''$connectConfig',
        "transforms": "TimestampConverter",
        "transforms.TimestampConverter.field": "'$timefield'",
        "transforms.TimestampConverter.type": "org.apache.kafka.connect.transforms.TimestampConverter$Value",
        "transforms.TimestampConverter.target.type": "Timestamp"'
    fi

    local connectConfig=''$connectConfig' }'

    echo " Creating connector with Config : $connectConfig"

    curl -X PUT http://localhost:8083/connectors/MongoSink.${name}/config -H "Content-Type: application/json" -d "$connectConfig"
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
createSink CmSpatMinimumDataEvents
createSink CmMapBroadcastRateEvents
createSink CmMapMinimumDataEvents
createSink CmLaneDirectionOfTravelAssessment
createSink CmConnectionOfTravelAssessment
createSink CmsignalStateEventAssessment
createSink CmSpatBroadcastRateEvents
createSink CMBsmEvents

createSink CmSpatTimeChangeDetailsNotification
createSink CmLaneDirectionOfTravelNotification
createSink CmConnectionOfTravelNotification
createSink CmAppHealthNotifications
createSink CmSignalStateConflictNotification
createSink CmSignalGroupAlignmentNotification



echo "----------------------------------"
echo "Kafka connector creation complete!"
echo "----------------------------------"