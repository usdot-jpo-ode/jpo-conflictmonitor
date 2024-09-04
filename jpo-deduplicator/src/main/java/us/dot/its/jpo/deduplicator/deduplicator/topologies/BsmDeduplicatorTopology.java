package us.dot.its.jpo.deduplicator.deduplicator.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.model.OdeMapPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.geotools.referencing.GeodeticCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers.OdeBsmJsonProcessorSupplier;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public class BsmDeduplicatorTopology {

    private static final Logger logger = LoggerFactory.getLogger(BsmDeduplicatorTopology.class);

    Topology topology;
    KafkaStreams streams;
    DeduplicatorProperties props;
    ObjectMapper objectMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    GeodeticCalculator calculator;
    


    public BsmDeduplicatorTopology(DeduplicatorProperties props){
        this.props = props;
        this.objectMapper = DateJsonMapper.getInstance();
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

        builder.addStateStore(Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(props.getKafkaStateStoreOdeBsmJsonName()),
                Serdes.String(), JsonSerdes.OdeBsm()));

        KStream<String, OdeBsmData> bsmRekeyedStream = inputStream.selectKey((key, value)->{
                J2735BsmCoreData core = ((J2735Bsm)value.getPayload().getData()).getCoreData();
                return core.getId();
        }).repartition(Repartitioned.with(Serdes.String(), JsonSerdes.OdeBsm()));

        KStream<String, OdeBsmData> deduplicatedStream = bsmRekeyedStream.process(new OdeBsmJsonProcessorSupplier(props.getKafkaStateStoreOdeBsmJsonName(), props), props.getKafkaStateStoreOdeBsmJsonName());

        
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
        calculator.setStartingGeographicPoint(lon1, lat1);
        calculator.setDestinationGeographicPoint(lon2, lat2);
        return calculator.getOrthodromicDistance();
    }
}
