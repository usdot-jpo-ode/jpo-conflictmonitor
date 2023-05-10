package us.dot.its.jpo.conflictmonitor.monitor.processors;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
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
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;

public class BsmEventProcessor extends ContextualProcessor<String, OdeBsmData, String, BsmEvent> {


    private static final Logger logger = LoggerFactory.getLogger(BsmEventProcessor.class);
    private final String fStoreName = "bsm-event-state-store";
    private final Duration fPunctuationInterval = Duration.ofSeconds(10); // Check Every 10 Seconds
    private final long fSuppressTimeoutMillis = Duration.ofSeconds(10).toMillis(); // Emit event if no data for the last 10 seconds
    private TimestampedKeyValueStore<String, BsmEvent> stateStore;

    @Getter
    @Setter
    private MapIndex mapIndex;

    @Setter
    @Getter
    private PunctuationType punctuationType;



    @Override
    public void init(ProcessorContext<String, BsmEvent> context) {
        try {
            super.init(context);
            stateStore = (TimestampedKeyValueStore<String, BsmEvent>) context.getStateStore(fStoreName);
            context.schedule(fPunctuationInterval, punctuationType, this::punctuate);
        } catch (Exception e) {
            logger.error("Error initializing BsmEventProcessor", e);
        }
    }

    @Override
    public void process(Record<String, OdeBsmData> inputRecord) {
        String key = inputRecord.key();
        OdeBsmData value = inputRecord.value();
        long timestamp = inputRecord.timestamp();

        if(!validateBSM(value)){
            logger.info("BSM Is not Valid: {}", value);
            return;
        }

        try {
            // Key the BSM's based upon vehicle ID.
            key = key + "_" + ((J2735Bsm)value.getPayload().getData()).getCoreData().getId();
            
            ValueAndTimestamp<BsmEvent> record = stateStore.get(key);
            
            if (record != null) {
                BsmEvent event = record.value();

                // Check if the new BSM is within the MAP bounds
                CoordinateXY newCoord = BsmUtils.getPosition(value);
                boolean newBsmInMap = (mapIndex.mapContainingPoint(newCoord) != null);

                // Get the last position and check whether it is within a MAP bounds.
                OdeBsmData lastBsm = event.getEndingBsm() != null ? event.getEndingBsm() : event.getStartingBsm();
                if (lastBsm != null) {
                    CoordinateXY lastCoord = BsmUtils.getPosition(lastBsm);
                    boolean lastBsmInMap = (mapIndex.mapContainingPoint(lastCoord) != null);

                    // If the "bsmInMap" status has changed, then emit the event and start a new event
                    if (lastBsmInMap != newBsmInMap) {
                        context().forward(new Record<>(key, event, timestamp));
                        stateStore.delete(key);
                        newEvent(value, key, timestamp);
                        return;
                    }
                }

                // Add the coordinate for the new BSM to the event
                String wktPath = addPointToPath(event.getWktPath(), newCoord);
                event.setWktPath(wktPath);

                long newRecTime = BsmTimestampExtractor.getBsmTimestamp(value);

                // If the new record is older than the last start bsm. Use the last start bsm instead.
                if(newRecTime < BsmTimestampExtractor.getBsmTimestamp(event.getStartingBsm())){
                    // If there is no ending BSM make the previous start bsm the end bsm 
                    if(event.getEndingBsm() == null){
                        event.setEndingBsm(event.getStartingBsm());
                    }
                    event.setStartingBsm(value);
                } else if(event.getEndingBsm() == null || newRecTime > BsmTimestampExtractor.getBsmTimestamp(event.getEndingBsm())){
                    // If the new record is more recent than the old record
                    event.setEndingBsm(value);
                }

                event.setEndingBsmTimestamp(timestamp);
                stateStore.put(key, ValueAndTimestamp.make(event, timestamp));


            } else {
                newEvent(value, key, timestamp);
            }
        } catch (Exception e) {
            logger.error("Error in BsmEventProcessor.process", e);
        }
    }

    private void newEvent(OdeBsmData value, String key, long timestamp) throws ParseException {
        BsmEvent event = new BsmEvent(value);

        // Add the coordinate for the new BSM to the event
        CoordinateXY newCoord = BsmUtils.getPosition(value);
        String wktPath = addPointToPath(event.getWktPath(), newCoord);
        event.setWktPath(wktPath);

        // Tag with MAP bounding box if it is in one
        var map = mapIndex.mapContainingPoint(newCoord);
        if (map != null) {
            event.setInMapBoundingBox(true);
            event.setWktMapBoundingBox(MapIndex.getBoundingPolygon(map).toText());
        } else {
            event.setInMapBoundingBox(false);
        }

        event.setStartingBsmTimestamp(timestamp);
        stateStore.put(key, ValueAndTimestamp.make(event, timestamp));
    }

    private void punctuate(long timestamp) {
        try (KeyValueIterator<String, ValueAndTimestamp<BsmEvent>> iterator = stateStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, ValueAndTimestamp<BsmEvent>> item = iterator.next();
                var key = item.key;
                var value = item.value.value();
                var itemTimestamp = value.getEndingBsmTimestamp() != null ? value.getEndingBsmTimestamp() : timestamp;
                var offset = timestamp - itemTimestamp;
                if (offset > fSuppressTimeoutMillis) {
                    context().forward(new Record<>(key, value, timestamp));
                    stateStore.delete(key);
                }
            }
        } catch (Exception e) {
            logger.error("Error in BsmEventProcessor.punctuate", e);
        }
    }

    public boolean validateBSM(OdeBsmData bsm){
        if (bsm == null) return false;
        if (bsm.getPayload() == null) return false;
        if (!(bsm.getPayload() instanceof OdeBsmPayload)) return false;
        if (bsm.getMetadata() == null) return false;
        if (!(bsm.getMetadata() instanceof OdeBsmMetadata)) return false;
        if (bsm.getPayload().getData() == null) return false;
        if (!(bsm.getPayload().getData() instanceof J2735Bsm)) return false;


        J2735BsmCoreData core = ((J2735Bsm)bsm.getPayload().getData()).getCoreData();
        if (core == null) return false;

        OdeBsmMetadata metadata = (OdeBsmMetadata)bsm.getMetadata();

        if (core.getPosition() == null) return false;

        if(core.getPosition().getLongitude() == null){
            return false;
        }

        if(core.getPosition().getLatitude() == null){
            return false;
        }

        if(core.getId() == null){
            return false;
        }

        if(core.getSecMark() == null){
            return false;
        }

        if(core.getSpeed() == null){
            return false;
        }

        if(core.getHeading() == null){
            return false;
        }

        if(metadata.getBsmSource() == null){
            return false;
        }

        if(metadata.getOriginIp() == null){
            return false;
        }

        if (metadata.getRecordGeneratedAt() == null){
            return false;
        }

        if (metadata.getOdeReceivedAt() == null) {
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