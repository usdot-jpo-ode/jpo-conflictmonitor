package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.time.Instant;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class IntersectionEventTopology {

    public static String getBsmID(OdeBsmData value){
        return ((J2735Bsm)value.getPayload().getData()).getCoreData().getId();
    }


    public static Topology build(String bsmEventTopic, ReadOnlyWindowStore bsmWindowStore) {
        
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

                Instant timeFrom = firstBsmTime.minusSeconds(60);
                Instant timeTo = lastBsmTime.plusSeconds(60);

                long firstBsmTimeMillis = firstBsmTime.toEpochMilli();
                long lastBsmTimeMillis = lastBsmTime.toEpochMilli();

                KeyValueIterator<Windowed<String>, OdeBsmData> range = bsmWindowStore.fetchAll(timeFrom, timeTo);

                String checkID = getBsmID(value.getStartingBsm());
                BsmAggregator agg = new BsmAggregator();
                while(range.hasNext()){
                    KeyValue<Windowed<String>, OdeBsmData> next = range.next();
                    long ts = BsmTimestampExtractor.getBsmTimestamp(next.value);

                    if(firstBsmTimeMillis <= ts && lastBsmTimeMillis >= ts && getBsmID(next.value).equals(checkID)){
                        agg.add(next.value);
                    }
                }
                range.close();

                return new KeyValue<>(key, value);
            }
        );
        

        return builder.build();
    }
}
