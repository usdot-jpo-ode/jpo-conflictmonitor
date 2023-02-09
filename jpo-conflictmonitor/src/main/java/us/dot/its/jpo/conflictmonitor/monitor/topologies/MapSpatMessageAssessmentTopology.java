package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Intersection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneConnection;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.IntersectionReferenceAlignmentNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentConstants.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        if (exceptionHandler != null) streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null) streams.setStateListener(stateListener);
        streams.start();
        logger.info("Started MapSpatMessageAssessment. Topology");
    }

    private Set<Integer> getAsSet(Integer input){
        Set<Integer> outputSet = new HashSet<>();
        if(input != null){
            outputSet.add(input);
        }
        return outputSet;
    }

    private J2735MovementPhaseState getSpatEventStateBySignalGroup(ProcessedSpat spat, int signalGroup){
        for(MovementState state: spat.getStates()){
            if(state.getSignalGroup() == signalGroup){
                List<MovementEvent> movementEvents = state.getStateTimeSpeed(); 
                if(movementEvents.size() > 0){
                    return movementEvents.get(0).getEventState();
                }
            }
        }
        return null;
    }

    private boolean doStatesConflict(J2735MovementPhaseState a, J2735MovementPhaseState b){
        return 
            a.equals(J2735MovementPhaseState.PROTECTED_CLEARANCE) && !b.equals(J2735MovementPhaseState.STOP_AND_REMAIN) ||
            a.equals(J2735MovementPhaseState.PROTECTED_MOVEMENT_ALLOWED) && !b.equals(J2735MovementPhaseState.STOP_AND_REMAIN) ||
            b.equals(J2735MovementPhaseState.PROTECTED_CLEARANCE) && !a.equals(J2735MovementPhaseState.STOP_AND_REMAIN) ||
            b.equals(J2735MovementPhaseState.PROTECTED_CLEARANCE) && !a.equals(J2735MovementPhaseState.STOP_AND_REMAIN);
    }

    public Topology buildTopology() {

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
    KTable<String, ProcessedMap> mapKTable = 
        builder.table(parameters.getMapInputTopicName(), 
        Materialized.with(
            Serdes.String(),
            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMap()
        ));



    //Join Input BSM Stream with Stream of Spat Messages
    KStream<String, SpatMap> spatJoinedMap = 
        processedSpatStream.leftJoin(mapKTable, (spat, map) -> {
            return new SpatMap(spat, map);
        }, 
        Joined.<String, ProcessedSpat, ProcessedMap>as("spat-maps-joined").withKeySerde(Serdes.String())
            .withValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
            .withOtherValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMap())
        );

    // Intersection Reference Alignment Check
    KStream<String, IntersectionReferenceAlignmentEvent> intersectionReferenceAlignmentEventStream = spatJoinedMap.flatMap(
        (key, value)->{
            ArrayList<KeyValue<String, IntersectionReferenceAlignmentEvent>> events = new ArrayList<>();
            IntersectionReferenceAlignmentEvent event = new IntersectionReferenceAlignmentEvent();

            event.setSourceID(key);

            if(value.getMap() != null){
                ProcessedMap map = value.getMap();
                Set<Integer> intersectionIds = new HashSet<>();
                
                // TODO
                // for(MapFeature feature: map.getFeatures()){
                //     intersectionIds.add(feature.getId());
                // }
                // event.setMapRoadRegulatorIds(intersectionIds);

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


    KStream<String, IntersectionReferenceAlignmentNotification> notificationEventStream = intersectionReferenceAlignmentEventStream.flatMap(
            (key, value)->{
                List<KeyValue<String, IntersectionReferenceAlignmentNotification>> result = new ArrayList<KeyValue<String, IntersectionReferenceAlignmentNotification>>();

                IntersectionReferenceAlignmentNotification notification = new IntersectionReferenceAlignmentNotification();
                notification.setEvent(value);
                notification.setNotificationText("Intersection Reference Alignment Notification, generated because corresponding intersection reference alignment event was generated.");
                notification.setNotificationHeading("Intersection Reference Alignment");
                result.add(new KeyValue<>(key, notification));
                return result;
            }
        );
            
        KTable<String, IntersectionReferenceAlignmentNotification> notificationTable = 
            notificationEventStream.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.IntersectionReferenceAlignmentNotification()))
            .reduce(
                (oldValue, newValue)->{
                        return oldValue;
                },
            Materialized.<String, IntersectionReferenceAlignmentNotification, KeyValueStore<Bytes, byte[]>>as("IntersectionReferenceAlignmentNotification")
            .withKeySerde(Serdes.String())
            .withValueSerde(JsonSerdes.IntersectionReferenceAlignmentNotification())
        );

        notificationTable.toStream().to(
            parameters.getIntersectionReferenceAlignmentNotificationTopicName(),
            Produced.with(Serdes.String(),
                    JsonSerdes.IntersectionReferenceAlignmentNotification()));

        if(parameters.isDebug()){
            notificationTable.toStream().print(Printed.toSysOut());
        }


    // Signal Group Alignment Event Check
    KStream<String, SignalGroupAlignmentEvent> signalGroupAlignmentEventStream = spatJoinedMap.flatMap(
        (key, value)->{
            ArrayList<KeyValue<String, SignalGroupAlignmentEvent>> events = new ArrayList<>();
            SignalGroupAlignmentEvent event = new SignalGroupAlignmentEvent();

            event.setSourceID(key);
            event.setTimestamp(SpatTimestampExtractor.getSpatTimestamp(value.getSpat()));
            
            Set<Integer> mapSignalGroups = new HashSet<>();
            Set<Integer> spatSignalGroups = new HashSet<>();

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


    
    // Signal Group Alignment Event Check
    KStream<String, SignalStateConflictEvent> signalStateConflictEventStream = spatJoinedMap.flatMap(
        (key, value)->{
            
            ArrayList<KeyValue<String, SignalStateConflictEvent>> events = new ArrayList<>();

            ProcessedMap map = value.getMap();
            ProcessedSpat spat = value.getSpat();

            if(map == null || spat == null){
                return events;
            }

            Intersection intersection = Intersection.fromProcessedMap(map);
            ArrayList<LaneConnection> connections = intersection.getLaneConnections();
            for(int i =0; i < connections.size(); i++){
                LaneConnection firstConnection = connections.get(i);
                for(int j = i+1; j< connections.size(); j++){
                    LaneConnection secondConnection = connections.get(j);
                    
                    if(firstConnection.crosses(secondConnection)){

                        

                        J2735MovementPhaseState firstState = getSpatEventStateBySignalGroup(spat, firstConnection.getSignalGroup());
                        J2735MovementPhaseState secondState = getSpatEventStateBySignalGroup(spat, secondConnection.getSignalGroup());

                        if(firstState == null || secondState == null){
                            // Skip if the connection is not in the Spat Message
                            continue;
                        }

                        if(doStatesConflict(firstState, secondState)){
                            
                            SignalStateConflictEvent event = new SignalStateConflictEvent();
                            event.setTimestamp(SpatTimestampExtractor.getSpatTimestamp(spat));
                            event.setRoadRegulatorID(intersection.getRoadRegulatorId());
                            event.setIntersectionID(intersection.getIntersectionId());
                            event.setFirstConflictingSignalGroup(firstConnection.getSignalGroup());
                            event.setSecondConflictingSignalGroup(secondConnection.getSignalGroup());
                            event.setFirstConflictingSignalState(firstState);
                            event.setSecondConflictingSignalState(secondState);
                            

                            if(firstState.equals(J2735MovementPhaseState.PROTECTED_MOVEMENT_ALLOWED) || firstState.equals(J2735MovementPhaseState.PROTECTED_CLEARANCE)){
                                event.setConflictType(secondState);
                            }else{
                                event.setConflictType(firstState);
                            }

                            events.add(new KeyValue<String, SignalStateConflictEvent>(key, event));
                            
                        }
                    }
                }
            }

            return events;
        }
    );

    signalStateConflictEventStream.to(
            parameters.getSignalStateConflictEventTopicName(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.SignalStateConflictEvent()));



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

    
    StateListener stateListener;

    @Override
    public void registerStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    StreamsUncaughtExceptionHandler exceptionHandler;

    @Override
    public void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
