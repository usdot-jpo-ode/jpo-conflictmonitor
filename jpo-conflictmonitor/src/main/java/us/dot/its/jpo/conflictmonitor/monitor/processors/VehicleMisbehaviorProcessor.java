// package us.dot.its.jpo.conflictmonitor.monitor.processors;
// import java.math.BigDecimal;
// import java.time.Duration;
// import java.time.Instant;
// import java.time.ZonedDateTime;
// import java.time.format.DateTimeFormatter;

// import org.apache.kafka.streams.state.KeyValueIterator;
// import org.apache.kafka.streams.state.KeyValueStore;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior.VehicleMisbehaviorParameters;
// import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
// import us.dot.its.jpo.conflictmonitor.monitor.models.events.VehicleMisbehaviorEvent;
// import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
// import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
// import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

// import org.apache.kafka.streams.processor.api.Record;
// import org.apache.kafka.streams.processor.PunctuationType;
// import org.apache.kafka.streams.processor.api.Processor;
// import org.apache.kafka.streams.processor.api.ProcessorContext;
// import org.apache.kafka.streams.KeyValue;

// public class VehicleMisbehaviorProcessor implements Processor<BsmRsuIdKey, ProcessedBsm<Point>, BsmRsuIdKey, VehicleMisbehaviorEvent>{

//     DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
//     ProcessorContext<BsmRsuIdKey, VehicleMisbehaviorEvent> context;
//     KeyValueStore<BsmRsuIdKey, ProcessedBsm<Point>> store;
//     String storeName;
//     VehicleMisbehaviorParameters parameters;

//     private static final Logger logger = LoggerFactory.getLogger(VehicleMisbehaviorProcessor.class);

//     public VehicleMisbehaviorProcessor(VehicleMisbehaviorParameters parameters){
//         this.storeName = parameters.getProcessedBsmStateStoreName();

//         System.out.println("Store Name in Constructor" + this.storeName);
//         this.parameters = parameters;
//     }

//     @Override
//     public void init(ProcessorContext<BsmRsuIdKey, VehicleMisbehaviorEvent> context) {
//         this.context = context;
//         store = context.getStateStore(this.storeName);
//         System.out.println("Store is" + store);
//         this.context.schedule(Duration.ofHours(1), PunctuationType.WALL_CLOCK_TIME, this::cleanupOldKeys);
//     }

    
//     /**
//      * 
//      * @param speed
//      * @return The Vehicle Speed in Miles Per Hour
//      */
//     public double getVehicleSpeedMph(BigDecimal speed){
//         return mpsToMph(speed.doubleValue() * 0.02);
//     }

//     /**
//      * 
//      * @param acceleration
//      * @return The Vehicle Acceleration converted to Ft / S^2
//      */
//     public double getVehicleAcceleration(BigDecimal acceleration){
//         return acceleration.doubleValue() * 0.01 * 3.2808399;
//     }

//     /**
//      * 
//      * @param yawRate
//      * @return The Yaw Rate converted to degrees per second.
//      */
//     public double getVehicleYawRate(BigDecimal yawRate){
//         return yawRate.doubleValue() * 0.01;
//     }


//     /**
//      * 
//      * @param mps
//      * @return The input value measured in miles per hour.
//      */
//     public double mpsToMph(double mps){
//         return mps * 2.23694;
//     }

//     /**
//      * 
//      * @param mps
//      * @return The input heading measured in degrees from north.
//      */
//     public double getVehicleHeading(BigDecimal heading){
//         return heading.doubleValue() * 0.0125;
//     }


//     /**
//      * 
//      * @param record
//      * @return gets an event with metadata matching the key values of the BSM
//      */
//     VehicleMisbehaviorEvent getEvent(Record<BsmRsuIdKey, ProcessedBsm<Point>> record){
//         VehicleMisbehaviorEvent event = new VehicleMisbehaviorEvent();

//         ProcessedBsm<Point> bsm = record.value();

//         event.setSource(record.key().toString());
//         event.setTimeStamp(bsm.getProperties().getTimeStamp().toInstant().toEpochMilli());
//         event.setVehicleID(bsm.getProperties().getId());
//         event.setReportedSpeed(getVehicleSpeedMph(bsm.getProperties().getSpeed()));
//         event.setSpeedRange(parameters.getSpeedRange());
//         event.setAccelerationRange(parameters.getAccelerationRange());
//         event.setYawRate(getVehicleYawRate(bsm.getProperties().getAccelSet().getAccelYaw()));
//         event.setReportedAccelerationLat(getVehicleAcceleration(bsm.getProperties().getAccelSet().getAccelLat()));
//         event.setReportedAccelerationLon(getVehicleAcceleration(bsm.getProperties().getAccelSet().getAccelLat()));
//         event.setReportedAccelerationVert(getVehicleAcceleration(bsm.getProperties().getAccelSet().getAccelVert()));
//         event.setReportedHeading(getVehicleHeading(bsm.getProperties().getHeading()));
//         return event;
//     }

//     public double getDecimalTime(ZonedDateTime time){
//         return time.toInstant().getEpochSecond() + (((double)time.toInstant().getNano()) / 1E9);
//     }


//     @Override
//     public void process(Record<BsmRsuIdKey, ProcessedBsm<Point>> record) {
//         ProcessedBsm<Point> bsm = record.value();

//         ProcessedBsm<Point> lastBsm = store.get(record.key());

//         System.out.println(bsm);

//         // Skip all calculations for the first BSM with a given key.
//         if(lastBsm == null){
//             store.put(record.key(), bsm);
//             return;
//         }

//         double timeDelta = getDecimalTime(bsm.getProperties().getTimeStamp()) - getDecimalTime(lastBsm.getProperties().getTimeStamp());

//         // Require at least 10 ms between BSM's.
//         if(timeDelta > 0.01){
            

//             double distance = CoordinateConversion.calculateGeodeticDistance(
//                 bsm.getGeometry().getCoordinates()[1],
//                 bsm.getGeometry().getCoordinates()[0],
//                 lastBsm.getGeometry().getCoordinates()[1],
//                 lastBsm.getGeometry().getCoordinates()[0]
//             );

//             double headingDelta = getVehicleHeading(bsm.getProperties().getHeading()) - getVehicleHeading(lastBsm.getProperties().getHeading());

//             if(timeDelta < 0){
//                 logger.warn("BSM Misbehavior received 2 vehicles in the wrong order. Computation performed in reverse");
//                 timeDelta = -1 * timeDelta;
//                 headingDelta = -1 * headingDelta;
//             }

//             double calculatedSpeed = mpsToMph(distance / timeDelta);
//             double calculatedHeading = headingDelta / timeDelta;

//             if(calculatedSpeed > parameters.getSpeedRange() || 
//                 getVehicleSpeedMph(bsm.getProperties().getSpeed()) > parameters.getSpeedRange() ||
//                 Math.abs(calculatedHeading) > parameters.getAccelerationRange() ||
//                 getVehicleAcceleration(bsm.getProperties().getAccelSet().getAccelYaw()) > parameters.getAccelerationRange()
//                 ){

//                 VehicleMisbehaviorEvent event = getEvent(record);
//                 event.setCalculatedSpeed(calculatedSpeed);
//                 event.setCalculatedHeading(calculatedHeading);
//                 context.forward(new Record<BsmRsuIdKey,VehicleMisbehaviorEvent>(record.key(), event, record.timestamp()));
//             }
//         }
//         else{
//             logger.warn("BSM Misbehavior received 2 vehicles with an identical vehicle ID within 10 ms. Key Data: " + record.key());
//         }

//         if(timeDelta > 0){
//             store.put(record.key(), bsm);
//         }
//     }


//     public Instant getMessageTime(ProcessedBsm<Point> message) {
//         return message.getProperties().getTimeStamp().toInstant();
//     }

//     private void cleanupOldKeys(final long timestamp) {
//         try (KeyValueIterator<BsmRsuIdKey, ProcessedBsm<Point>> iterator = store.all()) {
//             while (iterator.hasNext()) {
            
//             KeyValue<BsmRsuIdKey, ProcessedBsm<Point>> record = iterator.next();
//                 // Delete any record more than 10 minutes old
//                 if(Instant.ofEpochMilli(timestamp).minusSeconds(10 * 60).isAfter(getMessageTime(record.value))){
//                     store.delete(record.key);
//                 }
//             }
//         }
//     }
// }
