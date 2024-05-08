package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatRevisionCounterEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;

import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Properties;

public class SpatRevisionCounterTopology {

    private static final Logger logger = LoggerFactory.getLogger(SpatRevisionCounterTopology.class);

    Topology topology;
    KafkaStreams streams;
    String inputTopic;
    String outputTopic;
    Properties streamsProperties;
    ObjectMapper objectMapper;

    public SpatRevisionCounterTopology(String inputTopic, String outputTopic, Properties streamsProperties){
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
        this.streamsProperties = streamsProperties;
        this.objectMapper = new ObjectMapper();
    }

    
    public void start() {
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        if (exceptionHandler != null) streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null) streams.setStateListener(stateListener);
        streams.start();
    }

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, ProcessedSpat> inputStream = builder.stream(inputTopic, Consumed.with(Serdes.String(), JsonSerdes.ProcessedSpat()));

        KStream<String, SpatRevisionCounterEvent> eventStream = inputStream
        .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.ProcessedSpat()))
        .aggregate(() -> new SpatRevisionCounterEvent(),
        (key, newValue, aggregate) -> {

                if (aggregate.getPreviousSpat() == null){
                    aggregate.setPreviousSpat(newValue);
                    return null;
                }

                ZonedDateTime newValueTimestamp = newValue.getUtcTimeStamp();
                String newValueOdeReceivedAt = newValue.getOdeReceivedAt();

                newValue.setUtcTimeStamp(aggregate.getPreviousSpat().getUtcTimeStamp());
                newValue.setOdeReceivedAt(aggregate.getPreviousSpat().getOdeReceivedAt());

                int oldHash = aggregate.getPreviousSpat().hashCode();
                int newHash = newValue.hashCode();

                if (oldHash != newHash){  //Contents of spat message have changed
                    newValue.setUtcTimeStamp(newValueTimestamp);
                    newValue.setOdeReceivedAt(newValueOdeReceivedAt);
                    if (newValue.getRevision() == aggregate.getPreviousSpat().getRevision()) { //Revision has not changed
                        SpatRevisionCounterEvent newEvent = new SpatRevisionCounterEvent();
                        newEvent.setPreviousSpat(aggregate.getPreviousSpat());
                        newEvent.setNewSpat(newValue);
                        newEvent.setMessage("Spat message changed without revision increment.");
                        
                        aggregate.setPreviousSpat(newValue);
                        return newEvent;
                    }
                    else { //Revision has changed
                        aggregate.setPreviousSpat(newValue);

                        return null;
                    }
                }
                else { //Spat messages are the same
                    return null;
                }

            }, Materialized.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.SpatRevisionCounterEvent()))
            .toStream()
            .flatMap((key, value) ->{
                ArrayList<KeyValue<String, SpatRevisionCounterEvent>> outputList = new ArrayList<>();
                if (value != null){
                    outputList.add(new KeyValue<>(key, value));   
                }
                return outputList;
            });
        eventStream.to(outputTopic, Produced.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.SpatRevisionCounterEvent()));

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
