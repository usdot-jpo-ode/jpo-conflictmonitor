// Create indexes on all collections

console.log("");
console.log("Running create_ttl_indexes.js");

const expire_seconds = 7884000; // 3 months
const retry_milliseconds = 10000;

const collections = [
    {name: "OdeBsmJson", timefield: "none"},
    {name: "OdeRawEncodedBSMJson", timefield: "none"},
    {name: "OdeMapJson", timefield: "none"},
    {name: "OdeSpatJson", timefield: "none"},
    {name: "ProcessedMap", timefield: "none"},
    {name: "ProcessedSpat", timefield: "none"},
    {name: "OdeRawEncodedMAPJson", timefield: "none"},

    { name: "CmStopLineStopEvent", timefield: "eventGeneratedAt" },
    { name: "CmStopLinePassageEvent", timefield: "eventGeneratedAt" },
    { name: "CmSignalStateConflictEvents", timefield: "eventGeneratedAt" },
    { name: "CmIntersectionReferenceAlignmentEvents", timefield: "eventGeneratedAt" },
    { name: "CmSignalGroupAlignmentEvents", timefield: "eventGeneratedAt" },
    { name: "CmConnectionOfTravelEvent", timefield: "eventGeneratedAt" },
    { name: "CmLaneDirectionOfTravelEvent", timefield: "eventGeneratedAt" },
    { name: "CmSignalStateEvent", timefield: "eventGeneratedAt" },
    { name: "CmSpatTimeChangeDetailsEvent", timefield: "eventGeneratedAt" },
    { name: "CmSpatMinimumDataEvents", timefield: "eventGeneratedAt" },
    { name: "CmMapBroadcastRateEvents", timefield: "eventGeneratedAt" },
    { name: "CmMapMinimumDataEvents", timefield: "eventGeneratedAt" },
    { name: "CmLaneDirectionOfTravelAssessment", timefield: "assessmentGeneratedAt" },
    { name: "CmConnectionOfTravelAssessment", timefield: "assessmentGeneratedAt" },
    { name: "CmSignalStateEventAssessment", timefield: "assessmentGeneratedAt" },
    { name: "CmSpatBroadcastRateEvents", timefield: "eventGeneratedAt" },
    { name: "CMBsmEvents", timefield: "eventGeneratedAt" },
    { name: "CmSpatTimeChangeDetailsNotification", timefield: "notificationGeneratedAt" },
    { name: "CmLaneDirectionOfTravelNotification", timefield: "notificationGeneratedAt" },
    { name: "CmConnectionOfTravelNotification", timefield: "notificationGeneratedAt" },
    { name: "CmAppHealthNotifications", timefield: "notificationGeneratedAt" },
    { name: "CmSignalStateConflictNotification", timefield: "notificationGeneratedAt" },
    { name: "CmSignalGroupAlignmentNotification", timefield: "notificationGeneratedAt" },
    { name: "CmNotification", timefield: "notificationGeneratedAt" }
];



// Wait for the collections to exist in mongo before trying to create indexes on them
let missing_collection_count;
do {
    print("");
    try {
        missing_collection_count = 0;
        const collection_names = db.getCollectionNames();
        for (collection of collections) {
            if (collection.timefield !== 'none') {
                if (collection_names.includes(collection.name)) {
                    createIndex(collection);
                } else {
                    missing_collection_count++;
                    console.log("Collection " + collection.name + " does not exist yet");
                }
            }
        }
        if (missing_collection_count > 0) {
            print("Waiting on " + missing_collection_count + " collections to be created...will try again in " + retry_milliseconds + " ms");
            sleep(retry_milliseconds);
        }
    } catch (err) {
        console.error(err);
        sleep(retry_milliseconds);
    }
} while (missing_collection_count > 0);

console.log("Finished Creating All TTL indexes");

// Create TTL Indexes
function createIndex(collection) {
    if (indexExists(collection)) {
        //console.log("TTL index already exists for " + collection.name);
        return;
    }

    const collection_name = collection.name;
    const timefield = collection.timefield;

    console.log(
        "Creating TTL index for " + collection_name + " to remove documents after " +
        expire_seconds +
        " seconds"
    );

    try {
        var index_json = {};
        index_json[timefield] = 1;
        db[collection_name].createIndex(index_json,
            {expireAfterSeconds: expire_seconds}
        );
        console.log("Created TTL index for " + collection_name + " using the field: " + timefield + " as the timestamp");
    } catch (err) {
        var pattern_json = {};
        pattern_json[timefield] = 1;
        db.runCommand({
            "collMod": collection_name,
            "index": {
                keyPattern: pattern_json,
                expireAfterSeconds: expire_seconds
            }
        });
        console.log("Updated TTL index for " + collection_name + " using the field: " + timefield + " as the timestamp");
    }

}

function indexExists(collection) {
    return db[collection.name].getIndexes().find((idx) => idx.name.startsWith(collection.timefield)) !== undefined;
}