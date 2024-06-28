package us.dot.its.jpo.deduplicator.deduplicator.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.models.OdeBsmPair;
import us.dot.its.jpo.deduplicator.deduplicator.serialization.PairSerdes;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.model.OdeMapPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import org.apache.kafka.streams.kstream.*;
import org.geotools.referencing.GeodeticCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class BsmDeduplicatorTopology {

    private static final Logger logger = LoggerFactory.getLogger(MapDeduplicatorTopology.class);

    Topology topology;
    KafkaStreams streams;
    DeduplicatorProperties props;
    ObjectMapper objectMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    GeodeticCalculator calculator;
    


    public BsmDeduplicatorTopology(DeduplicatorProperties props){
        this.props = props;
        this.objectMapper = new ObjectMapper();
        calculator = new GeodeticCalculator();
    }


    
    public void start() {
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, props.createStreamProperties("BsmDeduplicator"));
        if (exceptionHandler != null) streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null) streams.setStateListener(stateListener);
        logger.info("Starting Bsm Deduplicator Topology");
        streams.start();
    }

    public Instant getInstantFromBsm(OdeBsmData bsm){
        String time = ((OdeBsmMetadata)bsm.getMetadata()).getOdeReceivedAt();

        return Instant.from(formatter.parse(time));
    }

    public int hashMapMessage(OdeMapData map){
        OdeMapPayload payload = (OdeMapPayload)map.getPayload();
        return Objects.hash(payload.toJson());
        
    }

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<Void, OdeBsmData> inputStream = builder.stream(this.props.getKafkaTopicOdeBsmJson(), Consumed.with(Serdes.Void(), JsonSerdes.OdeBsm()));


        KStream<String, OdeBsmData> bsmRekeyedStream = inputStream.selectKey((key, value)->{
                J2735BsmCoreData core = ((J2735Bsm)value.getPayload().getData()).getCoreData();
                return core.getId();
        });

        KStream<String, OdeBsmData> deduplicatedStream = bsmRekeyedStream
            .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.OdeBsm()))
            .aggregate(() -> new OdeBsmPair(new OdeBsmData(), true),
            (key, newValue, aggregate)->{
                
                if(aggregate.getMessage().getMetadata() == null){
                    System.out.println("First Message Forwarding");
                    return new OdeBsmPair(newValue, true);
                }

                Instant newValueTime = getInstantFromBsm(newValue);
                Instant oldValueTime = getInstantFromBsm(aggregate.getMessage());

                // If the messages are more than a certain time apart, forward the new message on
                if(newValueTime.minus(Duration.ofMillis(props.getOdeBsmMaximumTimeDelta())).isAfter(oldValueTime)){
                    System.out.println("Time is Different Forwarding");
                    return new OdeBsmPair(newValue, true );   
                }

                J2735BsmCoreData oldCore = ((J2735Bsm)aggregate.getMessage().getPayload().getData()).getCoreData();
                J2735BsmCoreData newCore = ((J2735Bsm)newValue.getPayload().getData()).getCoreData();

                // If the Vehicle is moving, forward the message on
                if(newCore.getSpeed().doubleValue() > props.getOdeBsmAlwaysIncludeAtSpeed()){
                    System.out.println("Vehicle is Moving, Forwarding");
                    return new OdeBsmPair(newValue, true);    
                }


                double distance = calculateGeodeticDistance(
                    newCore.getPosition().getLatitude().doubleValue(),
                    newCore.getPosition().getLongitude().doubleValue(),
                    oldCore.getPosition().getLatitude().doubleValue(),
                    oldCore.getPosition().getLatitude().doubleValue()
                );

                // If the position delta between the messages is suitable large, forward the message on
                if(distance > props.getOdeBsmMaximumPositionDelta()){
                    System.out.println("Position has Changed Forwarding");
                    return new OdeBsmPair(newValue, true);
                }

                System.out.println("Not Forwarding");
                return new OdeBsmPair(aggregate.getMessage(), false);
                
            }, Materialized.with(Serdes.String(), PairSerdes.OdeBsmPair()))
            .toStream()
            .flatMap((key, value) ->{
                ArrayList<KeyValue<String, OdeBsmData>> outputList = new ArrayList<>();
                if(value != null && value.isShouldSend()){
                    outputList.add(new KeyValue<>(key, value.getMessage()));   
                }
                return outputList;
            });
        
        deduplicatedStream.to(this.props.getKafkaTopicDeduplicatedOdeBsmJson(), Produced.with(Serdes.String(), JsonSerdes.OdeBsm()));

        return builder.build();

    }

    public void stop() {
        logger.info("Stopping Bsm deduplicator Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped Bsm deduplicator Socket Broadcast Topology.");
    }

    StateListener stateListener;
    public void registerStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    StreamsUncaughtExceptionHandler exceptionHandler;
    public void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public double calculateGeodeticDistance(double lat1, double lon1, double lat2, double lon2) {
        
        // Set the starting point
        calculator.setStartingGeographicPoint(lon2, lat2);

        // Set the destination point
        calculator.setDestinationGeographicPoint(lon2, lat2);

        // Get the geodetic distance in meters
        return calculator.getOrthodromicDistance();
    }
}
