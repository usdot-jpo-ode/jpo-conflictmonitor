package us.dot.its.jpo.conflictmonitor.monitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;

import us.dot.its.jpo.conflictmonitor.monitor.models.processors.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
// import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.geojson.spat.SpatFeature;
import us.dot.its.jpo.geojsonconverter.geojson.spat.SpatFeatureCollection;
import us.dot.its.jpo.ode.model.OdeBsmData;


public class ConflictTopology {
    
    public static Topology build(String mapGeoJsonTopic, String spatGeoJsonTopic) {
        StreamsBuilder builder = new StreamsBuilder();


        // GeoJson Input Spat Stream
        KStream<String, SpatFeatureCollection> geoJsonSpatStream = 
            builder.stream(
                spatGeoJsonTopic, 
                Consumed.with(
                    Serdes.String(), 
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.SpatGeoJson())
                );

        //geoJsonSpatStream.print(Printed.toSysOut());

        // BSM Input Stream
        KStream<String, OdeBsmData> bsmJsonStream = 
            builder.stream(
                "topic.OdeBsmJson", 
                Consumed.with(
                    Serdes.String(),
                    JsonSerdes.OdeBsm())
                );

        //bsmJsonStream.print(Printed.toSysOut());


        // Group the BSM's by Key (Source IP)
        KGroupedStream<String, OdeBsmData> bsmGroup = 
            bsmJsonStream.groupByKey();

        // Aggregate the BSM's using the BSM Aggregator
        Initializer<BsmAggregator> bsmAggregatorInitializer = BsmAggregator::new;

        Aggregator<String, OdeBsmData, BsmAggregator> currentBsmAdder = 
            (key, value, aggregate) -> aggregate.add(value);


        KTable<String, BsmAggregator> bsmtable = 
            bsmGroup.aggregate(
                bsmAggregatorInitializer,
                currentBsmAdder,
                Materialized.<String, BsmAggregator, KeyValueStore<Bytes, byte[]>>as("current-bsms")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(JsonSerdes.BsmDataAggregator())
            );

        //bsmtable.toStream().print(Printed.toSysOut());


        // Join Input BSM Stream with Stream of Spat Messages
        KStream<String, SpatFeatureCollection> spatJoinedBsms = 
            geoJsonSpatStream.leftJoin(bsmtable, (spatGeoJson, bsmAggregator) -> {
                System.out.println("spat" + spatGeoJson+ "Bsm Aggregator: "+ bsmAggregator);
                return null;
            }, 
            Joined.<String, SpatFeatureCollection, BsmAggregator>as("spat-joined-bsms").withKeySerde(Serdes.String())
                .withValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.SpatGeoJson()).withOtherValueSerde(JsonSerdes.BsmDataAggregator()));

        return builder.build();
    }
}





