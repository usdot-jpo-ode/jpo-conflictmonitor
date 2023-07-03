package us.dot.its.jpo.conflictmonitor.monitor.processors;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.streams.KeyValue;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEventIntersectionKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.IntersectionRegion;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class BsmEventProcessor extends ContextualProcessor<BsmIntersectionKey, OdeBsmData, BsmEventIntersectionKey, BsmEvent> {


    private static final Logger logger = LoggerFactory.getLogger(BsmEventProcessor.class);
    private final String fStoreName = "bsm-event-state-store";
    private final Duration fPunctuationInterval = Duration.ofSeconds(10); // Check Every 10 Seconds
    private final long fSuppressTimeoutMillis = Duration.ofSeconds(10).toMillis(); // Emit event if no data for the last 10 seconds
    private TimestampedKeyValueStore<BsmEventIntersectionKey, BsmEvent> stateStore;

    @Getter
    @Setter
    private MapIndex mapIndex;

    @Setter
    @Getter
    private PunctuationType punctuationType;



    @Override
    public void init(ProcessorContext<BsmEventIntersectionKey, BsmEvent> context) {
        try {
            super.init(context);
            stateStore = context.getStateStore(fStoreName);
            context.schedule(fPunctuationInterval, punctuationType, this::punctuate);
        } catch (Exception e) {
            logger.error("Error initializing BsmEventProcessor", e);
        }
    }

    @Override
    public void process(Record<BsmIntersectionKey, OdeBsmData> inputRecord) {
        BsmIntersectionKey key = inputRecord.key();
        OdeBsmData value = inputRecord.value();
        long timestamp = inputRecord.timestamp();

        if(!validateBSM(value)){
            logger.info("BSM Is not Valid: {}", value);
            return;
        }

        try {
            // Get all events matching the RSU ID and BSM Vehicle ID from the state store.
            // There may be multiple events if the BSM is within multiple MAPs.
            List<KeyValue<BsmEventIntersectionKey, ValueAndTimestamp<BsmEvent>>> storedEvents = new ArrayList<>();
            try (var storeIterator = stateStore.all()) {
                while (storeIterator.hasNext()) {
                    var storedEvent = storeIterator.next();
                    var storedKey = storedEvent.key;
                    if (Objects.equals(key, storedKey.getBsmIntersectionKey())) {
                        storedEvents.add(storedEvent);
                    }
                }
            }

            // List MAPs that the new BSM is within
            CoordinateXY newCoord = BsmUtils.getPosition(value);
            List<ProcessedMap<us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString>> mapsContainingNewBsm = mapIndex.mapsContainingPoint(newCoord);
            // List intersections that the new BSM is in
            Set<IntersectionRegion> newIntersections
                    = mapsContainingNewBsm.stream()
                        .map(map -> new IntersectionRegion(map))
                        .collect(Collectors.toSet());
            boolean newBsmInMap = !newIntersections.isEmpty(); // Whether the new BSM is in any MAP


            if (storedEvents.isEmpty()) {
                // If there aren't any stored events for the new BSM, create them
                newEvents(value, key, mapsContainingNewBsm, timestamp);
            } else {
                // Process and extend existing BSM Events.
                Set<IntersectionRegion> extendedIntersections = new HashSet<>();
                Set<IntersectionRegion> exitedIntersections = new HashSet<>();
                for (var storedEvent : storedEvents) {
                    BsmEventIntersectionKey eventKey = storedEvent.key;
                    BsmEvent event = storedEvent.value.value();
                    if (eventKey.hasIntersectionId()) {
                        // The stored event was in an intersection
                        IntersectionRegion intersection = eventKey.getIntersectionRegion();
                        if (newIntersections.contains(intersection)) {
                            // The new point is also in this map, extend the bsm
                            extendEvent(eventKey, event, newCoord, value, timestamp);
                            extendedIntersections.add(intersection);
                        } else {
                            // The new point isn't in this map, emit the bsm
                            System.out.println("Ending Bsm Event, New BSM not in region: " + eventKey.getIntersectionId());
                            context().forward(new Record<>(eventKey, event, timestamp));
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
                            System.out.println("Ending Bsm Event, New BSM in Region: " +eventKey.getIntersectionId());
                            context().forward(new Record<>(eventKey, event, timestamp));
                            stateStore.delete(eventKey);
                        }
                    }
                }

                // Create new events for intersections that the BSM is in that haven't been extended already
                // (it has newly entered the intersection bb)
                for (ProcessedMap<us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString> map : mapsContainingNewBsm) {
                    IntersectionRegion intersection = new IntersectionRegion(map);
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

    private void extendEvent(BsmEventIntersectionKey eventKey, BsmEvent event, Coordinate newCoord, OdeBsmData value, long timestamp) throws ParseException{
        String wktPath = addPointToPath(event.getWktPath(), newCoord);
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

    private void newEvents(OdeBsmData value, BsmIntersectionKey key, List<ProcessedMap<us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString>> mapsContainingNewBsm, long timestamp) throws ParseException {
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

    private void newEvent(OdeBsmData value, BsmIntersectionKey key, long timestamp, ProcessedMap<us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString> map) throws ParseException {
        BsmEvent event = getNewEvent(value, timestamp, true);
        event.setWktMapBoundingBox(MapIndex.getBoundingPolygon(map).toText());
        BsmEventIntersectionKey eventKey = new BsmEventIntersectionKey(key, new IntersectionRegion(map));
        stateStore.put(eventKey, ValueAndTimestamp.make(event, timestamp));
    }

    private void newEvent(OdeBsmData value, BsmIntersectionKey key, long timestamp) throws ParseException {
        BsmEvent event = getNewEvent(value, timestamp, false);
        BsmEventIntersectionKey eventKey = new BsmEventIntersectionKey(key);
        stateStore.put(eventKey, ValueAndTimestamp.make(event, timestamp));
    }



    private BsmEvent getNewEvent(OdeBsmData value, long timestamp, boolean inMapBoundingBox) throws ParseException {
        BsmEvent event = new BsmEvent(value);
        CoordinateXY newCoord = BsmUtils.getPosition(value);
        String wktPath = addPointToPath(event.getWktPath(), newCoord);
        event.setWktPath(wktPath);
        event.setStartingBsmTimestamp(timestamp);
        event.setWallClockTimestamp(Instant.now().toEpochMilli());
        event.setInMapBoundingBox(inMapBoundingBox);
        return event;
    }



    private void punctuate(long timestamp) {
        try (KeyValueIterator<BsmEventIntersectionKey, ValueAndTimestamp<BsmEvent>> iterator = stateStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<BsmEventIntersectionKey, ValueAndTimestamp<BsmEvent>> item = iterator.next();
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
                    System.out.println("Ending BSM Event, Time limit reached :"+ key.getIntersectionId());
                    context().forward(new Record<>(key, value, timestamp));
                    stateStore.delete(key);
                }
            }
        } catch (Exception e) {
            logger.error("Error in BsmEventProcessor.punctuate", e);
        }
    }

    public boolean validateBSM(OdeBsmData bsm){
        if (bsm == null) {
            logger.error("Null BSM");
            return false;
        }

        if (bsm.getPayload() == null) {
            logger.error("BSM missing payload {}", bsm);
            return false;
        }

        if (!(bsm.getPayload() instanceof OdeBsmPayload)) {
            logger.error("BSM payload is wrong type {}", bsm);
            return false;
        }

        if (bsm.getMetadata() == null) {
            logger.error("BSM missing metadata {}", bsm);
            return false;
        }

        if (!(bsm.getMetadata() instanceof OdeBsmMetadata)) {
            logger.error("BSM metadata is wrong type {}", bsm);
            return false;
        }

        if (bsm.getPayload().getData() == null) {
            logger.error("BSM payload.data missing {}", bsm);
            return false;
        }

        if (!(bsm.getPayload().getData() instanceof J2735Bsm)) {
            logger.error("BSM payload.data is wrong type {}", bsm);
            return false;
        }


        J2735BsmCoreData core = ((J2735Bsm)bsm.getPayload().getData()).getCoreData();
        if (core == null) {
            logger.error("BSM coreData missing {}", bsm);
            return false;
        }

        OdeBsmMetadata metadata = (OdeBsmMetadata)bsm.getMetadata();

        if (core.getPosition() == null) {
            logger.error("BSM position missing {}", bsm);
            return false;
        }

        if(core.getPosition().getLongitude() == null){
            logger.error("BSM longitude missing {}", bsm);
            return false;
        }

        if(core.getPosition().getLatitude() == null){
            logger.error("BSM latitude missing {}", bsm);
            return false;
        }

        if(core.getId() == null){
            logger.error("BSM id missing {}", bsm);
            return false;
        }

        if(core.getSecMark() == null){
            logger.error("BSM secMark missing {}", bsm);
            return false;
        }

        if(core.getSpeed() == null){
            logger.error("BSM speed missing {}", bsm);
            return false;
        }

        if(core.getHeading() == null){
            logger.error("BSM heading missing {}", bsm);
            return false;
        }

        if(metadata.getBsmSource() == null){
            logger.error("BSM source missing {}", bsm);
            return false;
        }

        if(metadata.getOriginIp() == null){
            logger.error("BSM originIp missing {}", bsm);
            return false;
        }

        if (metadata.getRecordGeneratedAt() == null){
            logger.error("BSM recordGeneratedAt missing {}", bsm);
            return false;
        }

        if (metadata.getOdeReceivedAt() == null) {
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
    public String addPointToPath(final String wktPath, final Coordinate coordinate) throws ParseException {
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
            return factory.createLineString(coords.toArray(new Coordinate[0])).toText();
        }
    }

}