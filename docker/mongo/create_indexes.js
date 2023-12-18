// Create indexes on all collections

console.log("");
console.log("Running create_ttl_indexes.js");

const expire_seconds = 7884000; // 3 months
const retry_milliseconds = 10000;

const collections = [
    {name: "OdeBsmJson", timefield: "none", intersectionfield: "none"},
    {name: "OdeRawEncodedBSMJson", timefield: "none", intersectionfield: "none"},
    {name: "OdeMapJson", timefield: "none", intersectionfield: "none"},
    {name: "OdeSpatJson", timefield: "none", intersectionfield: "none"},
    {name: "ProcessedMap", timefield: "none", intersectionfield: "none"},
    {name: "ProcessedSpat", timefield: "none", intersectionfield: "none"},
    {name: "OdeRawEncodedMAPJson", timefield: "none", intersectionfield: "none"},

    { name: "CmStopLineStopEvent", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmStopLinePassageEvent", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmSignalStateConflictEvents", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmIntersectionReferenceAlignmentEvents", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmSignalGroupAlignmentEvents", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmConnectionOfTravelEvent", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmLaneDirectionOfTravelEvent", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmSignalStateEvent", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmSpatTimeChangeDetailsEvent", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmSpatMinimumDataEvents", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmMapBroadcastRateEvents", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmMapMinimumDataEvents", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmLaneDirectionOfTravelAssessment", timefield: "assessmentGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmConnectionOfTravelAssessment", timefield: "assessmentGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmSignalStateEventAssessment", timefield: "assessmentGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmSpatBroadcastRateEvents", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CMBsmEvents", timefield: "eventGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmSpatTimeChangeDetailsNotification", timefield: "notificationGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmLaneDirectionOfTravelNotification", timefield: "notificationGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmConnectionOfTravelNotification", timefield: "notificationGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmAppHealthNotifications", timefield: "notificationGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmSignalStateConflictNotification", timefield: "notificationGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmSignalGroupAlignmentNotification", timefield: "notificationGeneratedAt", intersectionfield: "intersectionID" },
    { name: "CmNotification", timefield: "notificationGeneratedAt", intersectionfield: "intersectionID" }
];



// Wait for the collections to exist in mongo before trying to create indexes on them
let missing_collection_count;
do {
    print("");
    try {
        missing_collection_count = 0;
        const collection_names = db.getCollectionNames();
        for (collection of collections) {
            
            // Create Collection if It doesn't exist
            let created = false;
            if(!collection_names.includes(collection.name)){
                created = createCollection(collection);
            }else{
                created = true;
            }

            if(created){
                if (collection.timefield !== 'none') {
                    createTTLIndex(collection);

                    if(collection.intersectionfield !== 'none'){
                        createTimeIntersectionIndex(collection)
                    }
                }
            }else{
                missing_collection_count++;
                console.log("Collection " + collection.name + " does not exist yet");
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


function createCollection(collection){
    try {
        db.createCollection(collection.name);
        return true;
    } catch (err) {
        console.log("Unable to Create Collection: " + collection.name);
        return false;
    }
}

// Create TTL Indexes
function createTTLIndex(collection) {
    if (ttlIndexExists(collection)) {
        console.log("TTL index already exists for " + collection.name);
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


function createTimeIntersectionIndex(collection){
    if(timeIntersectionIndexExists(collection)){
        // Skip if Index already Exists
        console.log("Time Intersection index already exists for " + collection.name);
        return;
    }


    const collection_name = collection.name;
    const timefield = collection.timefield;
    const intersection_field = collection.intersectionfield;

    console.log(
        "Creating Time Intersection index for " + collection_name + " to remove documents after " +
        expire_seconds +
        " seconds"
    );

    var index_json = {};
    index_json[timefield] = -1;
    index_json[intersection_field] = -1

    try {
        db[collection_name].createIndex(index_json);
        console.log("Created Time Intersection index for " + collection_name + " using the field: " + timefield + " as the timestamp");
    } catch (err) {
        db.runCommand({
            "collMod": collection_name,
            "index": {
                keyPattern: index_json
            }
        });
        console.log("Updated TTL index for " + collection_name + " using the field: " + timefield + " as the timestamp");
    }




}

function ttlIndexExists(collection) {
    return db[collection.name].getIndexes().find((idx) => idx.hasOwnProperty('expireAfterSeconds')) !== undefined;
}

function timeIntersectionIndexExists(collection){
    return db[collection.name].getIndexes().find((idx) => idx.name == collection.timefield + "_-1_" + collection.intersectionfield + "_-1") !== undefined;
}