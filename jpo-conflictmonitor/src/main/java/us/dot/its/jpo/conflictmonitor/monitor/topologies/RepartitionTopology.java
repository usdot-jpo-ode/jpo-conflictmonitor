package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;
import us.dot.its.jpo.ode.model.OdeBsmData;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionConstants.DEFAULT_REPARTITION_ALGORITHM;

@Component(DEFAULT_REPARTITION_ALGORITHM)
public class RepartitionTopology
        extends BaseStreamsTopology<RepartitionParameters>
        implements RepartitionStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(RepartitionTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }



    @Override
    public Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, OdeBsmData> bsmRepartitionStream =
        builder.stream(
            parameters.getBsmInputTopicName(),
            Consumed.with(
                Serdes.String(),
                JsonSerdes.OdeBsm())
            );


        KStream<BsmRsuIdKey, OdeBsmData> bsmRekeyedStream = bsmRepartitionStream.selectKey((key, value)->{
            String ip = BsmUtils.getRsuIp(value);
            String bsmId = BsmUtils.getVehicleId(value);
            return new BsmRsuIdKey(ip, bsmId);
        });


        bsmRekeyedStream.to(
            parameters.getBsmRepartitionOutputTopicName(),
            Produced.with(
                JsonSerdes.BsmRsuIdKey(),
                JsonSerdes.OdeBsm(),
                new RsuIdPartitioner<BsmRsuIdKey, OdeBsmData>()
            )
        );



        return builder.build();

    }


}
