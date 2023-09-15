// Create indexes on all collections

console.log("Creating indexes. This might take some time...");

const expire_seconds = 300; // 3 months

// const expire_seconds = 90;

console.log(
  "Creating TTL indexes to remove documents after " +
    expire_seconds +
    " seconds"
);

var odettlCollections = [
  "OdeBsmJson",
  "OdeMapJson",
  "OdeSpatJson",
];

for (const collection of odettlCollections) {
  try {
    db[collection].createIndex(
      { "metadata.odeReceivedAt": 1 },
      { expireAfterSeconds: expire_seconds }
    );
    console.log("Created TTL index for " + collection + " on metadata.odeReceivedAt's timestamp");
  } catch (err) {
    db.runCommand({
      "collMod": collection,
      "index": {
        keyPattern: { "metadata.odeReceivedAt": 1 },
        expireAfterSeconds: expire_seconds
      }
    });
    console.log("Updated TTL index for " + collection + " on metadata.odeReceivedAt's timestamp");
  }
}

console.log("Created ODE TTL's");


var cmttlCollections = [
  "CmSignalStopEvent",
  "CmSignalStateConflictEvents",
  "CmIntersectionReferenceAlignmentEvents",
  "CmSignalGroupAlignmentEvents",
  "CmConnectionOfTravelEvent",
  "CmLaneDirectionOfTravelEvent",
  "CmSignalStateEvent",
  "CmSpatTimeChangeDetailsEvent",
  "CmMapBroadcastRateEvents",
  "CmLaneDirectionOfTravelAssessment",
  "CmSpatBroadcastRateEvents",
];

for (const collection of cmttlCollections) {
  try {
    db[collection].createIndex(
      { eventGeneratedAt: 1 },
      { expireAfterSeconds: expire_seconds }
    );
    console.log("Created TTL index for " + collection + " on eventGeneratedAt's timestamp");
  } catch (err) {
    db.runCommand({
      "collMod": collection,
      "index": {
        keyPattern: { eventGeneratedAt: 1 },
        expireAfterSeconds: expire_seconds
      }
    });
    console.log("Updated TTL index for " + collection + " on eventGeneratedAt's timestamp");
  }

}

console.log("Created ConflictMonitor TTL's");


console.log("Done.");
