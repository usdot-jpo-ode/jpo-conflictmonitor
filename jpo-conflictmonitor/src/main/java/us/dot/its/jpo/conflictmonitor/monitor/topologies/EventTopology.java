package us.dot.its.jpo.conflictmonitor.monitor.topologies;


import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event.EventParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event.EventStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.event.EventConstants.DEFAULT_EVENT_ALGORITHM;


@Component(DEFAULT_EVENT_ALGORITHM)
public class EventTopology
        extends BaseStreamsTopology<EventParameters>
        implements EventStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(EventTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }

    public Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Event> cotStream = builder.stream(parameters.getConnectionOfTravelEventTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event()));
        KStream<String, Event> allEvents = cotStream
            .merge(builder.stream(parameters.getConnectionOfTravelEventTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))
            .merge(builder.stream(parameters.getIntersectionReferenceAlignmentEventTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))
            .merge(builder.stream(parameters.getLaneDirectionOfTravelEventTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))
            .merge(builder.stream(parameters.getSignalGroupAlignmentEventTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))
            .merge(builder.stream(parameters.getSignalStateEventTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))
            .merge(builder.stream(parameters.getSignalStateConflictEventTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))
            .merge(builder.stream(parameters.getSpatTimeChangeDetailsTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))
            .merge(builder.stream(parameters.getMapMinimumDataTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))
            .merge(builder.stream(parameters.getSpatMinimumDataTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))
            .merge(builder.stream(parameters.getMapBroadcastRateTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))
            .merge(builder.stream(parameters.getSpatBroadcastRateTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))   
            .merge(builder.stream(parameters.getSpatRevisionCounterEventTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())))    
            .merge(builder.stream(parameters.getMapRevisionCounterEventTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Event())));    

        allEvents.to(parameters.getEventOutputTopicName(), Produced.with(Serdes.String(), JsonSerdes.Event()));
        if(parameters.isDebug()){
            allEvents.print(Printed.toSysOut());
        }
        


        return builder.build();

    }



}
