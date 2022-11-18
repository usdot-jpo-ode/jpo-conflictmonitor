package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStore;

import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Intersection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.StopLine;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class IntersectionEventTopology {

    public static String getBsmID(OdeBsmData value){
        return ((J2735Bsm)value.getPayload().getData()).getCoreData().getId();
    }

    public static BsmAggregator getBsmsByTimeVehicle(ReadOnlyWindowStore bsmWindowStore, Instant start, Instant end, String id){

        Instant timeFrom = start.minusSeconds(60);
        Instant timeTo = start.plusSeconds(60);

        long startMillis = start.toEpochMilli();
        long endMillis = end.toEpochMilli();

        KeyValueIterator<Windowed<String>, OdeBsmData> bsmRange = bsmWindowStore.fetchAll(timeFrom, timeTo);

        BsmAggregator agg = new BsmAggregator();
        while(bsmRange.hasNext()){
            KeyValue<Windowed<String>, OdeBsmData> next = bsmRange.next();
            long ts = BsmTimestampExtractor.getBsmTimestamp(next.value);

            if(startMillis <= ts && endMillis >= ts && getBsmID(next.value).equals(id)){
                agg.add(next.value);
            }
        }
        bsmRange.close();

        return agg;
    }

    public static SpatAggregator getSpatByTime(ReadOnlyWindowStore spatWindowStore, Instant start, Instant end){

        Instant timeFrom = start.minusSeconds(60);
        Instant timeTo = start.plusSeconds(60);

        long startMillis = start.toEpochMilli();
        long endMillis = end.toEpochMilli();

        KeyValueIterator<Windowed<String>, ProcessedSpat> spatRange = spatWindowStore.fetchAll(timeFrom, timeTo);

        SpatAggregator spatAggregator = new SpatAggregator();
        while(spatRange.hasNext()){
            KeyValue<Windowed<String>, ProcessedSpat> next = spatRange.next();
            long ts = SpatTimestampExtractor.getSpatTimestamp(next.value);


            //if(startMillis <= ts && endMillis >= ts){ Add this back in later once geojson converter timestamps are fixed
                spatAggregator.add(next.value);
            //}
        }
        spatRange.close();

        return spatAggregator;
    }


    public static MapFeatureCollection getMap(ReadOnlyKeyValueStore mapStore, String key){
        return (MapFeatureCollection) mapStore.get(key);
    }


    public static Topology build(String bsmEventTopic, ReadOnlyWindowStore bsmWindowStore, ReadOnlyWindowStore spatWindowStore, ReadOnlyKeyValueStore mapStore) {
        
        StreamsBuilder builder = new StreamsBuilder();

        
        KStream<String, BsmEvent> bsmEventStream = 
            builder.stream(
                bsmEventTopic, 
                Consumed.with(
                    Serdes.String(),
                    JsonSerdes.BsmEvent())
                );


        KStream<String, BsmEvent> intersectionState = bsmEventStream.map(
            (key, value)->{
                if(value.getStartingBsm() == null || value.getEndingBsm() == null){
                    System.out.println("Received Event with No Ending. Skipping");
                    return new KeyValue<>(key, value);
                }

                Instant firstBsmTime = Instant.ofEpochMilli(BsmTimestampExtractor.getBsmTimestamp(value.getStartingBsm()));
                Instant lastBsmTime = Instant.ofEpochMilli(BsmTimestampExtractor.getBsmTimestamp(value.getEndingBsm()));

                MapFeatureCollection map = null;
                BsmAggregator bsms = getBsmsByTimeVehicle(bsmWindowStore, firstBsmTime, lastBsmTime, getBsmID(value.getStartingBsm()));
                SpatAggregator spats = getSpatByTime(spatWindowStore, firstBsmTime, lastBsmTime);

                if(spats.getSpats().size() > 0){
                    ProcessedSpat firstSpat = spats.getSpats().first();
                    String ip = firstSpat.getOriginIp();
                    int intersectionId = firstSpat.getIntersectionId();

                    String mapLookupKey = ip +":"+ intersectionId;
                    System.out.println("Looking for Map with Key: " + mapLookupKey);
                    map = getMap(mapStore, mapLookupKey);
                }


                System.out.println("Detected Vehicle Event");
                System.out.println("Captured Bsms:  " + bsms.getBsms().size());
                System.out.println("Captured Spats: " + spats.getSpats().size());
                if(map != null){
                    Intersection intersection = Intersection.fromMapFeatureCollection(map);
                    System.out.println(intersection);
                    VehiclePath path = new VehiclePath(bsms, intersection);
                }else{
                    System.out.println("Map was Null");
                }
                

                return new KeyValue<>(key, value);
            }
        );
        

        return builder.build();
    }
}
