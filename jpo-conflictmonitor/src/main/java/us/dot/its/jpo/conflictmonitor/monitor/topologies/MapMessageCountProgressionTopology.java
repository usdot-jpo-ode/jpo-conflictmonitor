package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_revision_counter.MapMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_revision_counter.MapMessageCountProgressionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEvent;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_revision_counter.MapMessageCountProgressionConstants.DEFAULT_MAP_REVISION_COUNTER_ALGORITHM;

import java.util.ArrayList;
import java.util.Objects;

@Component(DEFAULT_MAP_REVISION_COUNTER_ALGORITHM)
public class MapMessageCountProgressionTopology
        extends BaseStreamsTopology<MapMessageCountProgressionParameters>
        implements MapMessageCountProgressionStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(MapMessageCountProgressionTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, ProcessedMap<LineString>> inputStream = builder.stream(parameters.getMapInputTopicName(), Consumed.with(Serdes.String(), JsonSerdes.ProcessedMapGeoJson()));

        KStream<String, MapMessageCountProgressionEvent> eventStream = inputStream
        .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.ProcessedMapGeoJson()))
        .aggregate(() -> new MapMessageCountProgressionEvent(),
        (key, newValue, aggregate) -> {

            aggregate.setMessage(null);
            if (aggregate.getNewMap() == null){
                aggregate.setNewMap(newValue);
                return aggregate;
            }

            //update the aggregate
            aggregate.setPreviousMap(aggregate.getNewMap());
            aggregate.setNewMap(newValue);

            aggregate.getNewMap().getProperties().setTimeStamp(aggregate.getPreviousMap().getProperties().getTimeStamp());
            aggregate.getNewMap().getProperties().setOdeReceivedAt(aggregate.getPreviousMap().getProperties().getOdeReceivedAt());

            int oldHash = Objects.hash(aggregate.getPreviousMap().toString());
            int newHash = Objects.hash(aggregate.getNewMap().toString());

            if (oldHash != newHash){  //Contents of map message have changed
                aggregate.getNewMap().getProperties().setTimeStamp(newValue.getProperties().getTimeStamp());
                aggregate.getNewMap().getProperties().setOdeReceivedAt(newValue.getProperties().getOdeReceivedAt());
                if (aggregate.getNewMap().getProperties().getRevision() == aggregate.getPreviousMap().getProperties().getRevision()) { //Revision has not changed
                    aggregate.setIntersectionID(aggregate.getNewMap().getProperties().getIntersectionId());
                    aggregate.setRoadRegulatorID(-1);
                    aggregate.setMessage("Map message changed without revision increment.");
                    return aggregate;
                }
                else { //Revision has changed
                    return aggregate;
                }
            }
            else { //Map messages are the same
                return aggregate;

            }

        }, Materialized.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.MapMessageCountProgressionEvent()))
        .toStream()
        .flatMap((key, value) ->{
            ArrayList<KeyValue<String, MapMessageCountProgressionEvent>> outputList = new ArrayList<>();
            if (value.getMessage() != null){
                outputList.add(new KeyValue<>(key, value));   
            }
            return outputList;
        });
        eventStream.to(parameters.getMapRevisionEventOutputTopicName(), Produced.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.MapMessageCountProgressionEvent()));

        return builder.build();


    }



}
