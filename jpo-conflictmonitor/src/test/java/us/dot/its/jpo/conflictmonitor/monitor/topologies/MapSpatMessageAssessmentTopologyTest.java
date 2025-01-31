package us.dot.its.jpo.conflictmonitor.monitor.topologies;



import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class MapSpatMessageAssessmentTopologyTest {

    String processedMapInputTopicName = "topic.ProcessedMap";
    String processedSpatInputTopicName = "topic.ProcessedSpat";
    String signalGroupAlignmentEventTopicName = "topic.CmSignalGroupAlignmentEvents";
    String signalStateConflictEventTopicName = "topic.CmSignalStateConflictEvent";
    String intersectionReferenceAlignmentEventTopicName = "topic.CmIntersectionReferenceAlignmentEvents";
    String intersectionReferenceAlignmentNotificationTopicName = "topic.CmIntersectionReferenceAlignmentNotification";
    String signalGroupAlignmentNotificationTopicName = "topic.CmSignalGroupAlignmentNotification";
    String signalStateConflictNotificationTopicName = "topic.CmSignalStateConflictNotification";


    String key = "{\"rsuId\":\"10.164.6.76\",\"intersectionId\":6324,\"region\":-1}";
    ObjectMapper objectMapper;

    String referenceMap; 



    String referenceSpat = "{\"validationMessages\":[],\"utcTimeStamp\":1733183919.836,\"schemaVersion\":1,\"recordGeneratedAt\":{\"\":\"2024-12-02T16:41:38.925Z\"},\"revision\":26,\"states\":[{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":1},{\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-03T00:16:18.836Z\",\"maxEndTime\":\"2024-12-03T11:22:58.836Z\"}}],\"signalGroup\":2},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T02:09:38.836Z\",\"maxEndTime\":\"2024-12-03T11:22:58.836Z\"}}],\"signalGroup\":3},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T06:29:38.836Z\",\"maxEndTime\":\"2024-12-03T20:26:18.836Z\"}}],\"signalGroup\":4},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":5},{\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-03T00:49:38.836Z\",\"maxEndTime\":\"2024-12-03T11:56:18.836Z\"}}],\"signalGroup\":6},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T02:42:58.836Z\",\"maxEndTime\":\"2024-12-03T11:56:18.836Z\"}}],\"signalGroup\":7},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T06:57:58.836Z\",\"maxEndTime\":\"2024-12-03T20:54:38.836Z\"}}],\"signalGroup\":8},{\"stateTimeSpeed\":[{\"eventState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-03T00:16:18.836Z\",\"maxEndTime\":\"2024-12-03T11:22:58.836Z\"}}],\"signalGroup\":12},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T06:29:38.836Z\",\"maxEndTime\":\"2024-12-03T20:26:18.836Z\"}}],\"signalGroup\":14},{\"stateTimeSpeed\":[{\"eventState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-03T00:49:38.836Z\",\"maxEndTime\":\"2024-12-03T11:56:18.836Z\"}}],\"signalGroup\":16},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T06:57:58.836Z\",\"maxEndTime\":\"2024-12-03T20:54:38.836Z\"}}],\"signalGroup\":18},{\"stateTimeSpeed\":[{\"eventState\":\"UNAVAILABLE\",\"timing\":{\"minEndTime\":\"2024-11-04T00:46:18.836Z\"}}],\"signalGroup\":25},{\"stateTimeSpeed\":[{\"eventState\":\"UNAVAILABLE\",\"timing\":{\"minEndTime\":\"2024-11-04T00:46:18.836Z\"}}],\"signalGroup\":26},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":202},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":204},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":206},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":208}],\"messageType\":\"SPAT\",\"cti4501Conformant\":false,\"name\":\"S State St & E University Pkwy\",\"intersectionId\":6324,\"odeReceivedAt\":\"2024-12-02T23:58:39.836Z\",\"originIp\":\"10.164.6.76\",\"status\":{\"failureFlash\":false,\"noValidSPATisAvailableAtThisTime\":false,\"fixedTimeOperation\":false,\"standbyOperation\":false,\"trafficDependentOperation\":false,\"manualControlIsEnabled\":false,\"off\":false,\"stopTimeIsActivated\":false,\"recentChangeInMAPassignedLanesIDsUsed\":true,\"recentMAPmessageUpdate\":true,\"failureMode\":false,\"noValidMAPisAvailableAtThisTime\":false,\"signalPriorityIsActive\":false,\"preemptIsActive\":false}}";

    String permissiveCrosswalkSpat = "{\"validationMessages\":[],\"utcTimeStamp\":1733183919.836,\"schemaVersion\":1,\"recordGeneratedAt\":{\"\":\"2024-12-02T16:41:38.925Z\"},\"revision\":26,\"states\":[{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":1},{\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-03T00:16:18.836Z\",\"maxEndTime\":\"2024-12-03T11:22:58.836Z\"}}],\"signalGroup\":2},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T02:09:38.836Z\",\"maxEndTime\":\"2024-12-03T11:22:58.836Z\"}}],\"signalGroup\":3},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T06:29:38.836Z\",\"maxEndTime\":\"2024-12-03T20:26:18.836Z\"}}],\"signalGroup\":4},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":5},{\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-03T00:49:38.836Z\",\"maxEndTime\":\"2024-12-03T11:56:18.836Z\"}}],\"signalGroup\":6},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T02:42:58.836Z\",\"maxEndTime\":\"2024-12-03T11:56:18.836Z\"}}],\"signalGroup\":7},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T06:57:58.836Z\",\"maxEndTime\":\"2024-12-03T20:54:38.836Z\"}}],\"signalGroup\":8},{\"stateTimeSpeed\":[{\"eventState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-03T00:16:18.836Z\",\"maxEndTime\":\"2024-12-03T11:22:58.836Z\"}}],\"signalGroup\":12},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T06:29:38.836Z\",\"maxEndTime\":\"2024-12-03T20:26:18.836Z\"}}],\"signalGroup\":14},{\"stateTimeSpeed\":[{\"eventState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-03T00:49:38.836Z\",\"maxEndTime\":\"2024-12-03T11:56:18.836Z\"}}],\"signalGroup\":16},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-03T06:57:58.836Z\",\"maxEndTime\":\"2024-12-03T20:54:38.836Z\"}}],\"signalGroup\":18},{\"stateTimeSpeed\":[{\"eventState\":\"UNAVAILABLE\",\"timing\":{\"minEndTime\":\"2024-11-04T00:46:18.836Z\"}}],\"signalGroup\":25},{\"stateTimeSpeed\":[{\"eventState\":\"UNAVAILABLE\",\"timing\":{\"minEndTime\":\"2024-11-04T00:46:18.836Z\"}}],\"signalGroup\":26},{\"stateTimeSpeed\":[{\"eventState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":202},{\"stateTimeSpeed\":[{\"eventState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":204},{\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":206},{\"stateTimeSpeed\":[{\"eventState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":208}],\"messageType\":\"SPAT\",\"cti4501Conformant\":false,\"name\":\"SStateSt&EUniversityPkwy\",\"intersectionId\":6324,\"odeReceivedAt\":\"2024-12-02T23:58:39.836Z\",\"originIp\":\"10.164.6.76\",\"status\":{\"failureFlash\":false,\"noValidSPATisAvailableAtThisTime\":false,\"fixedTimeOperation\":false,\"standbyOperation\":false,\"trafficDependentOperation\":false,\"manualControlIsEnabled\":false,\"off\":false,\"stopTimeIsActivated\":false,\"recentChangeInMAPassignedLanesIDsUsed\":true,\"recentMAPmessageUpdate\":true,\"failureMode\":false,\"noValidMAPisAvailableAtThisTime\":false,\"signalPriorityIsActive\":false,\"preemptIsActive\":false}}";
    String testSpat = "{\"validationMessages\":[],\"utcTimeStamp\":1733183919.836,\"schemaVersion\":1,\"recordGeneratedAt\":{\"\":\"2024-12-02T16:41:38.925Z\"},\"revision\":26,\"states\":[{\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-03T00:16:18.836Z\",\"maxEndTime\":\"2024-12-03T11:22:58.836Z\"}}],\"signalGroup\":2},{\"stateTimeSpeed\":[{\"eventState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":\"2024-12-02T23:57:58.836Z\",\"maxEndTime\":\"2024-12-02T23:57:58.836Z\"}}],\"signalGroup\":204}],\"messageType\":\"SPAT\",\"cti4501Conformant\":false,\"name\":\"SStateSt&EUniversityPkwy\",\"intersectionId\":6324,\"odeReceivedAt\":\"2024-12-02T23:58:39.836Z\",\"originIp\":\"10.164.6.76\",\"status\":{\"failureFlash\":false,\"noValidSPATisAvailableAtThisTime\":false,\"fixedTimeOperation\":false,\"standbyOperation\":false,\"trafficDependentOperation\":false,\"manualControlIsEnabled\":false,\"off\":false,\"stopTimeIsActivated\":false,\"recentChangeInMAPassignedLanesIDsUsed\":true,\"recentMAPmessageUpdate\":true,\"failureMode\":false,\"noValidMAPisAvailableAtThisTime\":false,\"signalPriorityIsActive\":false,\"preemptIsActive\":false}}";

    String conflictEvent1 = "{\"eventGeneratedAt\":1733327399062,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":25,\"firstConflictingSignalState\":\"UNAVAILABLE\",\"secondConflictingSignalGroup\":2,\"secondConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":7,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":28,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":13,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":40,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent2 = "{\"eventGeneratedAt\":1733327399062,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":25,\"firstConflictingSignalState\":\"UNAVAILABLE\",\"secondConflictingSignalGroup\":2,\"secondConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":7,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":28,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":14,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":39,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent3 = "{\"eventGeneratedAt\":1733327399062,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":25,\"firstConflictingSignalState\":\"UNAVAILABLE\",\"secondConflictingSignalGroup\":2,\"secondConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":7,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":28,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":15,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":38,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent4 = "{\"eventGeneratedAt\":1733327399063,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":25,\"firstConflictingSignalState\":\"UNAVAILABLE\",\"secondConflictingSignalGroup\":6,\"secondConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":7,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":28,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":33,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":20,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent5 = "{\"eventGeneratedAt\":1733327399063,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":25,\"firstConflictingSignalState\":\"UNAVAILABLE\",\"secondConflictingSignalGroup\":6,\"secondConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":7,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":28,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":34,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":19,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent6 = "{\"eventGeneratedAt\":1733327399063,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":25,\"firstConflictingSignalState\":\"UNAVAILABLE\",\"secondConflictingSignalGroup\":6,\"secondConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":7,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":28,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":35,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":18,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent7 = "{\"eventGeneratedAt\":1733327399073,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":2,\"firstConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"secondConflictingSignalGroup\":26,\"secondConflictingSignalState\":\"UNAVAILABLE\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":13,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":40,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":27,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":8,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent8 = "{\"eventGeneratedAt\":1733327399074,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":2,\"firstConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"secondConflictingSignalGroup\":26,\"secondConflictingSignalState\":\"UNAVAILABLE\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":14,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":39,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":27,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":8,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent9 = "{\"eventGeneratedAt\":1733327399075,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":2,\"firstConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"secondConflictingSignalGroup\":26,\"secondConflictingSignalState\":\"UNAVAILABLE\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":15,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":38,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":27,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":8,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent10 = "{\"eventGeneratedAt\":1733327399082,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":26,\"firstConflictingSignalState\":\"UNAVAILABLE\",\"secondConflictingSignalGroup\":6,\"secondConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":27,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":8,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":33,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":20,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent11 = "{\"eventGeneratedAt\":1733327399082,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":26,\"firstConflictingSignalState\":\"UNAVAILABLE\",\"secondConflictingSignalGroup\":6,\"secondConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":27,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":8,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":34,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":19,\"secondEgressLaneType\":\"roadway\"}";
    String conflictEvent12 = "{\"eventGeneratedAt\":1733327399083,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"UNAVAILABLE\",\"firstConflictingSignalGroup\":26,\"firstConflictingSignalState\":\"UNAVAILABLE\",\"secondConflictingSignalGroup\":6,\"secondConflictingSignalState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":27,\"firstIngressLaneType\":\"roadway\",\"firstEgressLane\":8,\"firstEgressLaneType\":\"roadway\",\"secondIngressLane\":35,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":18,\"secondEgressLaneType\":\"roadway\"}";


    String pedEvent1 = "{\"eventGeneratedAt\":1733265359848,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"firstConflictingSignalGroup\":202,\"firstConflictingSignalState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"secondConflictingSignalGroup\":12,\"secondConflictingSignalState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":42,\"firstIngressLaneType\":\"pedestrian\",\"firstEgressLane\":42,\"firstEgressLaneType\":\"pedestrian\",\"secondIngressLane\":12,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":9,\"secondEgressLaneType\":\"roadway\"}";
    String pedEvent2 = "{\"eventGeneratedAt\":1733265359850,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"firstConflictingSignalGroup\":202,\"firstConflictingSignalState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"secondConflictingSignalGroup\":12,\"secondConflictingSignalState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":42,\"firstIngressLaneType\":\"pedestrian\",\"firstEgressLane\":42,\"firstEgressLaneType\":\"pedestrian\",\"secondIngressLane\":12,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":10,\"secondEgressLaneType\":\"roadway\"}";
    String pedEvent3 = "{\"eventGeneratedAt\":1733265359850,\"eventType\":\"SignalStateConflict\",\"intersectionID\":6324,\"roadRegulatorID\":-1,\"timestamp\":1733183919836,\"conflictType\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"firstConflictingSignalGroup\":202,\"firstConflictingSignalState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"secondConflictingSignalGroup\":12,\"secondConflictingSignalState\":\"PERMISSIVE_MOVEMENT_ALLOWED\",\"source\":\"{ rsuId='10.164.6.76', intersectionId='6324', region='-1'}\",\"firstIngressLane\":42,\"firstIngressLaneType\":\"pedestrian\",\"firstEgressLane\":42,\"firstEgressLaneType\":\"pedestrian\",\"secondIngressLane\":12,\"secondIngressLaneType\":\"roadway\",\"secondEgressLane\":11,\"secondEgressLaneType\":\"roadway\"}";


    @Autowired
    ConflictMonitorProperties props;

    @Mock
    MapSpatMessageAssessmentAlgorithm mapSpatMessageAssessmentAlgorithm;
    MapSpatMessageAssessmentParameters mapSpatMessageAssessmentParameters = new MapSpatMessageAssessmentParameters();


    public MapSpatMessageAssessmentTopologyTest(){
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource("ReferenceMap.txt").toURI());
            referenceMap =  Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void compareSignalStateEvents(SignalStateConflictEvent a, SignalStateConflictEvent b){
        assertEquals(a.getEventType(), b.getEventType() );
        assertEquals(a.getIntersectionID(), b.getIntersectionID() );
        assertEquals(a.getRoadRegulatorID(), b.getRoadRegulatorID() );
        assertEquals(a.getConflictType(), b.getConflictType() );
        assertEquals(a.getFirstConflictingSignalGroup(), b.getFirstConflictingSignalGroup() );
        assertEquals(a.getSecondConflictingSignalGroup(), b.getSecondConflictingSignalGroup() );
        assertEquals(a.getFirstIngressLane(), b.getFirstIngressLane() );
        assertEquals(a.getSecondIngressLane(), b.getSecondIngressLane() );
        assertEquals(a.getFirstEgressLane(), b.getFirstEgressLane() );
        assertEquals(a.getSecondEgressLane(), b.getSecondEgressLane() );
        assertEquals(a.getFirstIngressLaneType(), b.getFirstIngressLaneType() );
        assertEquals(a.getSecondIngressLaneType(), b.getSecondIngressLaneType() );
        assertEquals(a.getFirstEgressLaneType(), b.getFirstEgressLaneType() );
        assertEquals(a.getSecondEgressLaneType(), b.getSecondEgressLaneType() );
    }

    

    @Test
    public void testSignalStateConflictEventTopology() {
        ConflictMonitorProperties conflictMonitorProperties = new ConflictMonitorProperties();
        conflictMonitorProperties.setKafkaTopicProcessedMap(processedMapInputTopicName);
        conflictMonitorProperties.setKafkaTopicProcessedSpat(processedSpatInputTopicName);
        conflictMonitorProperties.setKafkaTopicCmSignalStateEvent(signalStateConflictEventTopicName);

        MapSpatMessageAssessmentParameters parameters = new MapSpatMessageAssessmentParameters();
        parameters.setMapInputTopicName(processedMapInputTopicName);
        parameters.setSpatInputTopicName(processedSpatInputTopicName);
        parameters.setSignalGroupAlignmentEventTopicName(signalGroupAlignmentEventTopicName);
        parameters.setIntersectionReferenceAlignmentEventTopicName(intersectionReferenceAlignmentEventTopicName);
        parameters.setSignalStateConflictEventTopicName(signalStateConflictEventTopicName);
        parameters.setIntersectionReferenceAlignmentNotificationTopicName(intersectionReferenceAlignmentNotificationTopicName);
        parameters.setSignalGroupAlignmentNotificationTopicName(signalGroupAlignmentNotificationTopicName);
        parameters.setSignalStateConflictNotificationTopicName(signalStateConflictNotificationTopicName);
        

        MapSpatMessageAssessmentTopology mapSpatMessageAssessmentTopology = new MapSpatMessageAssessmentTopology();
        mapSpatMessageAssessmentTopology.setParameters(parameters);
        

    
        Topology topology = mapSpatMessageAssessmentTopology.buildTopology();


        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            
            TestInputTopic<String, String> inputProcessedMapData = driver.createInputTopic(
                processedMapInputTopicName, 
                Serdes.String().serializer(),
                Serdes.String().serializer());
            
            TestInputTopic<String, String> inputProcessedSpatData = driver.createInputTopic(
                processedSpatInputTopicName, 
                Serdes.String().serializer(),
                Serdes.String().serializer());


            TestOutputTopic<String, SignalStateConflictEvent> outputSignalStateConflictEvents = driver.createOutputTopic(
                signalStateConflictEventTopicName, 
                Serdes.String().deserializer(), 
                JsonSerdes.SignalStateConflictEvent().deserializer());

            objectMapper = new ObjectMapper();

            inputProcessedMapData.pipeInput(key, referenceMap);
            inputProcessedSpatData.pipeInput(key, referenceSpat);

            List<KeyValue<String, SignalStateConflictEvent>> signalStateConflictEvents = outputSignalStateConflictEvents.readKeyValuesToList();

            // // validate that only 12 messages make it through
            assertEquals(12, signalStateConflictEvents.size());

            SignalStateConflictEvent event1 = objectMapper.readValue(conflictEvent1, SignalStateConflictEvent.class);
            SignalStateConflictEvent event2 = objectMapper.readValue(conflictEvent2, SignalStateConflictEvent.class);
            SignalStateConflictEvent event3 = objectMapper.readValue(conflictEvent3, SignalStateConflictEvent.class);
            SignalStateConflictEvent event4 = objectMapper.readValue(conflictEvent4, SignalStateConflictEvent.class);
            SignalStateConflictEvent event5 = objectMapper.readValue(conflictEvent5, SignalStateConflictEvent.class);
            SignalStateConflictEvent event6 = objectMapper.readValue(conflictEvent6, SignalStateConflictEvent.class);
            SignalStateConflictEvent event7 = objectMapper.readValue(conflictEvent7, SignalStateConflictEvent.class);
            SignalStateConflictEvent event8 = objectMapper.readValue(conflictEvent8, SignalStateConflictEvent.class);
            SignalStateConflictEvent event9 = objectMapper.readValue(conflictEvent9, SignalStateConflictEvent.class);
            SignalStateConflictEvent event10 = objectMapper.readValue(conflictEvent10, SignalStateConflictEvent.class);
            SignalStateConflictEvent event11 = objectMapper.readValue(conflictEvent11, SignalStateConflictEvent.class);
            SignalStateConflictEvent event12 = objectMapper.readValue(conflictEvent12, SignalStateConflictEvent.class);


            compareSignalStateEvents(event1, signalStateConflictEvents.get(0).value);
            compareSignalStateEvents(event2, signalStateConflictEvents.get(1).value);
            compareSignalStateEvents(event3, signalStateConflictEvents.get(2).value);
            compareSignalStateEvents(event4, signalStateConflictEvents.get(3).value);
            compareSignalStateEvents(event5, signalStateConflictEvents.get(4).value);
            compareSignalStateEvents(event6, signalStateConflictEvents.get(5).value);
            compareSignalStateEvents(event7, signalStateConflictEvents.get(6).value);
            compareSignalStateEvents(event8, signalStateConflictEvents.get(7).value);
            compareSignalStateEvents(event9, signalStateConflictEvents.get(8).value);
            compareSignalStateEvents(event10, signalStateConflictEvents.get(9).value);
            compareSignalStateEvents(event11, signalStateConflictEvents.get(10).value);
            compareSignalStateEvents(event12, signalStateConflictEvents.get(11).value);

            // for(KeyValue<String,SignalStateConflictEvent> event : signalStateConflictEvents){
            //     System.out.println(event.key + " " + event.value.toString());
            // }

            inputProcessedSpatData.pipeInput(key, permissiveCrosswalkSpat);
            signalStateConflictEvents = outputSignalStateConflictEvents.readKeyValuesToList();
            assertEquals(15, signalStateConflictEvents.size());
            
            SignalStateConflictEvent event13 = objectMapper.readValue(pedEvent1, SignalStateConflictEvent.class);
            SignalStateConflictEvent event14 = objectMapper.readValue(pedEvent2, SignalStateConflictEvent.class);
            SignalStateConflictEvent event15 = objectMapper.readValue(pedEvent3, SignalStateConflictEvent.class);

            int length = signalStateConflictEvents.size();

            compareSignalStateEvents(event13, signalStateConflictEvents.get(length - 3).value);
            compareSignalStateEvents(event14, signalStateConflictEvents.get(length - 2).value);
            compareSignalStateEvents(event15, signalStateConflictEvents.get(length - 1).value);

           
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}