package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;

import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
//import us.dot.its.jpo.geojsonconverter.geojson.spat.SpatFeatureCollection;


public class ConflictTopology {
    
    public static Topology build(String mapGeoJsonTopic, String spatGeoJsonTopic) {
        StreamsBuilder builder = new StreamsBuilder();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

        

        // GeoJson Input Spat Stream
        // KStream<String, SpatFeatureCollection> geoJsonSpatStream = 
        //     builder.stream(
        //         spatGeoJsonTopic, 
        //         Consumed.with(
        //             Serdes.String(), 
        //             us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.SpatGeoJson())
        //         );

        //geoJsonSpatStream.print(Printed.toSysOut());

        // BSM Input Stream
        // KStream<String, OdeBsmData> bsmJsonStream = 
        //     builder.stream(
        //         "topic.OdeBsmJson", 
        //         Consumed.with(
        //             Serdes.String(),
        //             JsonSerdes.OdeBsm())
        //         );

        //bsmJsonStream.print(Printed.toSysOut());


        // Group the BSM's by Key (Source IP)
        // KGroupedStream<String, OdeBsmData> bsmGroup = 
        //     bsmJsonStream.groupByKey();

        // Aggregate the BSM's using the BSM Aggregator
        // Initializer<BsmAggregator> bsmAggregatorInitializer = BsmAggregator::new;

        // Aggregator<String, OdeBsmData, BsmAggregator> currentBsmAdder = 
        //     (key, value, aggregate) -> aggregate.add(value);

        // Aggregator<String, OdeBsmData, BsmAggregator> currentBsmSubtractor = 
        //     (key, value, aggregate) -> aggregate.subtract(value);


        // KTable<String, BsmAggregator> bsmtable = 
        //     bsmGroup.aggregate(
        //         bsmAggregatorInitializer,
        //         currentBsmAdder,
        //         Materialized.<String, BsmAggregator, KeyValueStore<Bytes, byte[]>>as("current-bsms")
        //                 .withKeySerde(Serdes.String())
        //                 .withValueSerde(JsonSerdes.BsmDataAggregator())
        //     );


        //
        KStream<String, String> windowedStream = builder.stream(
            "topic.OdeBsmJson",
            Consumed.with(Serdes.String(), JsonSerdes.OdeBsm()))
            .groupByKey()
            .windowedBy(SessionWindows.with(Duration.ofSeconds(10)).grace(Duration.ofSeconds(5)))
            // .aggregate(
            //     bsmAggregatorInitializer,
            //     currentBsmAdder,
            //     Materialized.<String, BsmAggregator, KeyValueStore<Bytes, byte[]>>as("current-bsms")
            //             .withKeySerde(Serdes.String())
            //             .withValueSerde(JsonSerdes.BsmDataAggregator())
            // )
            
            .count()
            //.suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded().shutDownWhenFull()))
            .toStream()
            .map((windowedKey, count)->{
                System.out.println("Count" + count);
                //String start = timeFormatter.format(windowedKey.window().startTime());
                //String end = timeFormatter.format(windowedKey.window().endTime());
                //String sessionInfo = String.format("Session info started: %s ended: %s with count %s", start, end, count);
                String sessionInfo = "StartTime" + windowedKey.window().startTime() +"EndTime: "+ windowedKey.window().endTime()+ " Count: " + count;
                return KeyValue.pair(windowedKey.key(), sessionInfo);
            })
            ;

        windowedStream.print(Printed.toSysOut());
        

        //bsmtable.toStream().print(Printed.toSysOut());


        // Join Input BSM Stream with Stream of Spat Messages
        // KStream<String, SpatFeatureCollection> spatJoinedBsms = 
        //     geoJsonSpatStream.leftJoin(bsmtable, (spatGeoJson, bsmAggregator) -> {
        //         //System.out.println("spat" + spatGeoJson+ "Bsm Aggregator: "+ bsmAggregator.getBsms().size());
        //         System.out.println("Bsms" + bsmAggregator.getBsms().size());
        //         System.out.println("Newest" + bsmAggregator.getBsms().first());
        //         System.out.println("Oldest"+ bsmAggregator.getBsms().last());


        //         return null;
        //     }, 
        //     Joined.<String, SpatFeatureCollection, BsmAggregator>as("spat-joined-bsms").withKeySerde(Serdes.String())
        //         .withValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.SpatGeoJson()).withOtherValueSerde(JsonSerdes.BsmDataAggregator()));

        return builder.build();
    }
}





