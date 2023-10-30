package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.apache.kafka.streams.kstream.Produced;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventStreamsAlgorithm;
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
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event.IntersectionEventConstants.*;

@Component(DEFAULT_INTERSECTION_EVENT_ALGORITHM)
public class IntersectionEventTopology
    extends BaseStreamsTopology<Void>
    implements IntersectionEventStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(IntersectionEventTopology.class);



    ConflictMonitorProperties conflictMonitorProps;


    BsmEventAlgorithm bsmEventAlgorithm;
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

        if (bsmEventAlgorithm == null) throw new IllegalStateException("BsmEventAlgorithm is not set.");
        if (!(bsmEventAlgorithm instanceof BsmEventStreamsAlgorithm)) throw new IllegalStateException("Non-KafkaStreams BsmEventAlgorithm is not supported.");
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
    public BsmEventAlgorithm getBsmEventAlgorithm() {
        return bsmEventAlgorithm;
    }



    @Override
    public void setBsmEventAlgorithm(BsmEventAlgorithm bsmEventAlgorithm) {
        this.bsmEventAlgorithm = bsmEventAlgorithm;
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
    public ReadOnlyWindowStore<BsmIntersectionKey, OdeBsmData> getBsmWindowStore() {
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

    




    public static String getBsmID(OdeBsmData value){
        return ((J2735Bsm)value.getPayload().getData()).getCoreData().getId();
    }

    private static BsmAggregator getBsmsByTimeVehicle(ReadOnlyWindowStore<BsmIntersectionKey, OdeBsmData> bsmWindowStore, Instant start, Instant end, String id){
        logger.info("getBsmsByTimeVehicle: Start: {}, End: {}, ID: {}", start, end, id);

        Instant timeFrom = start.minusSeconds(60);
        Instant timeTo = start.plusSeconds(60);

        long startMillis = start.toEpochMilli();
        long endMillis = end.toEpochMilli();

        KeyValueIterator<Windowed<BsmIntersectionKey>, OdeBsmData> bsmRange = bsmWindowStore.fetchAll(timeFrom, timeTo);

        BsmAggregator agg = new BsmAggregator();

        while(bsmRange.hasNext()){
            KeyValue<Windowed<BsmIntersectionKey>, OdeBsmData> next = bsmRange.next();
            long ts = BsmTimestampExtractor.getBsmTimestamp(next.value);
            if(startMillis <= ts && endMillis >= ts && getBsmID(next.value).equals(id)){
                agg.add(next.value);
            }
        }

        bsmRange.close();
        agg.sort();

        logger.info("Found {} BSMs", agg.getBsms().size());

        return agg;
    }

    private static SpatAggregator getSpatByTime(ReadOnlyWindowStore<RsuIntersectionKey, ProcessedSpat> spatWindowStore, Instant start,
                                                Instant end, Integer intersection){

        logger.info("getSpatByTime: Start: {}, End: {}, IntersectionId: {}", start, end, intersection);

        KeyValueIterator<Windowed<RsuIntersectionKey>, ProcessedSpat> spatRange = spatWindowStore.fetchAll(start, end);

        List<ProcessedSpat> allSpats = new ArrayList<>();

        SpatAggregator spatAggregator = new SpatAggregator();

        while(spatRange.hasNext()){
            KeyValue<Windowed<RsuIntersectionKey>, ProcessedSpat> next = spatRange.next();

            ProcessedSpat spat = next.value;
            if (intersection != null && Objects.equals(spat.getIntersectionId(), intersection)) {
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
        return (ProcessedMap<LineString>) mapStore.get(key);
    }

    @Override
    public Topology buildTopology() {
        
        StreamsBuilder builder = new StreamsBuilder();

        if (messageIngestAlgorithm instanceof MessageIngestStreamsAlgorithm) {
            var messageIngestStreamsAlgorithm = (MessageIngestStreamsAlgorithm)messageIngestAlgorithm;
            builder = messageIngestStreamsAlgorithm.buildTopology(builder);
        }

        
        KStream<BsmEventIntersectionKey, BsmEvent> bsmEventStream =
            builder.stream(
                conflictMonitorProps.getKafkaTopicCmBsmEvent(), 
                Consumed.with(
                    JsonSerdes.BsmEventIntersectionKey(),
                    JsonSerdes.BsmEvent())
                )
                    // Filter out BSM Events that aren't inside any MAP bounding box
                    .filter(
                            (key, value) -> value != null && value.isInMapBoundingBox()
            );

        //bsmEventStream.print(Printed.toSysOut());
        bsmEventStream.process(() -> new DiagnosticProcessor<>("bsmEventStream", logger));


 
        // Join Spats, Maps and BSMS
        KStream<RsuIntersectionKey, VehicleEvent> vehicleEventsStream = bsmEventStream.flatMap(
            (key, value)->{

                
                List<KeyValue<RsuIntersectionKey, VehicleEvent>> result = new ArrayList<>();

                
                

                if(value.getStartingBsm() == null || value.getEndingBsm() == null){
                    return result;
                }

                String vehicleId = getBsmID(value.getStartingBsm());
                

                Instant firstBsmTime = Instant.ofEpochMilli(BsmTimestampExtractor.getBsmTimestamp(value.getStartingBsm()));
                Instant lastBsmTime = Instant.ofEpochMilli(BsmTimestampExtractor.getBsmTimestamp(value.getEndingBsm()));

                ProcessedMap<LineString> map = null;
                BsmAggregator bsms = getBsmsByTimeVehicle(getBsmWindowStore(), firstBsmTime, lastBsmTime, vehicleId);

                SpatAggregator spats = getSpatByTime(getSpatWindowStore(), firstBsmTime, lastBsmTime, key.getIntersectionId());




                if(spats.getSpats().size() > 0){

                    // Find the MAP for the BSMs
                    RsuIntersectionKey rsuKey = new RsuIntersectionKey();
                    rsuKey.setRsuId(key.getRsuId());
                    rsuKey.setIntersectionId(key.getIntersectionId());

                    map = getMap(getMapStore(), rsuKey);

                    

                    if(map != null){

                        // logger.info("Found MAP: {}", map);
                        
                        Intersection intersection = Intersection.fromProcessedMap(map);

                        // logger.info("Got Intersection object from MAP: {}", intersection);

                        VehicleEvent event = new VehicleEvent(bsms, spats, intersection, rsuKey.toString());

                        result.add(new KeyValue<>(rsuKey, event));
    
                        
                    } else{
                        logger.warn("Map was Null");
                    }

                }


                logger.info("Detected Vehicle Event");
                logger.info("Vehicle ID: {}", vehicleId);
                logger.info("Captured Bsms: {}", bsms.getBsms().size());
                logger.info("Captured Spats: {}", spats.getSpats().size());

                return result;
            }
        );


        // Perform Analytics on Lane direction of Travel Events
        KStream<RsuIntersectionKey, LaneDirectionOfTravelEvent> laneDirectionOfTravelEventStream = vehicleEventsStream.flatMap(
            (key, value)->{
                String rsuId = key.getRsuId();
                List<KeyValue<RsuIntersectionKey, LaneDirectionOfTravelEvent>> result = new ArrayList<>();
                if(value.getBsms().getBsms().size() > 2){
                    double minDistanceFeet = stopLinePassageParameters.getStopLineMinDistance(rsuId);
                    double headingToleranceDegrees = stopLinePassageParameters.getHeadingTolerance(rsuId);

                    
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
                    new RsuIdPartitioner<RsuIntersectionKey, LaneDirectionOfTravelEvent>()));

        

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
                    new RsuIdPartitioner<RsuIntersectionKey, ConnectionOfTravelEvent>()));


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
                    new RsuIdPartitioner<RsuIntersectionKey, StopLinePassageEvent>())
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
                    new RsuIdPartitioner<RsuIntersectionKey, StopLineStopEvent>()));
 
        Topology intersectionTopology = builder.build();
        Topology combinedTopology = null;
        if (bsmEventAlgorithm instanceof BsmEventStreamsAlgorithm) {
            var bsmEventStreamsAlgorithm = (BsmEventStreamsAlgorithm)bsmEventAlgorithm;
            combinedTopology = bsmEventStreamsAlgorithm.buildTopology(intersectionTopology);
        }
        return combinedTopology;
    }


    
}
