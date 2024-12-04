package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Intersection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Lane;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneConnection;
import us.dot.its.jpo.conflictmonitor.monitor.models.RegulatorIntersectionId;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanesPair;
import us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanesPairList;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.IntersectionReferenceAlignmentNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.SignalGroupAlignmentNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.SignalStateConflictNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.connectinglanes.ConnectingLanesFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneTypeAttributes;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.*;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentConstants.DEFAULT_MAP_SPAT_MESSAGE_ASSESSMENT_ALGORITHM;

@Component(DEFAULT_MAP_SPAT_MESSAGE_ASSESSMENT_ALGORITHM)
public class MapSpatMessageAssessmentTopology
        extends BaseStreamsTopology<MapSpatMessageAssessmentParameters>
        implements MapSpatMessageAssessmentStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(MapSpatMessageAssessmentTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }

    private J2735MovementPhaseState getSpatEventStateBySignalGroup(ProcessedSpat spat, int signalGroup) {
        for (MovementState state : spat.getStates()) {
            if (state.getSignalGroup() == signalGroup) {
                List<MovementEvent> movementEvents = state.getStateTimeSpeed();
                if (!movementEvents.isEmpty()) {
                    return movementEvents.getFirst().getEventState();
                }
            }
        }

        if(signalGroup == 255){
            return J2735MovementPhaseState.PROTECTED_MOVEMENT_ALLOWED;
        }

        return null;
    }

//    private String hashLaneConnection(Integer intersectionID, int ingressOne, int ingressTwo, int egressOne, int egressTwo){
//        return intersectionID + "_" + ingressOne + "_" + ingressTwo + "_" + egressOne + "_" + egressTwo;
//    }

    private boolean doStatesConflict(J2735MovementPhaseState a, J2735MovementPhaseState b) {
        return a.equals(J2735MovementPhaseState.PROTECTED_CLEARANCE)
                        && !b.equals(J2735MovementPhaseState.STOP_AND_REMAIN)
                ||
                a.equals(J2735MovementPhaseState.PROTECTED_MOVEMENT_ALLOWED)
                        && !b.equals(J2735MovementPhaseState.STOP_AND_REMAIN)
                ||
                b.equals(J2735MovementPhaseState.PROTECTED_CLEARANCE)
                        && !a.equals(J2735MovementPhaseState.STOP_AND_REMAIN)
                ||
                b.equals(J2735MovementPhaseState.PROTECTED_MOVEMENT_ALLOWED)
                        && !a.equals(J2735MovementPhaseState.STOP_AND_REMAIN);
    }

    private boolean isPedestrian(LaneConnection connection){
        return mapLaneType(connection.getIngressLane().getLaneType()).equals("pedestrian") || mapLaneType(connection.getEgressLane().getLaneType()).equals("pedestrian");
    }

    private boolean isPermissiveState(J2735MovementPhaseState state){
        return state == J2735MovementPhaseState.PERMISSIVE_MOVEMENT_ALLOWED || state == J2735MovementPhaseState.PERMISSIVE_CLEARANCE;
    }
    
    private boolean isProtectedState(J2735MovementPhaseState state){
        return state == J2735MovementPhaseState.PROTECTED_MOVEMENT_ALLOWED || state == J2735MovementPhaseState.PROTECTED_CLEARANCE;
    }

    private boolean isActiveState(J2735MovementPhaseState state){
        return state == J2735MovementPhaseState.PERMISSIVE_CLEARANCE || state == J2735MovementPhaseState.PERMISSIVE_MOVEMENT_ALLOWED || state == J2735MovementPhaseState.PROTECTED_CLEARANCE || state == J2735MovementPhaseState.PROTECTED_MOVEMENT_ALLOWED;
    }

    private boolean doesLaneLaneConnectionConflict(Lane lane, int laneSignalGroup, LaneConnection connection, ProcessedSpat spat){
        J2735MovementPhaseState laneState = getSpatEventStateBySignalGroup(spat, laneSignalGroup);
        J2735MovementPhaseState connectionState = getSpatEventStateBySignalGroup(spat, connection.getSignalGroup());

        if(isActiveState(laneState) && !isPedestrian(connection) && isActiveState(connectionState)){ // Should only apply for designed near side movements
            return true;
        }

        return false;
    }

    public String mapLaneType(J2735LaneTypeAttributes attributes){
        if(attributes.getVehicle() != null){
            return "roadway";
        }else if(attributes.getCrosswalk() != null){
            return "pedestrian";
        }else if(attributes.getBikeLane() != null){
            return "roadway";
        }else if(attributes.getSidewalk() != null){
            return "pedestrian";
        }else if(attributes.getMedian() != null){
            return "roadway";
        }else if(attributes.getStriping() != null){
            return "roadway";
        }else if(attributes.getTrackedVehicle() != null){
            return "roadway";
        }else if(attributes.getParking() != null){
            return "roadway";
        }
        return "Unknown";
    }
    

    public Topology buildTopology() {

        // Populate concurrent permissive allowed from intersection-level config
        ConfigMap<ConnectedLanesPairList> concurrentPermissiveConfigMap = parameters.getConcurrentPermissiveListMap();
        final Set<ConnectedLanesPair> allowConcurrentPermissiveSet = new HashSet<>();
        for (ConnectedLanesPairList list : concurrentPermissiveConfigMap.values()) {
            allowConcurrentPermissiveSet.addAll(list);
        }

        StreamsBuilder builder = new StreamsBuilder();

        // SPaT Input Stream
        KStream<RsuIntersectionKey, ProcessedSpat> processedSpatStream = builder.stream(
                parameters.getSpatInputTopicName(),
                Consumed.with(
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
        );

        // Map table keyed by RsuIntersectionKey
        KTable<RsuIntersectionKey, ProcessedMap<LineString>> mapKTable = builder.table(parameters.getMapInputTopicName(),
                Materialized.with(
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMapGeoJson()));


        // For intersection reference alignment, re-key to RSU-only key to test if intersection ID and region match between
        // MAP and SPaTs from the same RSU
        KTable<String, ProcessedMap<LineString>> mapKTableRsuKey =
            mapKTable.toStream().selectKey((key, value) -> key.getRsuId()).toTable(
                Materialized.with(Serdes.String(), us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMapGeoJson())
        );

        KStream<String, SpatMap> rsuJoinStream =
            processedSpatStream
                .selectKey((key, value) -> key.getRsuId())
                .join(mapKTableRsuKey, (spat, map) -> new SpatMap(spat, map),
                    Joined.<String, ProcessedSpat, ProcessedMap<LineString>>as("spat-maps-joined-rsu")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
                        .withOtherValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMapGeoJson()));

        // Intersection Reference Alignment Check
        KStream<String, IntersectionReferenceAlignmentEvent> intersectionReferenceAlignmentEventStream = rsuJoinStream
                .flatMap(
                        (key, value) -> {
                            ArrayList<KeyValue<String, IntersectionReferenceAlignmentEvent>> events = new ArrayList<>();
                            IntersectionReferenceAlignmentEvent event = new IntersectionReferenceAlignmentEvent();

                            event.setSource(key);

                            RegulatorIntersectionId mapId = new RegulatorIntersectionId();
                            RegulatorIntersectionId spatId = new RegulatorIntersectionId();

                            if(value.getMap() != null && value.getMap().getProperties() != null){
                                ProcessedMap<?> map = value.getMap();
                                mapId.setIntersectionId(map.getProperties().getIntersectionId());
                                mapId.setRoadRegulatorId(map.getProperties().getRegion());

                                if(map.getProperties().getIntersectionId() != null){
                                    event.setIntersectionID(map.getProperties().getIntersectionId());
                                }

                                
                                if(map.getProperties().getRegion() != null){
                                    event.setRoadRegulatorID(map.getProperties().getRegion());
                                }else{
                                    event.setRoadRegulatorID(-1);
                                }
                                
                            }

                            if (value.getSpat() != null){
                                ProcessedSpat spat = value.getSpat();
                                event.setTimestamp(SpatTimestampExtractor.getSpatTimestamp(spat));
                                spatId.setIntersectionId(spat.getIntersectionId());
                                spatId.setRoadRegulatorId(spat.getRegion());

                                if(spat.getIntersectionId() != null){
                                    event.setIntersectionID(spat.getIntersectionId());
                                }

                                if(spat.getRegion() != null){
                                    event.setRoadRegulatorID(spat.getRegion());
                                }else{
                                    event.setRoadRegulatorID(-1);
                                }
                            }
                            
                            Set<RegulatorIntersectionId> mapIdSet = new HashSet<>();
                            mapIdSet.add(mapId);
                            event.setMapRegulatorIntersectionIds(mapIdSet);

                            Set<RegulatorIntersectionId> spatIdSet = new HashSet<>();
                            spatIdSet.add(spatId);
                            event.setSpatRegulatorIntersectionIds(spatIdSet);

                            if (!event.getSpatRegulatorIntersectionIds().equals(event.getMapRegulatorIntersectionIds())) {
                                events.add(new KeyValue<>(key, event));
                            }

                            return events;
                        });

        intersectionReferenceAlignmentEventStream.to(
                parameters.getIntersectionReferenceAlignmentEventTopicName(),
                Produced.with(Serdes.String(),
                        JsonSerdes.IntersectionReferenceAlignmentEvent()));

        KStream<String, IntersectionReferenceAlignmentNotification> notificationEventStream = intersectionReferenceAlignmentEventStream
                .flatMap(
                        (key, value) -> {
                            List<KeyValue<String, IntersectionReferenceAlignmentNotification>> result = new ArrayList<KeyValue<String, IntersectionReferenceAlignmentNotification>>();

                            var notification = new IntersectionReferenceAlignmentNotification();
                            notification.setEvent(value);
                            notification.setNotificationText(
                                    "Intersection Reference Alignment Notification, generated because corresponding intersection reference alignment event was generated.");
                            notification.setNotificationHeading("Intersection Reference Alignment");
                            result.add(new KeyValue<>(key, notification));
                            return result;
                        });

        KTable<String, IntersectionReferenceAlignmentNotification> intersectionNotificationTable = notificationEventStream
                .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.IntersectionReferenceAlignmentNotification()))
                .reduce(
                        (oldValue, newValue) -> {
                            return newValue;
                        },
                        Materialized
                                .<String, IntersectionReferenceAlignmentNotification, KeyValueStore<Bytes, byte[]>>as(
                                        "IntersectionReferenceAlignmentNotification")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.IntersectionReferenceAlignmentNotification()));

        intersectionNotificationTable.toStream().to(
                parameters.getIntersectionReferenceAlignmentNotificationTopicName(),
                Produced.with(Serdes.String(),
                        JsonSerdes.IntersectionReferenceAlignmentNotification()));

        // Join Spats with MAP KTable, RsuIntersectionKey for the Signal Group Alignment check which presume
        // that the Spat and Map are from the same intersection
        KStream<RsuIntersectionKey, SpatMap> spatJoinedMap = processedSpatStream
                .join(mapKTable, (spat, map) -> new SpatMap(spat, map),
                        Joined.<RsuIntersectionKey, ProcessedSpat, ProcessedMap<LineString>>as("spat-maps-joined")
                                .withKeySerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey())
                                .withValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
                                .withOtherValueSerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMapGeoJson()));

        // Signal Group Alignment Event Check
        KStream<String, SignalGroupAlignmentEvent> signalGroupAlignmentEventStream = spatJoinedMap.flatMap(
                (key, value) -> {
                    ArrayList<KeyValue<String, SignalGroupAlignmentEvent>> events = new ArrayList<>();
                    SignalGroupAlignmentEvent event = new SignalGroupAlignmentEvent();

                    event.setSource(key.toString());
                    event.setTimestamp(SpatTimestampExtractor.getSpatTimestamp(value.getSpat()));
                    
                    if(value.getSpat().getIntersectionId()!= null){
                        event.setIntersectionID(value.getSpat().getIntersectionId());
                    }

                    if(value.getSpat().getRegion() != null){
                        event.setRoadRegulatorID(value.getSpat().getRegion());
                    } else {
                        // Missing region = -1 instead of 0
                        event.setRoadRegulatorID(-1);
                    }

                    Set<Integer> mapSignalGroups = new HashSet<>();
                    Set<Integer> spatSignalGroups = new HashSet<>();

                    for (MovementState state : value.getSpat().getStates()) {
                        spatSignalGroups.add(state.getSignalGroup());
                    }

                    if(value.getMap() != null){
                        for(ConnectingLanesFeature<?> objectFeature: value.getMap().getConnectingLanesFeatureCollection().getFeatures()){
                            Integer signalGroupId = objectFeature.getProperties().getSignalGroupId();
                            if (signalGroupId != null) {
                                mapSignalGroups.add(signalGroupId);
                            }
                        }
                    }
                    
                    if (!mapSignalGroups.equals(spatSignalGroups)) {
                        event.setMapSignalGroupIds(mapSignalGroups);
                        event.setSpatSignalGroupIds(spatSignalGroups);
                        event.setSource(key.getRsuId());
                        events.add(new KeyValue<>(key.toString(), event));
                    }

                    return events;
                });

        signalGroupAlignmentEventStream.to(
                parameters.getSignalGroupAlignmentEventTopicName(),
                Produced.with(Serdes.String(),
                        JsonSerdes.SignalGroupAlignmentEvent()));

        KStream<String, SignalGroupAlignmentNotification> signalGroupNotificationEventStream = signalGroupAlignmentEventStream
                .flatMap(
                        (key, value) -> {
                            List<KeyValue<String, SignalGroupAlignmentNotification>> result = new ArrayList<KeyValue<String, SignalGroupAlignmentNotification>>();

                            SignalGroupAlignmentNotification notification = new SignalGroupAlignmentNotification();
                            notification.setEvent(value);
                            notification.setNotificationText(
                                    "Signal Group Alignment Notification, generated because corresponding signal group alignment event was generated.");
                            notification.setNotificationHeading("Signal Group Alignment");
                            result.add(new KeyValue<>(key, notification));
                            return result;
                        });

        KTable<String, SignalGroupAlignmentNotification> signalGroupNotificationTable = signalGroupNotificationEventStream
                .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.SignalGroupAlignmentNotification()))
                .reduce(
                        (oldValue, newValue) -> {
                            return newValue;
                        },
                        Materialized
                                .<String, SignalGroupAlignmentNotification, KeyValueStore<Bytes, byte[]>>as(
                                        "SignalGroupAlignmentNotification")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.SignalGroupAlignmentNotification()));

        signalGroupNotificationTable.toStream().to(
                parameters.getSignalGroupAlignmentNotificationTopicName(),
                Produced.with(Serdes.String(),
                        JsonSerdes.SignalGroupAlignmentNotification()));


        // Signal State Conflict Event Check
        KStream<String, SignalStateConflictEvent> signalStateConflictEventStream = spatJoinedMap.flatMap(
                (key, value) -> {

                    ArrayList<KeyValue<String, SignalStateConflictEvent>> events = new ArrayList<>();

                    ProcessedMap<LineString> map = value.getMap();
                    ProcessedSpat spat = value.getSpat();

                    if (map == null || spat == null) {
                        return events;
                    }

                    Intersection intersection = Intersection.fromProcessedMap(map);
                    ArrayList<LaneConnection> connections = intersection.getLaneConnections();
                    ArrayList<Lane> pedestrianLanes = intersection.getPedestrianLanes();


                    
                    for (int i = 0; i < connections.size(); i++) {
                        LaneConnection firstConnection = connections.get(i);
                        
                        // Compare Lane Connections with one another
                        for (int j = i + 1; j < connections.size(); j++) {
                            LaneConnection secondConnection = connections.get(j);

                            ConnectedLanesPair theseConnectedLanes = new ConnectedLanesPair(
                                    intersection.getIntersectionId(), intersection.getRoadRegulatorId(),
                                    firstConnection.getIngressLane().getId(), firstConnection.getEgressLane().getId(),
                                    secondConnection.getIngressLane().getId(), secondConnection.getEgressLane().getId());
                            
                            
                            // Skip if this connection is defined in the allowable map.
                            if(allowConcurrentPermissiveSet.contains(theseConnectedLanes)){
                                continue;
                            }
                            
                            // Compare between multiple lane connections
                            if (!firstConnection.isPedestrianConnection() && !secondConnection.isPedestrianConnection() && firstConnection.crosses(secondConnection) && firstConnection.getIngressLane() != secondConnection.getIngressLane()) {

                                J2735MovementPhaseState firstState = getSpatEventStateBySignalGroup(spat,
                                        firstConnection.getSignalGroup());
                                J2735MovementPhaseState secondState = getSpatEventStateBySignalGroup(spat,
                                        secondConnection.getSignalGroup());

                                if (firstState == null || secondState == null) {
                                    // Skip if the connection is not in the Spat Message
                                    continue;
                                }

                                if (doStatesConflict(firstState, secondState)) {
                                    SignalStateConflictEvent event = new SignalStateConflictEvent();
                                    event.setTimestamp(SpatTimestampExtractor.getSpatTimestamp(spat));
                                    event.setRoadRegulatorID(intersection.getRoadRegulatorId());
                                    event.setIntersectionID(intersection.getIntersectionId());
                                    event.setFirstConflictingSignalGroup(firstConnection.getSignalGroup());
                                    event.setSecondConflictingSignalGroup(secondConnection.getSignalGroup());
                                    event.setFirstConflictingSignalState(firstState);
                                    event.setSecondConflictingSignalState(secondState);
                                    event.setSource(key.toString());

                                    event.setFirstIngressLane(firstConnection.getIngressLane().getId());
                                    event.setFirstEgressLane(firstConnection.getEgressLane().getId());
                                    event.setFirstIngressLaneType(firstConnection.getIngressLane().getLaneTypeString());
                                    event.setFirstEgressLaneType(firstConnection.getEgressLane().getLaneTypeString());

                                    event.setSecondIngressLane(secondConnection.getIngressLane().getId());
                                    event.setSecondEgressLane(secondConnection.getEgressLane().getId());
                                    event.setSecondIngressLaneType(secondConnection.getIngressLane().getLaneTypeString());
                                    event.setSecondEgressLaneType(secondConnection.getEgressLane().getLaneTypeString());

                                    if (firstState.equals(J2735MovementPhaseState.PROTECTED_MOVEMENT_ALLOWED)
                                            || firstState.equals(J2735MovementPhaseState.PROTECTED_CLEARANCE)) {
                                        event.setConflictType(secondState);
                                    } else {
                                        event.setConflictType(firstState);
                                    }

                                    events.add(new KeyValue<String, SignalStateConflictEvent>(key.toString(), event));

                                }
                            }
                        }
                    }

                    // Compare Lane Connections to Pedestrian Lanes
                    for (Lane pedLane : pedestrianLanes) {
                        Set<Integer> pedLaneSignalGroups = intersection.getSignalGroupsForIngressLane(pedLane);
                        if(pedLaneSignalGroups.size() > 0){
                            for(LaneConnection connection : connections){
                                if(connection.crosses(pedLane)){
                                    for(Integer pedLaneSignalGroup : pedLaneSignalGroups){
                                        if(doesLaneLaneConnectionConflict(pedLane, pedLaneSignalGroup, connection, spat)){
                                            J2735MovementPhaseState laneState = getSpatEventStateBySignalGroup(spat, pedLaneSignalGroup);
                                            J2735MovementPhaseState connectionState = getSpatEventStateBySignalGroup(spat, connection.getSignalGroup());

                                            SignalStateConflictEvent event = new SignalStateConflictEvent();
                                            event.setTimestamp(SpatTimestampExtractor.getSpatTimestamp(spat));
                                            event.setRoadRegulatorID(intersection.getRoadRegulatorId());
                                            event.setIntersectionID(intersection.getIntersectionId());
                                            event.setFirstConflictingSignalGroup(pedLaneSignalGroup);
                                            event.setSecondConflictingSignalGroup(connection.getSignalGroup());
                                            event.setFirstConflictingSignalState(laneState);
                                            event.setSecondConflictingSignalState(connectionState);
                                            event.setSource(key.toString());

                                            event.setFirstIngressLane(pedLane.getId());
                                            event.setFirstEgressLane(pedLane.getId());
                                            event.setFirstIngressLaneType(mapLaneType(pedLane.getLaneType()));
                                            event.setFirstEgressLaneType(mapLaneType(pedLane.getLaneType()));

                                            event.setSecondIngressLane(connection.getIngressLane().getId());
                                            event.setSecondEgressLane(connection.getEgressLane().getId());
                                            event.setSecondIngressLaneType(mapLaneType(connection.getIngressLane().getLaneType()));
                                            event.setSecondEgressLaneType(mapLaneType(connection.getEgressLane().getLaneType()));

                                            if (isProtectedState(laneState)) {
                                                event.setConflictType(connectionState);
                                            } else {
                                                event.setConflictType(laneState);
                                            }

                                            System.out.println("Generating Event" + event);
                                            events.add(new KeyValue<String, SignalStateConflictEvent>(key.toString(), event));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return events;
                });

        signalStateConflictEventStream.to(
                parameters.getSignalStateConflictEventTopicName(),
                Produced.with(Serdes.String(),
                        JsonSerdes.SignalStateConflictEvent()));

        if(parameters.isDebug()){
            signalStateConflictEventStream.print(Printed.toSysOut());
        }

        KStream<String, SignalStateConflictNotification> signalStateConflictNotificationStream = signalStateConflictEventStream
                .flatMap(
                        (key, value) -> {
                            List<KeyValue<String, SignalStateConflictNotification>> result = new ArrayList<KeyValue<String, SignalStateConflictNotification>>();

                            SignalStateConflictNotification notification = new SignalStateConflictNotification();
                            notification.setEvent(value);
                            notification.setNotificationText(
                                    "Signal State Conflict Notification, generated because corresponding signal state conflict event was generated.");
                            notification.setNotificationHeading("Signal State Conflict");
                            result.add(new KeyValue<>(key, notification));
                            return result;
                        });

        if(parameters.isDebug()){
            signalStateConflictNotificationStream.print(Printed.toSysOut());
        }

        KTable<String, SignalStateConflictNotification> signalStateConflictNotificationTable = signalStateConflictNotificationStream
                .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.SignalStateConflictNotification()))
                .reduce(
                        (oldValue, newValue) -> {
                            return newValue;
                        },
                        Materialized
                                .<String, SignalStateConflictNotification, KeyValueStore<Bytes, byte[]>>as(
                                        "SignalStateConflictNotification")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.SignalStateConflictNotification()));


        signalStateConflictNotificationTable.toStream().to(
                parameters.getSignalStateConflictNotificationTopicName(),
                Produced.with(Serdes.String(),
                        JsonSerdes.SignalStateConflictNotification()));

        return builder.build();

    }


}
