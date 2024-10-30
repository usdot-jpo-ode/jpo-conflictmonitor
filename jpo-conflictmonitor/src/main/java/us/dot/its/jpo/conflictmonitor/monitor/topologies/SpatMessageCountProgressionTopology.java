package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_revision_counter.SpatMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_revision_counter.SpatMessageCountProgressionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_revision_counter.SpatMessageCountProgressionConstants.DEFAULT_SPAT_REVISION_COUNTER_ALGORITHM;

import java.util.ArrayList;

@Component(DEFAULT_SPAT_REVISION_COUNTER_ALGORITHM)
public class SpatMessageCountProgressionTopology
        extends BaseStreamsTopology<SpatMessageCountProgressionParameters>
        implements SpatMessageCountProgressionStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SpatMessageCountProgressionTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, ProcessedSpat> inputStream = builder.stream(parameters.getSpatInputTopicName(), Consumed.with(Serdes.String(), JsonSerdes.ProcessedSpat()));

        KStream<String, SpatMessageCountProgressionEvent> eventStream = inputStream
        .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.ProcessedSpat()))
        .aggregate(() -> new SpatMessageCountProgressionEvent(),
        (key, newValue, aggregate) -> {

            aggregate.setMessage(null);
            if (aggregate.getNewSpat() == null){
                aggregate.setNewSpat(newValue);
                return aggregate;
            }

            //update the aggregate
            aggregate.setPreviousSpat(aggregate.getNewSpat());
            aggregate.setNewSpat(newValue);

            aggregate.getNewSpat().setUtcTimeStamp(aggregate.getPreviousSpat().getUtcTimeStamp());
            aggregate.getNewSpat().setOdeReceivedAt(aggregate.getPreviousSpat().getOdeReceivedAt());

            int oldHash = aggregate.getPreviousSpat().hashCode();
            int newHash = aggregate.getNewSpat().hashCode();

            if (oldHash != newHash){  //Contents of spat message have changed
                aggregate.getNewSpat().setUtcTimeStamp(newValue.getUtcTimeStamp());
                aggregate.getNewSpat().setOdeReceivedAt(newValue.getOdeReceivedAt());
                if (aggregate.getNewSpat().getRevision() == aggregate.getPreviousSpat().getRevision()) { //Revision has not changed
                    aggregate.setIntersectionID(aggregate.getNewSpat().getIntersectionId());
                    aggregate.setRoadRegulatorID(-1);
                    aggregate.setMessage("Spat message changed without revision increment.");
                    
                    return aggregate;
                }
                else { //Revision has changed
                    return aggregate;
                }
            }
            else { //Spat messages are the same
                return aggregate;

            }

        }, Materialized.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.SpatMessageCountProgressionEvent()))
        .toStream()
        .flatMap((key, value) ->{
            ArrayList<KeyValue<String, SpatMessageCountProgressionEvent>> outputList = new ArrayList<>();
            if (value.getMessage() != null){
                outputList.add(new KeyValue<>(key, value));   
            }
            return outputList;
        });
        eventStream.to(parameters.getSpatRevisionEventOutputTopicName(), Produced.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.SpatMessageCountProgressionEvent()));

        return builder.build();
    }

    public void stop() {
        logger.info("Stopping Spat Revision Counter Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped Spat Revision Counter Socket Broadcast Topology.");
    }

    StateListener stateListener;
    public void registerStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    StreamsUncaughtExceptionHandler exceptionHandler;
    public void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }



}
