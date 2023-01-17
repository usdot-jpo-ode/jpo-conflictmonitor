package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Intersection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneConnection;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;
import org.apache.kafka.streams.kstream.Printed;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionConstants.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;


@Component(DEFAULT_REPARTITION_ALGORITHM)
public class RepartitionTopology implements RepartitionStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(RepartitionTopology.class);

    RepartitionParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(RepartitionParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public RepartitionParameters getParameters() {
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
        logger.info("Starting Repartition Topology.");
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        streams.start();
        logger.info("Started Repartition Topology");
        System.out.println(parameters.getBsmInputTopicName());
    }

    private Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();
 
        KStream<String, OdeBsmData> bsmRepartitionStream = 
        builder.stream(
            parameters.getBsmInputTopicName(), 
            Consumed.with(
                Serdes.String(),
                JsonSerdes.OdeBsm())
            );


        KStream<RsuIntersectionKey, OdeBsmData> bsmRekeyedStream = bsmRepartitionStream.selectKey((key, value)->{
            J2735BsmCoreData core = ((J2735Bsm) value.getPayload().getData()).getCoreData();
            String ip = ((OdeBsmMetadata)value.getMetadata()).getOriginIp();
            return new RsuIntersectionKey(ip, 0);
        });

        bsmRekeyedStream.to(
            parameters.getBsmRepartitionOutputTopicName(), 
            Produced.with(
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                JsonSerdes.OdeBsm(),
                new RsuIdPartitioner<RsuIntersectionKey, OdeBsmData>()
            )
        );



    return builder.build();

    }

    @Override
    public void stop() {
        logger.info("Stopping BSMRepartitionTopology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped BSMRepartitionTopology.");
    }

    

}
