package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression.EventStateProgressionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression.EventStateProgressionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapBoundingBox;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.store.MapSpatiallyIndexedStateStoreSupplier;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.processors.DiagnosticProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestConstants.DEFAULT_MESSAGE_INGEST_ALGORITHM;

// TODO Use ProcessedBsm
@Component(DEFAULT_MESSAGE_INGEST_ALGORITHM)
public class MessageIngestTopology
        extends BaseStreamsBuilder<MessageIngestParameters>
        implements MessageIngestStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(MessageIngestTopology.class);

    EventStateProgressionStreamsAlgorithm spatTransitionAlgorithm;

    @Override
    public void setEventStateProgressionAlgorithm(EventStateProgressionAlgorithm spatTransitionAlgorithm) {
        // Enforce the algorithm being a Streams algorithm
        if (spatTransitionAlgorithm instanceof EventStateProgressionStreamsAlgorithm spatTransitionStreamsAlgorithm) {
            this.spatTransitionAlgorithm = spatTransitionStreamsAlgorithm;
        } else {
            throw new IllegalArgumentException("Algorithm is not an instance of SpatTransitionStreamsAlgorithm");
        }
    }

    @Override
    public EventStateProgressionAlgorithm getEventStateProgressionAlgorithm() {
        return spatTransitionAlgorithm;
    }

    @Override
    public void buildTopology(StreamsBuilder builder) {


        
        /*
         * 
         * 
         *  BSM MESSAGES
         * 
         */


        //BSM Input Stream
        // Ingest only BSMs within the intersection bounding box
        KStream<BsmIntersectionIdKey, OdeBsmData> bsmJsonStream =
            builder.stream(
                parameters.getBsmTopic(), 
                Consumed.with(
                    JsonSerdes.BsmIntersectionIdKey(),
                    JsonSerdes.OdeBsm())
                    .withTimestampExtractor(new BsmTimestampExtractor())
                );


        

        //Group up all of the BSM's based upon the new ID.
        KGroupedStream<BsmIntersectionIdKey, OdeBsmData> bsmKeyGroup =
                bsmJsonStream.groupByKey(Grouped.with(JsonSerdes.BsmIntersectionIdKey(), JsonSerdes.OdeBsm()));





        //Take the BSM's and Materialize them into a Temporal Time window. The length of the time window shouldn't matter much
        //but enables kafka to temporally query the records later. If there are duplicate keys, the more recent value is taken.
        var bsmWindowed = bsmKeyGroup.windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMillis(1), Duration.ofMillis(60000)))
        .reduce(
            (oldValue, newValue)->{
                System.out.println("Overwriting BSM");
                return newValue;
            },
            Materialized.<BsmIntersectionIdKey, OdeBsmData, WindowStore<Bytes, byte[]>>as(parameters.getBsmStoreName())
                    .withKeySerde(JsonSerdes.BsmIntersectionIdKey())
                    .withValueSerde(JsonSerdes.OdeBsm())
                    .withCachingDisabled()
                    // .withLoggingEnabled(loggingConfig)
                    .withLoggingDisabled()
                    .withRetention(Duration.ofMinutes(10))
        );

        // Check partition of windowed data
        if (parameters.isDebug()) {
            bsmWindowed.toStream().process(() -> new DiagnosticProcessor<>("Windowed BSMs", logger));
        }


         /*
          *
          *
          *  SPAT MESSAGES
          *
          */



        // SPaT Input Stream
        KStream<RsuIntersectionKey, ProcessedSpat> processedSpatStream =
            builder.stream(
                parameters.getSpatTopic(), 
                Consumed.with(
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
                    .withTimestampExtractor(new SpatTimestampExtractor())
                )   // Filter out null SPATs
                    .filter((key, value) -> {
                        if (value == null) {
                            logger.error("Encountered null SPAT");
                            return false;
                        } else {
                            return true;
                        }
                    });

        // Plug Illegal Spat Transition check into here since it needs the stream with event timestamp
        spatTransitionAlgorithm.buildTopology(builder, processedSpatStream);


        if (parameters.isDebug()) {
            processedSpatStream.process(() -> new DiagnosticProcessor<>("ProcessedSpats", logger));
        }

        // Group up all of the Spats's based upon the new key. Generally speaking this shouldn't change anything as the Spats's have unique keys
        KGroupedStream<RsuIntersectionKey, ProcessedSpat> spatKeyGroup =
                processedSpatStream.groupByKey(
                        Grouped.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat()));



        // //Take the Spats's and Materialize them into a Temporal Time window. The length of the time window shouldn't matter much
        // //but enables kafka to temporally query the records later. If there are duplicate keys, the more recent value is taken.
        var spatWindowed = spatKeyGroup.windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMillis(1), Duration.ofMillis(10000)))
        .reduce(
            (oldValue, newValue)->{
                    return newValue;
            },
            Materialized.<RsuIntersectionKey, ProcessedSpat, WindowStore<Bytes, byte[]>>as(parameters.getSpatStoreName())
                    .withKeySerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey())
                    .withValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
                    .withCachingDisabled()
                    .withLoggingDisabled()
                    .withRetention(Duration.ofMinutes(5))
        );

        if (parameters.isDebug()) {
            spatWindowed.toStream().process(() -> new DiagnosticProcessor<>("Windowed SPATs", logger));
        }

        //
        //  MAP MESSAGES
        //

        // Create MAP table for bounding boxes
        builder.table(
                parameters.getMapTopic(),
                Consumed.with(
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMapGeoJson()),
                    Materialized.<RsuIntersectionKey, ProcessedMap<LineString>, KeyValueStore<Bytes, byte[]>>as(parameters.getMapStoreName())
            ).mapValues(
                    map -> new MapBoundingBox(map)
            ).toStream()
                .to(parameters.getMapBoundingBoxTopic(),
                        Produced.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                JsonSerdes.MapBoundingBox(),
                                new IntersectionIdPartitioner<RsuIntersectionKey, MapBoundingBox>()));



        // Read Map Bounding Box Topic into GlobalKTable with spatially indexed state store
        builder.globalTable(parameters.getMapBoundingBoxTopic(),
                Consumed.with(
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        JsonSerdes.MapBoundingBox()),
                Materialized.as(new MapSpatiallyIndexedStateStoreSupplier(
                        parameters.getMapSpatialIndexStoreName(),
                        mapIndex,
                        parameters.getMapBoundingBoxTopic()))
        );


    }



    @Override
    protected Logger getLogger() {
        return logger;
    }




    @Override
    public ReadOnlyWindowStore<BsmIntersectionIdKey, OdeBsmData> getBsmWindowStore(KafkaStreams streams) {
        return streams.store(StoreQueryParameters.fromNameAndType(
            parameters.getBsmStoreName(), QueryableStoreTypes.windowStore()));
    }

    @Override
    public ReadOnlyWindowStore<RsuIntersectionKey, ProcessedSpat> getSpatWindowStore(KafkaStreams streams) {
        return streams.store(StoreQueryParameters.fromNameAndType(
            parameters.getSpatStoreName(), QueryableStoreTypes.windowStore()));
    }

    @Override
    public ReadOnlyKeyValueStore<RsuIntersectionKey, ProcessedMap<LineString>> getMapStore(KafkaStreams streams) {
        return streams.store(StoreQueryParameters.fromNameAndType(
            parameters.getMapStoreName(), QueryableStoreTypes.keyValueStore()));
    }



    private MapIndex mapIndex;


    @Override
    public MapIndex getMapIndex() {
        return mapIndex;
    }

    @Override
    public void setMapIndex(MapIndex mapIndex) {
        this.mapIndex = mapIndex;
    }

    @Override
    public void validate() {
        if (mapIndex == null) {
            throw new IllegalArgumentException("MapIndex is not set");
        }
    }


}
