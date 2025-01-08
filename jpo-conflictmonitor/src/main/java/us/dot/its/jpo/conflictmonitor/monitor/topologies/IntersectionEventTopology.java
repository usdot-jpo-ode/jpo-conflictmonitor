package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event.IntersectionEventStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.conflictmonitor.monitor.models.VehicleEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Intersection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLineStopEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.processors.DiagnosticProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event.IntersectionEventConstants.*;


@Component(DEFAULT_INTERSECTION_EVENT_ALGORITHM)
public class IntersectionEventTopology
    extends BaseStreamsTopology<Void>
    implements IntersectionEventStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(IntersectionEventTopology.class);



    ConflictMonitorProperties conflictMonitorProps;


    MessageIngestAlgorithm messageIngestAlgorithm;
    LaneDirectionOfTravelAlgorithm laneDirectionOfTravelAlgorithm;
    LaneDirectionOfTravelParameters laneDirectionOfTravelParams;
    ConnectionOfTravelAlgorithm connectionOfTravelAlgorithm;
    ConnectionOfTravelParameters connectionOfTravelParams;
    StopLinePassageAlgorithm signalStateVehicleCrossesAlgorithm;
    StopLinePassageParameters stopLinePassageParameters;
    StopLineStopAlgorithm signalStateVehicleStopsAlgorithm;
    StopLineStopParameters stopLineStopParameters;


    @Override
    protected Logger getLogger() {
        return logger;
    }



    @Override
    protected void validate() {
        if (streamsProperties == null) throw new IllegalStateException("Streams properties are not set.");

        if (messageIngestAlgorithm == null) throw new IllegalStateException("MessageIngestAlgorithm is not set.");
        if (!(messageIngestAlgorithm instanceof MessageIngestStreamsAlgorithm)) throw new IllegalStateException("Non-KafkaStreams MessageIngestAlgorithm is not supported.");
        if (laneDirectionOfTravelAlgorithm == null) throw new IllegalStateException("LaneDirectionOfTravelAlgorithm is not set");
        if (laneDirectionOfTravelParams == null) throw new IllegalStateException("LaneDirectionOfTravelParameters is not set");
        if (connectionOfTravelAlgorithm == null) throw new IllegalStateException("ConnectionOfTravelAlgorithm is not set");
        if (connectionOfTravelParams == null) throw new IllegalStateException("ConnectionOfTravelParameters is not set");
        if (signalStateVehicleCrossesAlgorithm == null) throw new IllegalStateException("SignalStateVehicleCrossesAlgorithm is not set");
        if (stopLinePassageParameters == null) throw new IllegalStateException("SignalStateVehicleCrossesParameters is not set");
        if (signalStateVehicleStopsAlgorithm == null) throw new IllegalStateException("SignalStateVehicleStopsAlgorithm is not set");
        if (stopLineStopParameters == null) throw new IllegalStateException("SignalStateVehicleStopsParameters is not set");
        if (streams != null && streams.state().isRunningOrRebalancing()) throw new IllegalStateException("Start called while streams is already running.");
    }




    @Override
    public ConflictMonitorProperties getConflictMonitorProperties() {
        return conflictMonitorProps;
    }



    @Override
    public void setConflictMonitorProperties(ConflictMonitorProperties conflictMonitorProps) {
        this.conflictMonitorProps = conflictMonitorProps;
    }


     @Override
    public MessageIngestAlgorithm getMessageIngestAlgorithm() {
        return messageIngestAlgorithm;
    }

    @Override
    public void setMessageIngestAlgorithm(MessageIngestAlgorithm messageIngestAlgorithm) {
        this.messageIngestAlgorithm = messageIngestAlgorithm;
    }


    @Override
    public LaneDirectionOfTravelAlgorithm getLaneDirectionOfTravelAlgorithm() {
        return laneDirectionOfTravelAlgorithm;
    }

    @Override
    public LaneDirectionOfTravelParameters getLaneDirectionOfTravelParams() {
        return laneDirectionOfTravelParams;
    }

    @Override
    public ConnectionOfTravelAlgorithm getConnectionOfTravelAlgorithm() {
        return connectionOfTravelAlgorithm;
    }

    @Override
    public ConnectionOfTravelParameters getConnectionOfTravelParams() {
        return connectionOfTravelParams;
    }

    @Override
    public StopLinePassageAlgorithm getSignalStateVehicleCrossesAlgorithm() {
        return signalStateVehicleCrossesAlgorithm;
    }

    @Override
    public StopLinePassageParameters getStopLinePassageParameters() {
        return stopLinePassageParameters;
    }

    @Override
    public StopLineStopAlgorithm getSignalStateVehicleStopsAlgorithm() {
        return signalStateVehicleStopsAlgorithm;
    }

    @Override
    public StopLineStopParameters getStopLineStopParameters() {
        return stopLineStopParameters;
    }

    @Override
    public ReadOnlyWindowStore<BsmIntersectionIdKey, ProcessedBsm<Point>> getBsmWindowStore() {
        return ((MessageIngestStreamsAlgorithm)messageIngestAlgorithm).getBsmWindowStore(streams);
    }

    @Override
    public ReadOnlyWindowStore<RsuIntersectionKey, ProcessedSpat> getSpatWindowStore() {
        return ((MessageIngestStreamsAlgorithm)messageIngestAlgorithm).getSpatWindowStore(streams);
    }

    @Override
    public ReadOnlyKeyValueStore<RsuIntersectionKey, ProcessedMap<LineString>> getMapStore() {
        return ((MessageIngestStreamsAlgorithm)messageIngestAlgorithm).getMapStore(streams);
    }




    @Override
    public void setLaneDirectionOfTravelAlgorithm(LaneDirectionOfTravelAlgorithm laneAlgorithm) {
        this.laneDirectionOfTravelAlgorithm = laneAlgorithm;
    }

    @Override
    public void setLaneDirectionOfTravelParams(LaneDirectionOfTravelParameters laneParams) {
        this.laneDirectionOfTravelParams = laneParams;
    }

    @Override
    public void setConnectionOfTravelAlgorithm(ConnectionOfTravelAlgorithm connTravelAlgorithm) {
        this.connectionOfTravelAlgorithm = connTravelAlgorithm;
    }

    @Override
    public void setConnectionOfTravelParams(ConnectionOfTravelParameters connTravelParams) {
        this.connectionOfTravelParams = connTravelParams;
    }

    @Override
    public void setSignalStateVehicleCrossesAlgorithm(StopLinePassageAlgorithm crossesAlgorithm) {
        this.signalStateVehicleCrossesAlgorithm = crossesAlgorithm;
    }

    @Override
    public void setStopLinePassageParameters(StopLinePassageParameters crossesParams) {
        this.stopLinePassageParameters = crossesParams;
    }

    @Override
    public void setSignalStateVehicleStopsAlgorithm(StopLineStopAlgorithm stopsAlgorithm) {
        this.signalStateVehicleStopsAlgorithm = stopsAlgorithm;
    }

    @Override
    public void setStopLineStopParameters(StopLineStopParameters stopsParams) {
        this.stopLineStopParameters = stopsParams;
    }

    





    private static BsmAggregator getBsmsByTimeVehicle(ReadOnlyWindowStore<BsmIntersectionIdKey, ProcessedBsm<Point>> bsmWindowStore,
                                                      Instant start, Instant end, BsmIntersectionIdKey key){
        logger.info("getBsmsByTimeVehicle: Start: {}, End: {}, key: {}", start, end, key);
        final String rsuId = key.getRsuId();
        final String vehicleId = key.getBsmId();
        final int intersectionId = key.getIntersectionId();
        final int region = key.getRegion();

        Instant timeFrom = start.minusSeconds(60);
        Instant timeTo = start.plusSeconds(60);

        long startMillis = start.toEpochMilli();
        long endMillis = end.toEpochMilli();

        KeyValueIterator<Windowed<BsmIntersectionIdKey>, ProcessedBsm<Point>> bsmRange = bsmWindowStore.fetchAll(timeFrom, timeTo);

        BsmAggregator agg = new BsmAggregator();

        while(bsmRange.hasNext()){
            KeyValue<Windowed<BsmIntersectionIdKey>, ProcessedBsm<Point>> next = bsmRange.next();
            Windowed<BsmIntersectionIdKey> storedWindowedKey = next.key;
            BsmIntersectionIdKey storedKey = storedWindowedKey.key();
            String storedVehicleId = storedKey.getBsmId();
            String storedRsuId = storedKey.getRsuId();
            int storedIntersectionId = storedKey.getIntersectionId();
            int storedRegion = storedKey.getRegion();
            ProcessedBsm<Point> storedBsm = next.value;
            long ts = BsmTimestampExtractor.getBsmTimestamp(next.value);

            // Filter by timestamp and Vehicle ID.
            // Also filter by RSU and intersection/region to avoid duplicates
            if(startMillis <= ts && endMillis >= ts
                    && Objects.equals(storedVehicleId, vehicleId)
                    && Objects.equals(storedRsuId, rsuId)
                    && intersectionId == storedIntersectionId){
                // Filter by region if it is present and not test value
                if (region > 0 && region != storedRegion) continue;
                agg.add(storedBsm);
            }
        }

        bsmRange.close();
        agg.sort();

        logger.info("Found {} BSMs", agg.getBsms().size());

        return agg;
    }

    private static SpatAggregator getSpatByTime(ReadOnlyWindowStore<RsuIntersectionKey, ProcessedSpat> spatWindowStore, Instant start,
                                                Instant end, RsuIntersectionKey key){

        logger.info("getSpatByTime: Start: {}, End: {}, Key: {}", start, end, key);
        final int intersection = key.getIntersectionId();
        final int region = key.getRegion();

        KeyValueIterator<Windowed<RsuIntersectionKey>, ProcessedSpat> spatRange = spatWindowStore.fetchAll(start, end);

        List<ProcessedSpat> allSpats = new ArrayList<>();

        SpatAggregator spatAggregator = new SpatAggregator();

        while(spatRange.hasNext()){
            KeyValue<Windowed<RsuIntersectionKey>, ProcessedSpat> next = spatRange.next();

            ProcessedSpat spat = next.value;
            int spatIntersectionId = spat.getIntersectionId() != null ? spat.getIntersectionId().intValue() : -1;
            int spatRegion = spat.getRegion() != null ? spat.getRegion().intValue() : -1;
            if (spatIntersectionId == intersection) {
                // Filter by region if it is present and not test value
                if (region > 0 && region != spatRegion) continue;
                spatAggregator.add(spat);
            }

            allSpats.add(spat);
        }
        spatRange.close();
        spatAggregator.sort();

        logger.info("Total SPATs: {}", allSpats.size());
        logger.info("Found {} SPATs", spatAggregator.getSpats().size());

        return spatAggregator;
    }



    private static ProcessedMap<LineString> getMap(ReadOnlyKeyValueStore<RsuIntersectionKey, ProcessedMap<LineString>> mapStore, RsuIntersectionKey key){
        // Find the MAP based on the intersection ID and optionally region
        try (var mapIterator = mapStore.all()) {
            while (mapIterator.hasNext()) {
                var kvp = mapIterator.next();
                RsuIntersectionKey storedKey = kvp.key;
                if (key.getRegion() > 0) {
                    // Non-test region specified, get matching region MAP
                    if (key.getRegion() == storedKey.getRegion() && key.getIntersectionId() == storedKey.getIntersectionId()) {
                        logger.info("Found MAP intersectionID = {}, region = {}", storedKey.getIntersectionId(), storedKey.getRegion());
                        return kvp.value;
                    }
                } else {
                    // No region, or test region; get first MAP matching the intersectionID ignoring the region
                    if (key.getIntersectionId() == storedKey.getIntersectionId()) {
                        logger.info("Found MAP intersectionID = {}, region = {}", storedKey.getIntersectionId(), storedKey.getRegion());
                        return kvp.value;
                    }
                }
            }
        }
        return null;
    }



    @Override
    public Topology buildTopology() {
        
        StreamsBuilder builder = new StreamsBuilder();

        if (messageIngestAlgorithm instanceof MessageIngestStreamsAlgorithm messageIngestStreamsAlgorithm) {
            messageIngestStreamsAlgorithm.buildTopology(builder);
        }


        
        KStream<BsmIntersectionIdKey, BsmEvent> bsmEventStream =
            builder
                .stream(
                    conflictMonitorProps.getKafkaTopicCmBsmEvent(),
                    Consumed.with(
                        JsonSerdes.BsmIntersectionIdKey(),
                        JsonSerdes.BsmEvent()))

                    // Remove BSM Events outside MAPs
                    .filter((key, value) -> value == null || value.isInMapBoundingBox()) // pass through tombstones

                    // De-duplicate BSM Events from different RSUs.
                    // Filter out if the (vehicle ID, intersection, region) was already added to the downstream table
                    // within the time interval
                    .filter((key, value) -> value == null || isNewBsmEvent(key, value)) // pass through tombstones

                    // Queryable KTable that is queried by the above filter
                    .toTable(
                        Materialized.<BsmIntersectionIdKey, BsmEvent, KeyValueStore<Bytes, byte[]>>as(BSM_EVENT_DEDUPLICATE_STORE)
                            .withKeySerde(JsonSerdes.BsmIntersectionIdKey())
                            .withValueSerde(JsonSerdes.BsmEvent())
                            .withLoggingDisabled() // Don't need to create an internal topic for this
                            .withCachingDisabled())
                    .toStream()

                    // Remove tombstones from the stream here that have already served their purpose of deleting from
                    // the table store
                    .filter((key, value) -> value != null);

        // Whenever a new BsmEvent is received, clean old entries from the BsmEvent deduplicate store to prevent
        // it from growing indefinitely
        bsmEventStream
                // Get a list of tombstones
                .flatMap((key, value) -> listBsmEventsToRemove(value))
                // Write the tombstones back to the BsmEvent topic
                .to(conflictMonitorProps.getKafkaTopicCmBsmEvent(),
                    Produced.with(
                            JsonSerdes.BsmIntersectionIdKey(),
                            JsonSerdes.BsmEvent(),
                            new IntersectionIdPartitioner<>()));

        bsmEventStream.process(() -> new DiagnosticProcessor<>("bsmEventStream", logger));


 
        // Join Spats, Maps and BSMS
        KStream<RsuIntersectionKey, VehicleEvent> vehicleEventsStream = bsmEventStream.flatMap(
            (key, value)->{

                
                List<KeyValue<RsuIntersectionKey, VehicleEvent>> result = new ArrayList<>();

                
                

                if(value.getStartingBsm() == null || value.getEndingBsm() == null){
                    logger.warn("Starting or ending BSM is null in BSM event, returning empty VehicleEvent list.  BsmEvent: {}", value);
                    return result;
                }


                

                Instant firstBsmTime = Instant.ofEpochMilli(BsmTimestampExtractor.getBsmTimestamp(value.getStartingBsm()));
                Instant lastBsmTime = Instant.ofEpochMilli(BsmTimestampExtractor.getBsmTimestamp(value.getEndingBsm()));

                ProcessedMap<LineString> map = null;
                BsmAggregator bsms = getBsmsByTimeVehicle(getBsmWindowStore(), firstBsmTime, lastBsmTime, key);

                SpatAggregator spats = getSpatByTime(getSpatWindowStore(), firstBsmTime, lastBsmTime, key);




                if(spats.getSpats().size() > 0){

                    // Find the MAP for the BSMs
                    RsuIntersectionKey rsuKey = new RsuIntersectionKey();
                    rsuKey.setRsuId(key.getRsuId());
                    rsuKey.setIntersectionId(key.getIntersectionId());

                    map = getMap(getMapStore(), rsuKey);

                    

                    if(map != null){

                        Intersection intersection = Intersection.fromProcessedMap(map);

                        VehicleEvent event = new VehicleEvent(bsms, spats, intersection, rsuKey.toString());

                        result.add(new KeyValue<>(rsuKey, event));
                        
                    } else{
                        logger.warn("Map was Null");
                    }

                }


                logger.info("Detected Vehicle Event");
                logger.info("Vehicle ID: {}", key.getBsmId());
                logger.info("Captured Bsms: {}", bsms.getBsms().size());
                logger.info("Captured Spats: {}", spats.getSpats().size());

                return result;
            }
        );


        // Perform Analytics on Lane direction of Travel Events
        KStream<RsuIntersectionKey, LaneDirectionOfTravelEvent> laneDirectionOfTravelEventStream = vehicleEventsStream.flatMap(
            (key, value)->{
                IntersectionRegion intersectionRegion = new IntersectionRegion(key.getIntersectionId(), key.getRegion());
                List<KeyValue<RsuIntersectionKey, LaneDirectionOfTravelEvent>> result = new ArrayList<>();
                if(value.getBsms().getBsms().size() > 2){
                    double minDistanceFeet = stopLinePassageParameters.getStopLineMinDistance(intersectionRegion);
                    double headingToleranceDegrees = stopLinePassageParameters.getHeadingTolerance(intersectionRegion);

                    
                    VehiclePath path = new VehiclePath(value.getBsms(), value.getIntersection(), minDistanceFeet,
                            headingToleranceDegrees);

                    
                    ArrayList<LaneDirectionOfTravelEvent> events = laneDirectionOfTravelAlgorithm.getLaneDirectionOfTravelEvents(laneDirectionOfTravelParams, path);
                    
                    for(LaneDirectionOfTravelEvent event: events){
                        event.setSource(value.getSource());
                        result.add(new KeyValue<>(key, event));
                    }
                }
                return result;
            }
        );

        logger.info("LaneDirectionOfTravelEventStream: {}", laneDirectionOfTravelEventStream);
        laneDirectionOfTravelEventStream.to(
            conflictMonitorProps.getKafkaTopicCmLaneDirectionOfTravelEvent(), 
            Produced.with(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                    JsonSerdes.LaneDirectionOfTravelEvent(),
                    new IntersectionIdPartitioner<RsuIntersectionKey, LaneDirectionOfTravelEvent>()));

        

        // Perform Analytics on Lane direction of Travel Events
        KStream<RsuIntersectionKey, ConnectionOfTravelEvent> connectionTravelEventsStream = vehicleEventsStream.flatMap(
            (key, value)->{
                List<KeyValue<RsuIntersectionKey, ConnectionOfTravelEvent>> result = new ArrayList<>();
                if(value.getBsms().getBsms().size() > 2){
                    VehiclePath path = new VehiclePath(value.getBsms(), value.getIntersection(), 15.0, 20.0);

                    
                    ConnectionOfTravelEvent event = connectionOfTravelAlgorithm.getConnectionOfTravelEvent(connectionOfTravelParams, path);
                    if(event != null){
                        event.setSource(value.getSource());
                        result.add(new KeyValue<>(key, event));
                    }else{
                        logger.info("No Lane Connection of Travel Event Found");
                    }
                }
                return result;
                
            }
        );

        connectionTravelEventsStream.to(
            conflictMonitorProps.getKafkaTopicCmConnectionOfTravelEvent(), 
            Produced.with(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                    JsonSerdes.ConnectionOfTravelEvent(),
                    new IntersectionIdPartitioner<RsuIntersectionKey, ConnectionOfTravelEvent>()));


        // Perform Analytics of Signal State Vehicle Crossing Intersection
        KStream<RsuIntersectionKey, StopLinePassageEvent> stopLinePassageEventStream = vehicleEventsStream.flatMap(
            (key, value)->{
                List<KeyValue<RsuIntersectionKey, StopLinePassageEvent>> result = new ArrayList<>();

                if(value.getBsms().getBsms().size() > 2){
                    VehiclePath path = new VehiclePath(value.getBsms(), value.getIntersection(), stopLinePassageParameters.getStopLineMinDistance(), stopLinePassageParameters.getHeadingTolerance());

                    
                    StopLinePassageEvent event = signalStateVehicleCrossesAlgorithm.getStopLinePassageEvent(stopLinePassageParameters, path, value.getSpats());
                    if(event != null){
                        event.setSource(value.getSource());
                        result.add(new KeyValue<>(key, event));
                        logger.info("Found Stop Line Passage Event");
                    }
                }

                return result;
            }
        );

        stopLinePassageEventStream.to(
            conflictMonitorProps.getKafkaTopicCmSignalStateEvent(), 
            Produced.with(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                    JsonSerdes.StopLinePassageEvent(),
                    new IntersectionIdPartitioner<RsuIntersectionKey, StopLinePassageEvent>())
                );



        // Perform Analytics of Stop Line Stop Events
        KStream<RsuIntersectionKey, StopLineStopEvent> stopLineStopEventStream = vehicleEventsStream.flatMap(
            (key, value)->{
                List<KeyValue<RsuIntersectionKey, StopLineStopEvent>> result = new ArrayList<>();
                if(value.getBsms().getBsms().size() >2){
                    VehiclePath path = new VehiclePath(value.getBsms(), value.getIntersection(), stopLinePassageParameters.getStopLineMinDistance(), stopLineStopParameters.getHeadingTolerance());

                    
                    StopLineStopEvent event = signalStateVehicleStopsAlgorithm.getStopLineStopEvent(stopLineStopParameters, path, value.getSpats());
                    if(event != null){
                        event.setSource(value.getSource());
                        result.add(new KeyValue<>(key, event));
                    }
                }

                
                return result;
            }
        );

        stopLineStopEventStream.to(
            conflictMonitorProps.getKafakTopicCmVehicleStopEvent(), 
            Produced.with(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                    JsonSerdes.StopLineStopEvent(),
                    new IntersectionIdPartitioner<RsuIntersectionKey, StopLineStopEvent>()));
 
       return builder.build();
    }


    private static final String BSM_EVENT_DEDUPLICATE_STORE = "bsm-event-deduplicate-store";
    private static final long BSM_EVENT_INTERVAL_MS = 1000;
    private static final long BSM_EVENT_MAX_AGE_MS = 120 * 1000;

    private ReadOnlyKeyValueStore<BsmIntersectionIdKey, BsmEvent> getBsmEventDeduplicateStore() {
        return streams.store(
                StoreQueryParameters.fromNameAndType(BSM_EVENT_DEDUPLICATE_STORE,
                        QueryableStoreTypes.keyValueStore()));
    }

    // Test if a stored BSM Event already exists for the same vehicleID, intersectionId and region
    // within the interval
    private boolean isNewBsmEvent(BsmIntersectionIdKey key, BsmEvent bsmEvent) {
        final int intersectionId = key.getIntersectionId();
        final int region = key.getRegion();
        final String vehicleId = key.getBsmId();
        ReadOnlyKeyValueStore<BsmIntersectionIdKey, BsmEvent> bsmEventStore = getBsmEventDeduplicateStore();
        BsmEvent storedBsmEvent = null;
        try (var iterator = bsmEventStore.all()) {
            while (iterator.hasNext()) {
                var kvp = iterator.next();
                var storedKey = kvp.key;
                String storedVehicleId = storedKey.getBsmId();
                int storedIntersectionId = storedKey.getIntersectionId();
                int storedRegion = storedKey.getRegion();
                // Compare region if any
                if (region > 0 && region != storedRegion) continue;
                // Compare vehicleId and intersectionId
                if (Objects.equals(vehicleId, storedVehicleId) && intersectionId == storedIntersectionId) {
                    storedBsmEvent = kvp.value;
                    break;
                }
            }
        }
        if (storedBsmEvent == null) return true;
        long interval = Math.abs(storedBsmEvent.getStartingBsmTimestamp() - bsmEvent.getStartingBsmTimestamp());
        logger.info("Stored BSM event exists for key {}. BSMEvent interval = {}", key, interval);
        boolean filter = interval > BSM_EVENT_INTERVAL_MS;
        if (!filter) {
            logger.info("The duplicate BSMEvent interval is less than {} ms, it will be filtered out", BSM_EVENT_INTERVAL_MS);
        }
        return filter;
    }

    private List<KeyValue<BsmIntersectionIdKey, BsmEvent>> listBsmEventsToRemove(BsmEvent currentBsmEvent) {
        final long currentTimestamp = currentBsmEvent.getStartingBsmTimestamp();
        // List of events to remove
        List<KeyValue<BsmIntersectionIdKey, BsmEvent>> tombstones = new ArrayList<>();
        ReadOnlyKeyValueStore<BsmIntersectionIdKey, BsmEvent> bsmEventStore = getBsmEventDeduplicateStore();
        try (var iterator = bsmEventStore.all()) {
            while (iterator.hasNext()) {
                var kvp = iterator.next();
                var storedKey = kvp.key;
                var storedValue = kvp.value;
                long storedTimestamp = storedValue.getStartingBsmTimestamp();
                long interval = currentTimestamp - storedTimestamp;
                if (interval > BSM_EVENT_MAX_AGE_MS) {
                    tombstones.add(KeyValue.pair(storedKey, (BsmEvent)null));
                    logger.info("Old BSMEvent with key {}, timestamp {} will be removed from deduplicate store",
                            storedKey, storedTimestamp);
                }
            }
        }
        return tombstones;
    }
    
}
