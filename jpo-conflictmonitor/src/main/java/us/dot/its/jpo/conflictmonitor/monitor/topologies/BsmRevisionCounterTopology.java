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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_revision_counter.BsmRevisionCounterParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_revision_counter.BsmRevisionCounterStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmRevisionCounterEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_revision_counter.BsmRevisionCounterConstants.DEFAULT_BSM_REVISION_COUNTER_ALGORITHM;

import java.util.ArrayList;
import java.util.Objects;

@Component(DEFAULT_BSM_REVISION_COUNTER_ALGORITHM)
public class BsmRevisionCounterTopology
        extends BaseStreamsTopology<BsmRevisionCounterParameters>
        implements BsmRevisionCounterStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(BsmRevisionCounterTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, OdeBsmData> inputStream = builder.stream(parameters.getBsmInputTopicName(), Consumed.with(Serdes.String(), JsonSerdes.OdeBsm()));

        KStream<String, BsmRevisionCounterEvent> eventStream = inputStream
        .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.OdeBsm()))
        .aggregate(() -> new BsmRevisionCounterEvent(),
        (key, newValue, aggregate) -> {
            aggregate.setMessage(null);
            if (aggregate.getNewBsm() == null){
                aggregate.setNewBsm(newValue);
                return aggregate;
            }

            //update the aggregate
            aggregate.setPreviousBsm(aggregate.getNewBsm());
            aggregate.setNewBsm(newValue);

            J2735Bsm previousBsmPayload = (J2735Bsm) aggregate.getPreviousBsm().getPayload().getData();
            J2735Bsm newBsmPayload = (J2735Bsm) aggregate.getNewBsm().getPayload().getData();

            int newSecMark = newBsmPayload.getCoreData().getSecMark();

            newBsmPayload.getCoreData().setSecMark(previousBsmPayload.getCoreData().getSecMark());
            aggregate.getNewBsm().getMetadata().setOdeReceivedAt(aggregate.getPreviousBsm().getMetadata().getOdeReceivedAt());

            int oldMetadataHash = Objects.hash(aggregate.getPreviousBsm().getMetadata().toJson());
            int newMetadataHash = Objects.hash(aggregate.getNewBsm().getMetadata().toJson());
            int oldPayloadHash = Objects.hash(previousBsmPayload.toJson());
            int newPayloadHash = Objects.hash(newBsmPayload.toJson());

            if (oldPayloadHash != newPayloadHash || oldMetadataHash != newMetadataHash){  //Contents of bsm message have changed
                newBsmPayload.getCoreData().setSecMark(newSecMark);
                aggregate.getNewBsm().getMetadata().setOdeReceivedAt(newValue.getMetadata().getOdeReceivedAt());
                if (previousBsmPayload.getCoreData().getMsgCnt() == newBsmPayload.getCoreData().getMsgCnt()) { //Revision has not changed
                    aggregate.setMessage("Bsm message changed without msgCount increment.");
                    aggregate.setRoadRegulatorID(-1);
                    return aggregate;
                }
                else { //Revision has changed
                    return aggregate;
                }
            }
            else { //Bsm messages are the same
                return aggregate;

            }

        }, Materialized.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRevisionCounterEvent()))
        .toStream()
        .flatMap((key, value) ->{
            ArrayList<KeyValue<String, BsmRevisionCounterEvent>> outputList = new ArrayList<>();
            if (value.getMessage() != null){
                outputList.add(new KeyValue<>(key, value));   
            }
            return outputList;
        });
        eventStream.to(parameters.getBsmRevisionEventOutputTopicName(), Produced.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRevisionCounterEvent()));

        return builder.build();
    }

    public void stop() {
        logger.info("Stopping Bsm Revision Counter Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped Bsm Revision Counter Socket Broadcast Topology.");
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
