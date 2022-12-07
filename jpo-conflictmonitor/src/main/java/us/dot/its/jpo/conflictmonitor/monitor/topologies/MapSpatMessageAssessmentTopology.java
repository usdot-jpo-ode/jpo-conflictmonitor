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
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentConstants.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


@Component(DEFAULT_MAP_SPAT_MESSAGE_ASSESSMENT_ALGORITHM)
public class MapSpatMessageAssessmentTopology implements MapSpatMessageAssessmentStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(MapSpatMessageAssessmentTopology.class);

    MapSpatMessageAssessmentParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(MapSpatMessageAssessmentParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public MapSpatMessageAssessmentParameters getParameters() {
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
        logger.info("Starting MapSpatMessageAssessment Topology.");
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        streams.start();
        logger.info("Started MapSpatMessageAssessment. Topology");
    }

    private Set<Integer> getAsSet(int input){
        Set<Integer> outputSet = new HashSet<>();
        outputSet.add(input);
        return outputSet;
    }

    private Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();
 
    // SPaT Input Stream
    KStream<String, ProcessedSpat> processedSpatStream = 
    builder.stream(
        parameters.getSpatInputTopicName(), 
        Consumed.with(
            Serdes.String(),
            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
            //.withTimestampExtractor(new SpatTimestampExtractor())
        );

    // Map Input Stream
    KTable<String, MapFeatureCollection> mapKTable = 
        builder.table(parameters.getMapInputTopicName(), 
        Materialized.with(
            Serdes.String(),
            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.MapGeoJson()
        ));



    //Join Input BSM Stream with Stream of Spat Messages
    KStream<String, SpatMap> spatJoinedMap = 
        processedSpatStream.leftJoin(mapKTable, (spat, map) -> {
            return new SpatMap(spat, map);
        }, 
        Joined.<String, ProcessedSpat, MapFeatureCollection>as("spat-maps-joined").withKeySerde(Serdes.String())
            .withValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
            .withOtherValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.MapGeoJson())
        );

    // Intersection Reference Alignment Check
    KStream<String, IntersectionReferenceAlignmentEvent> intersectionReferenceAlignmentEventStream = spatJoinedMap.flatMap(
        (key, value)->{
            ArrayList<KeyValue<String, IntersectionReferenceAlignmentEvent>> events = new ArrayList<>();
            IntersectionReferenceAlignmentEvent event = new IntersectionReferenceAlignmentEvent();

            event.setSourceID(key);

            if(value.getMap() != null){
                MapFeatureCollection map = value.getMap();
                Set<Integer> intersectionIds = new HashSet<>();
                
                for(MapFeature feature: map.getFeatures()){
                    intersectionIds.add(feature.getId());
                }
                event.setMapRoadRegulatorIds(intersectionIds);

            }

            if(value.getSpat() != null){
                ProcessedSpat spat = value.getSpat();
                event.setTimestamp(SpatTimestampExtractor.getSpatTimestamp(spat));
                event.setSpatRoadRegulatorIds(getAsSet(spat.getRegion()));
                event.setSpatIntersectionIds(getAsSet(spat.getIntersectionId()));
            }

            if(!event.getSpatRoadRegulatorIds().equals(event.getMapRoadRegulatorIds()) ||
                !event.getSpatIntersectionIds().equals(event.getMapIntersectionIds())){
                events.add(new KeyValue<>(key, event));
            }

            return events;
        }
    );

    intersectionReferenceAlignmentEventStream.to(
            parameters.getIntersectionReferenceAlignmentEventTopicName(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.IntersectionReferenceAlignmentEvent()));


    // Signal Group Alignment Event Check
    KStream<String, SignalGroupAlignmentEvent> signalGroupAlignmentEventStream = spatJoinedMap.flatMap(
        (key, value)->{
            ArrayList<KeyValue<String, SignalGroupAlignmentEvent>> events = new ArrayList<>();
            SignalGroupAlignmentEvent event = new SignalGroupAlignmentEvent();

            event.setSourceID(key);
            event.setTimestamp(SpatTimestampExtractor.getSpatTimestamp(value.getSpat()));
            
            Set<Integer> mapSignalGroups = new HashSet<>();
            Set<Integer> spatSignalGroups = new HashSet<>();

            for(MapFeature feature: value.getMap().getFeatures()){
                // Current Map Structure Doesn't have Map Signal Groups
            }

            for(MovementState state: value.getSpat().getStates()){
                spatSignalGroups.add(state.getSignalGroup());
            }
            
            if(!mapSignalGroups.equals(spatSignalGroups)){
                event.setMapSignalGroupIds(mapSignalGroups);
                event.setSpatSignalGroupIds(spatSignalGroups);
                events.add(new KeyValue<>(key, event));           
            }

            return events;
        }
    );


    signalGroupAlignmentEventStream.to(
            parameters.getSignalGroupAlignmentEventTopicName(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.SignalGroupAlignmentEvent()));

    return builder.build();

    }

    @Override
    public void stop() {
        logger.info("Stopping MapSpatMessageAssessmentTopology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped MapSpatMessageAssessmentTopology.");
    }

    

}
