// Drops all the sink collections

console.log("Dropping collections.");

var collections = [
  "OdeRawEncodedBSMJson",
  "OdeBsmJson",
  "OdeMapJson",
  "ProcessedMap",
  "OdeRawEncodedMAPJson",
  "OdeRawEncodedSPATJson",
  "OdeSpatJson",
  "ProcessedSpat",
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

for (const collection of collections) {
  console.log("Dropping " + collection + " ...");
  db[collection].drop();
  console.log("Dropped " + collection);
}

console.log("Done.");
