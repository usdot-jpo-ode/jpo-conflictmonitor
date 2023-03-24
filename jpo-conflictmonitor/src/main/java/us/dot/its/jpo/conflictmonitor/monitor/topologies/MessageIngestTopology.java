package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestConstants.*;

@Component(DEFAULT_MESSAGE_INGEST_ALGORITHM)
public class MessageIngestTopology implements MessageIngestStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(MessageIngestTopology.class);
    
    //public static int rekeyCount = 0;
    public Topology build() {

        StreamsBuilder builder = new StreamsBuilder();
        
        /*
         * 
         * 
         *  BSMS MESSAGES
         * 
         */

        //BSM Input Stream
        KStream<Void, OdeBsmData> bsmJsonStream = 
            builder.stream(
                parameters.getBsmTopic(), 
                Consumed.with(
                    Serdes.Void(),
                    JsonSerdes.OdeBsm())
                    .withTimestampExtractor(new BsmTimestampExtractor())
                );

        //Change the BSM Feed to use the Key Key + ID + Msg Count. This should be unique for every BSM message.
        KStream<String, OdeBsmData> bsmRekeyedStream = bsmJsonStream.selectKey((key, value)->{
            J2735BsmCoreData core = ((J2735Bsm) value.getPayload().getData()).getCoreData();
            String ip = ((OdeBsmMetadata)value.getMetadata()).getOriginIp();
            return ip+"_"+core.getId() +"_"+ BsmTimestampExtractor.getBsmTimestamp(value);
        });

        //Group up all of the BSM's based upon the new ID. Generally speaking this shouldn't change anything as the BSM's have unique keys
        KGroupedStream<String, OdeBsmData> bsmKeyGroup = bsmRekeyedStream.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.OdeBsm()));

        //Take the BSM's and Materialize them into a Temporal Time window. The length of the time window shouldn't matter much
        //but enables kafka to temporally query the records later. If there are duplicate keys, the more recent value is taken.
        bsmKeyGroup.windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofSeconds(30), Duration.ofSeconds(30)))
        .reduce(
            (oldValue, newValue)->{
                return newValue;
            },
            Materialized.<String, OdeBsmData, WindowStore<Bytes, byte[]>>as(parameters.getBsmStoreName())
            .withKeySerde(Serdes.String())
            .withValueSerde(JsonSerdes.OdeBsm())
        );

        // //bsmRekeyedStream.print(Printed.toSysOut());

        // /*
        //  * 
        //  * 
        //  *  SPAT MESSAGES
        //  * 
        //  */



        // //SPaT Input Stream
        KStream<String, ProcessedSpat> processedSpatStream = 
            builder.stream(
                parameters.getSpatTopic(), 
                Consumed.with(
                    Serdes.String(),
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
                    .withTimestampExtractor(new SpatTimestampExtractor())
                );
        

        // //Change the Spat Feed to use the Key Key + ID + UTC Time String. This should be unique for every Spat message.
        KStream<String, ProcessedSpat> spatRekeyedStream = processedSpatStream.selectKey((key, value)->{
            long ts = SpatTimestampExtractor.getSpatTimestamp(value);
            String newKey = key +"_"+ value.getIntersectionId() +"_"+ ts;
            return newKey;
        });

        // //Group up all of the Spats's based upon the new ID. Generally speaking this shouldn't change anything as the Spats's have unique keys
        KGroupedStream<String, ProcessedSpat> spatKeyGroup = spatRekeyedStream.groupByKey(Grouped.with(Serdes.String(), us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat()));

        // //Take the Spats's and Materialize them into a Temporal Time window. The length of the time window shouldn't matter much
        // //but enables kafka to temporally query the records later. If there are duplicate keys, the more recent value is taken.
        spatKeyGroup.windowedBy(TimeWindows.of(Duration.ofSeconds(30)).grace(Duration.ofSeconds(30)))
        .reduce(
            (oldValue, newValue)->{
                    return newValue;
            },
            Materialized.<String, ProcessedSpat, WindowStore<Bytes, byte[]>>as(parameters.getSpatStoreName())
            .withKeySerde(Serdes.String())
            .withValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
        );

        // processedSpatStream.print(Printed.toSysOut());

        

        // /*
        //  * 
        //  * 
        //  *  MAP MESSAGES
        //  * 
        //  */

        KStream<String, ProcessedMap> mapJsonStream = 
            builder.stream(
                parameters.getMapTopic(), 
                Consumed.with(
                    Serdes.String(),
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMap())
                );
            
        // //Group up all of the Maps's based upon the new ID. 
        KGroupedStream<String, ProcessedMap> mapKeyGroup = mapJsonStream.groupByKey(Grouped.with(Serdes.String(), us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMap()));

        KTable<String, ProcessedMap> maptable = 
            mapKeyGroup
            .reduce(
                (oldValue, newValue)->{
                        return newValue;
                },
            Materialized.<String, ProcessedMap, KeyValueStore<Bytes, byte[]>>as(parameters.getMapStoreName())
            .withKeySerde(Serdes.String())
            .withValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMap())
            );


        return builder.build();
    }

    MessageIngestParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;
    StateListener stateListener;
    StreamsUncaughtExceptionHandler exceptionHandler;

    @Override
    public void start() {
        if (parameters == null) {
            throw new IllegalStateException("Start called before setting parameters.");
        }
        if (streamsProperties == null) {
            throw new IllegalStateException("Streams properties are not set.");
        }
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
        logger.info("Starting MessageIngest Topology.");
        Topology topology = build();
        streams = new KafkaStreams(topology, streamsProperties);
        if (exceptionHandler != null) streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null) streams.setStateListener(stateListener);
        streams.start();
        logger.info("Started MessageIngest Topology");
    }
    @Override
    public void stop() {
        logger.info("Stopping MessageIngest Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped MessageIngest Topology.");
    }
    @Override
    public void setParameters(MessageIngestParameters parameters) {
        this.parameters = parameters;
    }
    @Override
    public MessageIngestParameters getParameters() {
        return parameters;
    }
    @Override
    public void setStreamsProperties(Properties streamsProperties) {
        this.streamsProperties = streamsProperties;
    }
    @Override
    public Properties getStreamsProperties() {
        return streamsProperties;
    }
    @Override
    public KafkaStreams getStreams() {
        return streams;
    }
    @Override
    public void registerStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }
    @Override
    public void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public ReadOnlyWindowStore<String, OdeBsmData> getBsmWindowStore() {
        return streams.store(StoreQueryParameters.fromNameAndType(
            parameters.getBsmStoreName(), QueryableStoreTypes.windowStore()));
    }

    @Override
    public ReadOnlyWindowStore<String, ProcessedSpat> getSpatWindowStore() {
        return streams.store(StoreQueryParameters.fromNameAndType(
            parameters.getSpatStoreName(), QueryableStoreTypes.windowStore()));
    }

    @Override
    public ReadOnlyKeyValueStore<String, ProcessedMap> getMapStore() {
        return streams.store(StoreQueryParameters.fromNameAndType(
            parameters.getMapStoreName(), QueryableStoreTypes.keyValueStore()));
    }
}
