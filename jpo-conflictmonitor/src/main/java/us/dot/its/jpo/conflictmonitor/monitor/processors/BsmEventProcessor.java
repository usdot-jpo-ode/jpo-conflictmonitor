package us.dot.its.jpo.conflictmonitor.monitor.processors;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapBoundingBox;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.BsmEventTopology;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
import us.dot.its.jpo.conflictmonitor.monitor.utils.MathTransformPair;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuLogKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.BsmProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class BsmEventProcessor
        extends ContextualProcessor<
            RsuLogKey,
            ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point>,
            BsmIntersectionIdKey,
            Object> {

    private static final Logger logger = LoggerFactory.getLogger(BsmEventProcessor.class);
    private final String fStoreName = "bsm-event-state-store";
    private final Duration fPunctuationInterval = Duration.ofSeconds(10); // Check Every 10 Seconds
    private final long fSuppressTimeoutMillis = Duration.ofSeconds(10).toMillis(); // Emit event if no data for the last 10 seconds
    private TimestampedKeyValueStore<BsmIntersectionIdKey, BsmEvent> stateStore;

    @Getter
    @Setter
    private MapIndex mapIndex;

    @Setter
    @Getter
    private PunctuationType punctuationType;

    @Getter
    @Setter
    private boolean simplifyPath;

    @Getter
    @Setter
    private double simplifyPathToleranceMeters;

    private Cancellable punctuatorCancellationToken;

    @Override
    public void init(ProcessorContext<BsmIntersectionIdKey, Object> context) {
        try {
            super.init(context);
            stateStore = context.getStateStore(fStoreName);
            punctuatorCancellationToken = context.schedule(fPunctuationInterval, punctuationType, this::punctuate);
        } catch (Exception e) {
            logger.error("Error initializing BsmEventProcessor", e);
        }
    }


    @Override
    public void close() {
        // Cancel the punctuator if the task thread is closed per recommendation:
        // https://docs.confluent.io/platform/current/streams/developer-guide/processor-api.html#defining-a-stream-processor
        if (punctuatorCancellationToken != null) {
                punctuatorCancellationToken.cancel();
        }
        super.close();
    }

    @Override
    public void process(Record<RsuLogKey, ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point>> inputRecord) {
        RsuLogKey key = inputRecord.key();
        ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> value = inputRecord.value();
        long timestamp = inputRecord.timestamp();

        if(!validateBSM(value)){
            logger.info("BSM Is not Valid: {}", value);
            return;
        }

        try {

            // List MAPs that the new BSM is within
            CoordinateXY newCoord = BsmUtils.getPosition(value);
            List<MapBoundingBox> mapsContainingNewBsm = mapIndex.mapsContainingPoint(newCoord);
            // List intersections that the new BSM is in
            Set<IntersectionRegion> newIntersections
                    = mapsContainingNewBsm.stream()
                    .map(map -> new IntersectionRegion(map.getIntersectionId(), map.getRegion()))
                    .collect(Collectors.toSet());
            boolean newBsmInMap = !newIntersections.isEmpty(); // Whether the new BSM is in any MAP

            // If the BSM is in one or more MAPs, output BSM to each intersection partition
            if (newBsmInMap) {
                for (IntersectionRegion ir : newIntersections) {
                    int intersectionId = ir.getIntersectionId();
                    int region = ir.getRegion();
                    var bsmIntersectionIdKey = new BsmIntersectionIdKey(key.getBsmId(), key.getRsuId(), intersectionId, region, key.getLogId());

                    var intersectionRecord = inputRecord.withKey(bsmIntersectionIdKey);
                    context().forward(intersectionRecord, BsmEventTopology.PARTITIONED_BSM_SINK);
                }
            }

            // Get all events matching the RSU ID and BSM Vehicle ID from the state store.
            // There may be multiple events if the BSM is within multiple MAPs.
            List<KeyValue<BsmIntersectionIdKey, ValueAndTimestamp<BsmEvent>>> storedEvents = new ArrayList<>();
            try (var storeIterator = stateStore.all()) {
                while (storeIterator.hasNext()) {
                    var storedEvent = storeIterator.next();
                    var storedKey = storedEvent.key;
                    if (Objects.equals(key.getRsuId(), storedKey.getRsuId()) && Objects.equals(key.getBsmId(), storedKey.getBsmId())) {
                        storedEvents.add(storedEvent);
                    }
                }
            }




            if (storedEvents.isEmpty()) {
                // If there aren't any stored events for the new BSM, create them
                newEvents(value, key, mapsContainingNewBsm, timestamp);
            } else {
                // Process and extend existing BSM Events.
                Set<IntersectionRegion> extendedIntersections = new HashSet<>();
                Set<IntersectionRegion> exitedIntersections = new HashSet<>();
                for (var storedEvent : storedEvents) {
                    var eventKey = storedEvent.key;
                    BsmEvent event = storedEvent.value.value();
                    if (event.isInMapBoundingBox()) {
                        // The stored event was in an intersection
                        IntersectionRegion intersection = eventKey.getIntersectionRegion();
                        if (newIntersections.contains(intersection)) {
                            // The new point is also in this map, extend the bsm
                            extendEvent(eventKey, event, newCoord, value, timestamp);
                            extendedIntersections.add(intersection);
                        } else {
                            // The new point isn't in this map, emit the bsm
                            logger.info("Ending Bsm Event, New BSM not in region: {}", eventKey.getIntersectionId());
                            context().forward(new Record<>(eventKey, event, timestamp), BsmEventTopology.BSM_SINK);
                            stateStore.delete(eventKey);
                            exitedIntersections.add(intersection);
                        }
                    } else {
                        // The stored event was not in an intersection
                        if (!newBsmInMap) {
                            // The new BSM isn't in any intersections either, extend the stored one
                            extendEvent(eventKey, event, newCoord, value, timestamp);
                        } else {
                            // The new BSM is in intersections, emit the stored one
                            logger.info("Ending Bsm Event, New BSM in Region: {}", eventKey.getIntersectionId());
                            context().forward(new Record<>(eventKey, event, timestamp), BsmEventTopology.BSM_SINK);
                            stateStore.delete(eventKey);
                        }
                    }
                }

                // Create new events for intersections that the BSM is in that haven't been extended already
                // (it has newly entered the intersection bb)
                for (MapBoundingBox map : mapsContainingNewBsm) {
                    IntersectionRegion intersection = map.intersectionRegion();
                    if (!extendedIntersections.contains(intersection)) {
                        newEvent(value, key, timestamp, map);
                    }
                }

                // If the BSM isn't in any intersection, but previously was, create a new event
                if (!newBsmInMap && exitedIntersections.size() > 0) {
                    newEvent(value, key, timestamp);
                }
            }




        } catch (Exception e) {
            logger.error("Error in BsmEventProcessor.process", e);
        }
    }

    private void extendEvent(
            BsmIntersectionIdKey eventKey,
            BsmEvent event,
            Coordinate newCoord,
            ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> value,
            long timestamp) throws ParseException{

        String wktPath = addPointToPath(event.getWktPath(), newCoord, simplifyPath, simplifyPathToleranceMeters);
        event.setWktPath(wktPath);

        long newRecTime = BsmTimestampExtractor.getBsmTimestamp(value);

        // If the new record is older than the last start bsm. Use the last start bsm instead.
        if (newRecTime < BsmTimestampExtractor.getBsmTimestamp(event.getStartingBsm())) {
            // If there is no ending BSM make the previous start bsm the end bsm
            if (event.getEndingBsm() == null) {
                event.setEndingBsm(event.getStartingBsm());
            }
            event.setStartingBsm(value);
        } else if (event.getEndingBsm() == null || newRecTime > BsmTimestampExtractor.getBsmTimestamp(event.getEndingBsm())) {
            // If the new record is more recent than the old record
            event.setEndingBsm(value);
        }

        event.setEndingBsmTimestamp(timestamp);
        event.setWallClockTimestamp(Instant.now().toEpochMilli());
        stateStore.put(eventKey, ValueAndTimestamp.make(event, timestamp));
    }

    private void newEvents(
            ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> value,
            RsuLogKey key,
            List<MapBoundingBox> mapsContainingNewBsm, long timestamp) throws ParseException {
        if (mapsContainingNewBsm.isEmpty()) {
            // Not in any map.
            // Only create one.
            newEvent(value, key, timestamp);
        } else {
            for (var map : mapsContainingNewBsm) {
                newEvent(value, key, timestamp, map);
            }
        }
    }

    private void newEvent(
            ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> value,
            RsuLogKey key, long timestamp,
            MapBoundingBox map) throws ParseException {
        BsmEvent event = getNewEvent(value, timestamp, true);
        event.setWktMapBoundingBox(map.getBoundingPolygonWkt());
        event.setIntersectionID(map.getIntersectionId());
        var eventKey = new BsmIntersectionIdKey(key.getBsmId(), key.getRsuId(), map.getIntersectionId(), map.getRegion(), key.getLogId());
        stateStore.put(eventKey, ValueAndTimestamp.make(event, timestamp));
    }

    private void newEvent(ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> value, RsuLogKey key, long timestamp) throws ParseException {
        BsmEvent event = getNewEvent(value, timestamp, false);
        var eventKey = new BsmIntersectionIdKey(key.getBsmId(), key.getRsuId(), -1, key.getLogId());
        stateStore.put(eventKey, ValueAndTimestamp.make(event, timestamp));
    }



    private BsmEvent getNewEvent(ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> value, long timestamp, boolean inMapBoundingBox)
            throws ParseException {
        BsmEvent event = new BsmEvent(value);
        CoordinateXY newCoord = BsmUtils.getPosition(value);
        String wktPath = addPointToPath(event.getWktPath(), newCoord, simplifyPath, simplifyPathToleranceMeters);
        event.setWktPath(wktPath);
        event.setStartingBsmTimestamp(timestamp);
        event.setWallClockTimestamp(Instant.now().toEpochMilli());
        event.setInMapBoundingBox(inMapBoundingBox);
        return event;
    }



    private void punctuate(long timestamp) {
        try (KeyValueIterator<BsmIntersectionIdKey, ValueAndTimestamp<BsmEvent>> iterator = stateStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<BsmIntersectionIdKey, ValueAndTimestamp<BsmEvent>> item = iterator.next();
                var key = item.key;
                var value = item.value.value();
                long itemTimestamp;
                if (PunctuationType.WALL_CLOCK_TIME.equals(punctuationType)) {
                    itemTimestamp = value.getWallClockTimestamp();
                } else {
                    itemTimestamp = value.getEndingBsmTimestamp() != null ? value.getEndingBsmTimestamp() : timestamp;
                }
                var offset = timestamp - itemTimestamp;
                if (offset > fSuppressTimeoutMillis) {
                    logger.info("Ending BSM Event, Time limit reached :"+ key.getIntersectionId());
                    context().forward(new Record<>(key, value, timestamp), BsmEventTopology.BSM_SINK);
                    stateStore.delete(key);
                }
            }
        } catch (Exception e) {
            logger.error("Error in BsmEventProcessor.punctuate", e);
        }
    }

    public static boolean validateBSM(ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> bsm){
        if (bsm == null) {
            logger.error("Null BSM");
            return false;
        }

        if (bsm.getGeometry() instanceof us.dot.its.jpo.geojsonconverter.pojos.geojson.Point pointGeom) {
            double[] coords = pointGeom.getCoordinates();
            if (coords == null) {
                logger.error("BSM coordinates missing {}", bsm);
                return false;
            }
            if (coords.length < 2) {
                logger.error("BSM coords array too small. {}", bsm);
                return false;
            }
        } else {
            logger.error("ProcessedBsm geometry is not us.dot.its.jpo.geojsonconverter.pojos.geojson.Point, {}",
                    bsm.getGeometry());
            return false;
        }

        BsmProperties props = bsm.getProperties();

        if(props.getId() == null){
            logger.error("BSM id missing {}", bsm);
            return false;
        }

        if(props.getSecMark() == null){
            logger.error("BSM secMark missing {}", bsm);
            return false;
        }

        if(props.getSpeed() == null){
            logger.error("BSM speed missing {}", bsm);
            return false;
        }

        if(props.getHeading() == null){
            logger.error("BSM heading missing {}", bsm);
            return false;
        }

        if(props.getLogName() == null && props.getOriginIp() == null){
            logger.error("BSM source (log name or origin IP) missing {}", bsm);
            return false;
        }

        if (props.getTimeStamp() == null){
            logger.error("BSM timestamp missing {}", bsm);
            return false;
        }

        if (props.getOdeReceivedAt() == null) {
            logger.error("BSM odeReceivedAt missing {}", bsm);
            return false;
        }

        return true;
    }

    /**
     * Adds a point to a WKT line string
     * @param wktPath original WKT LineString or null
     * @return new WKT LineString with the new point added
     */
    public String addPointToPath(final String wktPath, final Coordinate coordinate,
                                 final boolean simplifyPath, final double simplifyPathToleranceCM) throws ParseException {
        List<Coordinate> coords = new ArrayList<>();
        if (wktPath != null) {
            WKTReader wktReader = new WKTReader();
            Geometry geom = wktReader.read(wktPath);
            if (geom instanceof Point) {
                coords.add(geom.getCoordinate());
            } else if (geom instanceof LineString) {
                coords.addAll(Arrays.asList(geom.getCoordinates()));
            }
        }
        coords.add(coordinate);
        GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
        if (coords.size() == 1) {
            return factory.createPoint(coords.get(0)).toText();
        } else {
            LineString path = factory.createLineString(coords.toArray(new Coordinate[0]));

            if (simplifyPath) {
                return simplifyPath(path, simplifyPathToleranceCM).toText();
            } else {
                return path.toText();
            }
        }
    }

    public LineString simplifyPath(LineString path, double simplifyPathToleranceMeters) {
        MathTransformPair transforms = CoordinateConversion.findGcsToUtmTransforms(path);
        if (transforms == null) {
            logger.error("Can't simplify path because coordinate transform wasn't found. Returning unsimplified path.");
            return path;
        }
        LineString utmPath = CoordinateConversion.transformLineString(path, transforms.getTransform());
        var simplifier = new DouglasPeuckerSimplifier(utmPath);
        simplifier.setDistanceTolerance(simplifyPathToleranceMeters);
        LineString utmSimplifiedPath = (LineString)simplifier.getResultGeometry();
        LineString gcsSimplifiedPath = CoordinateConversion.transformLineString(utmSimplifiedPath, transforms.getInverseTransform());
        return gcsSimplifiedPath;
    }

}