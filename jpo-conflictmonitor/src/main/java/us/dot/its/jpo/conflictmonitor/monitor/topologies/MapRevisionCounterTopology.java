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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_revision_counter.MapRevisionCounterParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_revision_counter.MapRevisionCounterStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapRevisionCounterEvent;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_revision_counter.MapRevisionCounterConstants.DEFAULT_MAP_REVISION_COUNTER_ALGORITHM;

import java.util.ArrayList;
import java.util.Objects;

@Component(DEFAULT_MAP_REVISION_COUNTER_ALGORITHM)
public class MapRevisionCounterTopology
        extends BaseStreamsTopology<MapRevisionCounterParameters>
        implements MapRevisionCounterStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(MapRevisionCounterTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, ProcessedMap<LineString>> inputStream = builder.stream(parameters.getMapInputTopicName(), Consumed.with(Serdes.String(), JsonSerdes.ProcessedMapGeoJson()));

        KStream<String, MapRevisionCounterEvent> eventStream = inputStream
        .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.ProcessedMapGeoJson()))
        .aggregate(() -> new MapRevisionCounterEvent(),
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

        }, Materialized.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.MapRevisionCounterEvent()))
        .toStream()
        .flatMap((key, value) ->{
            ArrayList<KeyValue<String, MapRevisionCounterEvent>> outputList = new ArrayList<>();
            if (value.getMessage() != null){
                outputList.add(new KeyValue<>(key, value));   
            }
            return outputList;
        });
        eventStream.to(parameters.getMapRevisionEventOutputTopicName(), Produced.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.MapRevisionCounterEvent()));

        return builder.build();


    }



}
