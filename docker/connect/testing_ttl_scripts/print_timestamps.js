// Create indexes on all collections

console.log("Creating indexes. This might take some time...");

// const expire_seconds = 7884000; // 3 months

const expire_seconds = 90;

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

const documents = db.OdeBsmJson.find().toArray();

for (const document of documents){
    const timestamp = document._id.getTimestamp();
    console.log(timestamp);
}

console.log("Created ODE TTL's");

console.log("Done.");
