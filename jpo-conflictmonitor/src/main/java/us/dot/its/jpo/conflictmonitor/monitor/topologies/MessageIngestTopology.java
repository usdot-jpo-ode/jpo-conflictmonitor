package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestConstants.DEFAULT_MESSAGE_INGEST_ALGORITHM;

@Component(DEFAULT_MESSAGE_INGEST_ALGORITHM)
public class MessageIngestTopology
        extends BaseStreamsTopology<MessageIngestParameters>
        implements MessageIngestStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(MessageIngestTopology.class);
    
    public Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();
        
        /*
         * 
         * 
         *  BSM MESSAGES
         * 
         */

        //BSM Input Stream
        KStream<BsmIntersectionKey, OdeBsmData> bsmJsonStream =
            builder.stream(
                parameters.getBsmTopic(), 
                Consumed.with(
                    JsonSerdes.BsmIntersectionKey(),
                    JsonSerdes.OdeBsm())
                    .withTimestampExtractor(new BsmTimestampExtractor())
                );

        //Group up all of the BSM's based upon the new ID.
        KGroupedStream<BsmIntersectionKey, OdeBsmData> bsmKeyGroup =
                bsmJsonStream.groupByKey(Grouped.with(JsonSerdes.BsmIntersectionKey(), JsonSerdes.OdeBsm()));

        //Take the BSM's and Materialize them into a Temporal Time window. The length of the time window shouldn't matter much
        //but enables kafka to temporally query the records later. If there are duplicate keys, the more recent value is taken.
        bsmKeyGroup.windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMillis(1), Duration.ofMillis(10000)))
        .reduce(
            (oldValue, newValue)->{
                return newValue;
            },
            Materialized.<BsmIntersectionKey, OdeBsmData, WindowStore<Bytes, byte[]>>as(parameters.getBsmStoreName())
                    .withKeySerde(JsonSerdes.BsmIntersectionKey())
                    .withValueSerde(JsonSerdes.OdeBsm())
                    .withCachingDisabled()
                    .withLoggingDisabled()
                    .withRetention(Duration.ofMinutes(5))
        );


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
        

        // Group up all of the Spats's based upon the new key. Generally speaking this shouldn't change anything as the Spats's have unique keys
        KGroupedStream<RsuIntersectionKey, ProcessedSpat> spatKeyGroup =
                processedSpatStream.groupByKey(
                        Grouped.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat()));



        // //Take the Spats's and Materialize them into a Temporal Time window. The length of the time window shouldn't matter much
        // //but enables kafka to temporally query the records later. If there are duplicate keys, the more recent value is taken.
        spatKeyGroup.windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMillis(1), Duration.ofMillis(10000)))
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


        //
        //  MAP MESSAGES
        //
        builder.table(
                parameters.getMapTopic(), 
                Consumed.with(
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMapGeoJson()),
                    Materialized.<RsuIntersectionKey, ProcessedMap<LineString>, KeyValueStore<Bytes, byte[]>>as(parameters.getMapStoreName())
            ).mapValues(map -> {
                mapIndex.insert(map);
                var boundingPolygon = mapIndex.getBoundingPolygon(map);
                var wkt = boundingPolygon.toString();
                return wkt;
            }).toStream()
                .to(parameters.getMapBoundingBoxTopic(),
                        Produced.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                Serdes.String(),
                                new RsuIdPartitioner<RsuIntersectionKey, String>()));

        // TODO read Map Topic into GlobalKTable with spatially indexed GlobalStore
        // Derive custom store from InMemoryKeyValueStore.







        return builder.build();
    }



    @Override
    protected Logger getLogger() {
        return logger;
    }




    @Override
    public ReadOnlyWindowStore<BsmIntersectionKey, OdeBsmData> getBsmWindowStore() {
        return streams.store(StoreQueryParameters.fromNameAndType(
            parameters.getBsmStoreName(), QueryableStoreTypes.windowStore()));
    }

    @Override
    public ReadOnlyWindowStore<RsuIntersectionKey, ProcessedSpat> getSpatWindowStore() {
        return streams.store(StoreQueryParameters.fromNameAndType(
            parameters.getSpatStoreName(), QueryableStoreTypes.windowStore()));
    }

    @Override
    public ReadOnlyKeyValueStore<RsuIntersectionKey, ProcessedMap<LineString>> getMapStore() {
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
    protected void validate() {
        super.validate();
        if (mapIndex == null) {
            throw new IllegalArgumentException("MapIndex is not set");
        }
    }
}
