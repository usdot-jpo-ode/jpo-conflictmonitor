// Create indexes on all collections

console.log("Creating indexes. This might take some time...");

const expire_seconds = 7884000; // 3 months


const collections = [
    {name: "OdeBsmJson", timefield: "none"}, 
    {name: "OdeMapJson", timefield: "none"},
    {name: "OdeSpatJson", timefield: "none"},

    {name: "CmSignalStateConflictEvents", timefield: "eventGeneratedAt"},
    {name: "CmIntersectionReferenceAlignmentEvents", timefield: "eventGeneratedAt"},
    {name: "CmSignalGroupAlignmentEvents", timefield: "eventGeneratedAt"},
    {name: "CmLaneDirectionOfTravelEvent", timefield: "eventGeneratedAt"},
    {name: "CmConnectionOfTravelEvent", timefield: "eventGeneratedAt"},
    {name: "CmSignalStateEvent", timefield: "eventGeneratedAt"},
    {name: "CmSpatTimeChangeDetailsEvent", timefield: "eventGeneratedAt"},
    {name: "CmMapBroadcastRateEvents", timefield: "eventGeneratedAt"},
    {name: "CmLaneDirectionOfTravelAssessment", timefield: "assessmentGeneratedAt"},
    {name: "CmSpatBroadcastRateEvents", timefield: "eventGeneratedAt"},
];

const collection_names_that_should_exist = collections.map(function(collection) {
  return collection.name;
});

// Wait for the collections to exist in mongo before trying to create indexes on them
let missing_collection_count;
do {
  missing_collection_count = 0;
  const collection_names = db.getCollectionNames();
  for (collection_name of collection_names_that_should_exist) {
    if (!collection_names.includes(collection_name)) {
      missing_collection_count++;
    }
  }
  if (missing_collection_count > 0) {
    print("Waiting for " + missing_collection_count + " collections to exist in mongo before creating indexes");
    sleep(1000);
  }
} while (missing_collection_count > 0);


console.log(
  "Creating TTL indexes to remove documents after " +
    expire_seconds +
    " seconds"
);

  
// Create TTL Indexes
for (const collection of collections) {
  if (collection.timefield !== "none"){
    try {
      var index_json = {};
      index_json[collection.timefield] = 1;
      db[collection.name].createIndex(index_json,
          { expireAfterSeconds: expire_seconds }
          );
      console.log("Created TTL index for " + collection.name + " using the field: " + collection.timefield + " as the timestamp");
    } catch (err) {
      var pattern_json = {};
      pattern_json[collection.timefield] = 1;
      db.runCommand({
        "collMod": collection.name,
        "index": {
          keyPattern: pattern_json,
          expireAfterSeconds: expire_seconds
        }
      });
      console.log("Updated TTL index for " + collection.name + " using the field: " + collection.timefield + " as the timestamp");
    }
  }
}

console.log("Finished Creating TTL's");