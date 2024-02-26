// Create indexes on all collections

/*
This is the second script responsible for configuring mongoDB automatically on startup.
This script is responsible for creating collections, adding indexes and TTLs
For more information see the header in a_init_replicas.js
*/

console.log("");
console.log("Running create_indexes.js");

const expire_seconds = 7884000; // 3 months
const retry_milliseconds = 10000;

// name -> collection name
// ttlField -> field to perform ttl on 
// timeField -> field to index for time queries
// intersectionField -> field containing intersection id for id queries

const collections = [
    {name: "OdeBsmJson", ttlField: "recordGeneratedAt", timeField: "metadata.odeReceivedAt", intersectionField: "none"},
    {name: "OdeRawEncodedBSMJson", ttlField: "recordGeneratedAt", timeField: "none", intersectionField: "none"},
    {name: "OdeMapJson", ttlField: "recordGeneratedAt", timeField: "none", intersectionField: "none"},
    {name: "OdeSpatJson", ttlField: "recordGeneratedAt", timeField: "none", intersectionField: "none"},
    {name: "ProcessedMap", ttlField: "recordGeneratedAt", timeField: "properties.timeStamp", intersectionField: "properties.intersectionId"},
    {name: "ProcessedSpat", ttlField: "recordGeneratedAt", timeField: "odeReceivedAt", intersectionField: "intersectionId"},
    {name: "OdeRawEncodedMAPJson", ttlField: "recordGeneratedAt", timeField: "none", intersectionField: "none"},


    { name: "CmStopLineStopEvent", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmStopLinePassageEvent", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmIntersectionReferenceAlignmentEvents", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmSignalGroupAlignmentEvents", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmConnectionOfTravelEvent", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmLaneDirectionOfTravelEvent", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmSpatTimeChangeDetailsEvent", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmSpatMinimumDataEvents", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmMapBroadcastRateEvents", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmMapMinimumDataEvents", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmSpatBroadcastRateEvents", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },
    { name: "CMBsmEvents", ttlField: "eventGeneratedAt", timeField: "eventGeneratedAt", intersectionField: "intersectionID" },


    { name: "CmLaneDirectionOfTravelAssessment", ttlField: "assessmentGeneratedAt", timeField: "assessmentGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmConnectionOfTravelAssessment", ttlField: "assessmentGeneratedAt", timeField: "assessmentGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmSignalStateEventAssessment", ttlField: "assessmentGeneratedAt", timeField: "assessmentGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmStopLineStopAssessment", ttlField: "assessmentGeneratedAt", timeField: "assessmentGeneratedAt", intersectionField: "intersectionID" },
    
    
    { name: "CmSpatTimeChangeDetailsNotification", ttlField: "notificationGeneratedAt", timeField: "notificationGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmLaneDirectionOfTravelNotification", ttlField: "notificationGeneratedAt", timeField: "notificationGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmConnectionOfTravelNotification", ttlField: "notificationGeneratedAt", timeField: "notificationGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmAppHealthNotifications", ttlField: "notificationGeneratedAt", timeField: "notificationGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmSignalStateConflictNotification", ttlField: "notificationGeneratedAt", timeField: "notificationGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmSignalGroupAlignmentNotification", ttlField: "notificationGeneratedAt", timeField: "notificationGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmStopLinePassageNotification", ttlField: "notificationGeneratedAt", timeField: "notificationGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmStopLineStopNotification", ttlField: "notificationGeneratedAt", timeField: "notificationGeneratedAt", intersectionField: "intersectionID" },
    { name: "CmNotification", ttlField: "notificationGeneratedAt", timeField: "notificationGeneratedAt", intersectionField: "intersectionID" }
];

try{
    db.getMongo().setReadPref("primaryPreferred");
    db = db.getSiblingDB("ConflictMonitor");
    db.getMongo().setReadPref("primaryPreferred");
    var isMaster = db.isMaster();
    if (isMaster.primary) {
        print("Connected to the primary replica set member.");
    } else {
        print("Not connected to the primary replica set member. Current node: " + isMaster.host);
    }
} 
catch(err){
    console.log("Could not switch DB to Sibling DB");
    console.log(err);
}


// Wait for the collections to exist in mongo before trying to create indexes on them
let missing_collection_count;
do {
    print("");
    try {
        missing_collection_count = 0;
        const collection_names = db.getCollectionNames();
        for (collection of collections) {
            console.log("Creating Indexes for Collection" + collection);
            // Create Collection if It doesn't exist
            let created = false;
            if(!collection_names.includes(collection.name)){
                created = createCollection(collection);
                // created = true;
            }else{
                created = true;
            }

            if(created){
                if (collection.hasOwnProperty('ttlField') && collection.ttlField !== 'none') {
                    createTTLIndex(collection);  
                }

                if(collection.hasOwnProperty('timeField') && collection.timeField !== 'none'){
                    createTimeIntersectionIndex(collection)
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
        console.log("Error while setting up TTL indexs in collections");
        console.log(rs.status());
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
        console.log(err);
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
    const timeField = collection.ttlField;

    console.log(
        "Creating TTL index for " + collection_name + " to remove documents after " +
        expire_seconds +
        " seconds"
    );

    try {
        var index_json = {};
        index_json[timeField] = 1;
        db[collection_name].createIndex(index_json,
            {expireAfterSeconds: expire_seconds}
        );
        console.log("Created TTL index for " + collection_name + " using the field: " + timeField + " as the timestamp");
    } catch (err) {
        var pattern_json = {};
        pattern_json[timeField] = 1;
        db.runCommand({
            "collMod": collection_name,
            "index": {
                keyPattern: pattern_json,
                expireAfterSeconds: expire_seconds
            }
        });
        console.log("Updated TTL index for " + collection_name + " using the field: " + timeField + " as the timestamp");
    }

}


function createTimeIntersectionIndex(collection){
    if(timeIntersectionIndexExists(collection)){
        // Skip if Index already Exists
        console.log("Time Intersection index already exists for " + collection.name);
        return;
    }


    const collection_name = collection.name;
    const timeField = collection.timeField;

    console.log(
        "Creating Time Intersection index for " + collection_name + " to remove documents after " +
        expire_seconds +
        " seconds"
    );

    var index_json = {};
    index_json[timeField] = -1;

    if(collection.hasOwnProperty('intersectionField') && collection.intersectionField !== 'none'){
        index_json[collection.intersectionField] = -1
    }

    

    try {
        db[collection_name].createIndex(index_json);
        console.log("Created Time Intersection index for " + collection_name + " using the field: " + timeField + " as the timestamp");
    } catch (err) {
        db.runCommand({
            "collMod": collection_name,
            "index": {
                keyPattern: index_json
            }
        });
        console.log("Updated TTL index for " + collection_name + " using the field: " + timeField + " as the timestamp");
    }




}

function ttlIndexExists(collection) {
    return db[collection.name].getIndexes().find((idx) => idx.hasOwnProperty('expireAfterSeconds')) !== undefined;
}

function timeIntersectionIndexExists(collection){
    return db[collection.name].getIndexes().find((idx) => idx.name == collection.timeField + "_-1_" + collection.intersectionField + "_-1") !== undefined;
}
