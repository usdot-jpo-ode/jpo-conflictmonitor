package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapRevisionCounterEvent;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;

import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Properties;

public class MapRevisionCounterTopology {

    private static final Logger logger = LoggerFactory.getLogger(MapRevisionCounterTopology.class);

    Topology topology;
    KafkaStreams streams;
    String inputTopic;
    String outputTopic;
    Properties streamsProperties;
    ObjectMapper objectMapper;

    public MapRevisionCounterTopology(String inputTopic, String outputTopic, Properties streamsProperties){
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

        KStream<String, ProcessedMap<LineString>> inputStream = builder.stream(inputTopic, Consumed.with(Serdes.String(), JsonSerdes.ProcessedMapGeoJson()));

        KStream<String, MapRevisionCounterEvent> eventStream = inputStream
        .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.ProcessedMapGeoJson()))
        .aggregate(() -> new MapRevisionCounterEvent(),
        (key, newValue, aggregate) -> {

                if (aggregate.getPreviousMap() == null){
                    aggregate.setPreviousMap(newValue);
                    return null;
                }

                ZonedDateTime newValueTimestamp = newValue.getProperties().getTimeStamp();
                ZonedDateTime newValueOdeReceivedAt = newValue.getProperties().getOdeReceivedAt();

                newValue.getProperties().setTimeStamp(aggregate.getPreviousMap().getProperties().getTimeStamp());
                newValue.getProperties().setOdeReceivedAt(aggregate.getPreviousMap().getProperties().getOdeReceivedAt());

                int oldHash = aggregate.getPreviousMap().getProperties().hashCode();
                int newHash = newValue.getProperties().hashCode();

                if (oldHash != newHash){  //Contents of Map message have changed
                    newValue.getProperties().setTimeStamp(newValueTimestamp);
                    newValue.getProperties().setOdeReceivedAt(newValueOdeReceivedAt);
                    if (newValue.getProperties().getRevision() == aggregate.getPreviousMap().getProperties().getRevision()) { //Revision has not changed
                        MapRevisionCounterEvent newEvent = new MapRevisionCounterEvent();
                        newEvent.setPreviousMap(aggregate.getPreviousMap());
                        newEvent.setNewMap(newValue);
                        newEvent.setMessage("Map message changed without revision increment.");
                        
                        aggregate.setPreviousMap(newValue);
                        return newEvent;
                    }
                    else { //Revision has changed
                        aggregate.setPreviousMap(newValue);

                        return null;
                    }
                }
                else { //Map messages are the same
                    return null;
                }

            }, Materialized.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.MapRevisionCounterEvent()))
            .toStream()
            .flatMap((key, value) ->{
                ArrayList<KeyValue<String, MapRevisionCounterEvent>> outputList = new ArrayList<>();
                if (value != null){
                    outputList.add(new KeyValue<>(key, value));   
                }
                return outputList;
            });
        eventStream.to(outputTopic, Produced.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.MapRevisionCounterEvent()));

        return builder.build();
    }

    public void stop() {
        logger.info("Stopping Map Revision Counter Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped Map Revision Counter Socket Broadcast Topology.");
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
