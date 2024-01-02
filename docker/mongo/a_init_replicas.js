
// Creating Replica Set, then Setting up Collections have been split apart to allow creating collections to happen on the primary partition of the replica set.


console.log("Initializing Replicas");
try{
    db_status = rs.status();
} catch(err){
    console.log("Initializing New DB");
    try{
        rs.initiate({ _id: 'rs0', members: [{ _id: 0, host: 'localhost:27017' }] }).ok 
    }catch(err){
        console.log("Unable to Initialize DB");
    }
}
