package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class MessageIngestTopology {
    public static int rekeyCount = 0;
    public static Topology build(
        String odeBsmTopic, String bsmStoreName, 
        String geoJsonSpatTopic, String spatStoreName,
        String geoJsonMapTopic, String mapStroreName) {

        StreamsBuilder builder = new StreamsBuilder();
        
        

        //BSM Input Stream
        KStream<String, OdeBsmData> bsmJsonStream = 
            builder.stream(
                odeBsmTopic, 
                Consumed.with(
                    Serdes.String(),
                    JsonSerdes.OdeBsm())
                    .withTimestampExtractor(new BsmTimestampExtractor())
                );

        //Change the BSM Feed to use the Key Key + ID + Msg Count. This should be unique for every BSM message.
        KStream<String, OdeBsmData> bsmRekeyedStream = bsmJsonStream.selectKey((key, value)->{
            J2735BsmCoreData core = ((J2735Bsm) value.getPayload().getData()).getCoreData();  
            return key +"_"+ core.getId() +"_"+ core.getMsgCnt();
        });

        //Group up all of the BSM's based upon the new ID. Generally speaking this shouldn't change anything as the BSM's have unique keys
        KGroupedStream<String, OdeBsmData> bsmKeyGroup = bsmRekeyedStream.groupByKey();

        //Take the BSM's and Materialize them into a Temporal Time window. The length of the time window shouldn't matter much
        //but enables kafka to temporally query the records later. If there are duplicate keys, the more recent value is taken.
        bsmKeyGroup.windowedBy(TimeWindows.of(Duration.ofSeconds(30)).grace(Duration.ofSeconds(30)))
        .reduce(
            (oldValue, newValue)->{
                rekeyCount +=1;
                System.out.println("Reducer was used" + rekeyCount);
                System.out.println("Old" + oldValue);
                System.out.println("New" + newValue);
                    return newValue;
            },
            Materialized.<String, OdeBsmData, WindowStore<Bytes, byte[]>>as(bsmStoreName)
            .withKeySerde(Serdes.String())
            .withValueSerde(JsonSerdes.OdeBsm())
        );

        
        



        return builder.build();
    }
}
