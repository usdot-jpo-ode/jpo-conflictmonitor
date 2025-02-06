package us.dot.its.jpo.conflictmonitor.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;


import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression.EventStateProgressionAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression.EventStateProgressionAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.*;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.map.MapMinimumDataAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.map.MapMinimumDataAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.spat.SpatMinimumDataAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.spat.SpatMinimumDataAggregationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event.EventAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event.EventAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event.EventParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event.IntersectionEventAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event.IntersectionEventStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression.EventStateProgressionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression.EventStateProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression.EventStateProgressionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationStreamsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigInitializer;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigTopology;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test for {@link MonitorServiceController}
 */
@RunWith(MockitoJUnitRunner.class)
public class MonitorServiceControllerTest {

    @Mock ConflictMonitorProperties conflictMonitorProperties;
    @Mock KafkaTemplate<String, String> kafkaTemplate;
    @Mock ConfigTopology configTopology;
    @Mock ConfigParameters configParameters;
    @Mock
    ConfigInitializer configInitializer;


    @Mock RepartitionAlgorithmFactory repartitionAlgorithmFactory;
    @Mock RepartitionStreamsAlgorithm repartitionAlgorithm;
    RepartitionParameters repartitionParameters = new RepartitionParameters();

    @Mock NotificationAlgorithmFactory notificationAlgorithmFactory;
    @Mock NotificationStreamsAlgorithm notificationAlgorithm;
    NotificationParameters notificationParameters = new NotificationParameters();

    @Mock MapValidationAlgorithmFactory mapValidationAlgorithmFactory;
    @Mock MapValidationStreamsAlgorithm mapValidationAlgorithm;
    MapValidationParameters mapValidationParameters = new MapValidationParameters();

    @Mock MapTimestampDeltaAlgorithmFactory mapTimestampDeltaAlgorithmFactory;
    @Mock MapTimestampDeltaStreamsAlgorithm mapTimestampDeltaStreamsAlgorithm;
    MapTimestampDeltaParameters mapTimestampDeltaParameters = new MapTimestampDeltaParameters();

    @Mock SpatValidationStreamsAlgorithmFactory spatValidationStreamsAlgorithmFactory;
    @Mock SpatValidationStreamsAlgorithm spatValidationAlgorithm;
    SpatValidationParameters spatValidationParameters = new SpatValidationParameters();

    @Mock SpatTimestampDeltaAlgorithmFactory spatTimestampDeltaAlgorithmFactory;
    @Mock SpatTimestampDeltaStreamsAlgorithm spatTimestampDeltaStreamsAlgorithm;
    SpatTimestampDeltaParameters spatTimestampDeltaParameters = new SpatTimestampDeltaParameters();

    AggregationParameters aggregationParameters = new AggregationParameters();
    @Mock EventStateProgressionAggregationAlgorithmFactory eventStateProgressionAggregationAlgorithmFactory;
    @Mock EventStateProgressionAggregationAlgorithm eventStateProgressionAggregationAlgorithm;
    @Mock SpatMinimumDataAggregationAlgorithmFactory spatMinimumDataAggregationAlgorithmFactory;
    @Mock SpatMinimumDataAggregationAlgorithm spatMinimumDataAggregationAlgorithm;
    @Mock MapMinimumDataAggregationAlgorithmFactory mapMinimumDataAggregationAlgorithmFactory;
    @Mock MapMinimumDataAggregationAlgorithm mapMinimumDataAggregationAlgorithm;
    @Mock TimeChangeDetailsAggregationAlgorithmFactory timeChangeDetailsAggregationAlgorithmFactory;
    @Mock TimeChangeDetailsAggregationAlgorithm timeChangeDetailsAggregationAlgorithm;
    @Mock IntersectionReferenceAlignmentAggregationAlgorithmFactory intersectionReferenceAlignmentAggregationAlgorithmFactory;
    @Mock IntersectionReferenceAlignmentAggregationAlgorithm intersectionReferenceAlignmentAggregationAlgorithm;
    @Mock SignalGroupAlignmentAggregationAlgorithmFactory signalGroupAlignmentAggregationAlgorithmFactory;
    @Mock SignalGroupAlignmentAggregationAlgorithm signalGroupAlignmentAggregationAlgorithm;
    @Mock SignalStateConflictAggregationAlgorithmFactory signalStateConflictAggregationAlgorithmFactory;
    @Mock SignalStateConflictAggregationAlgorithm signalStateConflictAggregationAlgorithm;
    @Mock MapMessageCountProgressionAggregationAlgorithmFactory mapMessageCountProgressionAggregationAlgorithmFactory;
    @Mock MapMessageCountProgressionAggregationAlgorithm mapMessageCountProgressionAggregationAlgorithm;
    @Mock SpatMessageCountProgressionAggregationAlgorithmFactory spatMessageCountProgressionAggregationAlgorithmFactory;
    @Mock SpatMessageCountProgressionAggregationAlgorithm spatMessageCountProgressionAggregationAlgorithm;
    @Mock BsmMessageCountProgressionAggregationAlgorithmFactory bsmMessageCountProgressionAggregationAlgorithmFactory;
    @Mock BsmMessageCountProgressionAggregationAlgorithm bsmMessageCountProgressionAggregationAlgorithm;


    @Mock SpatTimeChangeDetailsAlgorithmFactory spatTimeChangeDetailsAlgorithmFactory;
    @Mock SpatTimeChangeDetailsStreamsAlgorithm spatTimeChangeDetailsAlgorithm;
    SpatTimeChangeDetailsParameters spatTimeChangeDetailsParameters = new SpatTimeChangeDetailsParameters();

    @Mock MapSpatMessageAssessmentAlgorithmFactory mapSpatMessageAssessmentAlgorithmFactory;
    @Mock MapSpatMessageAssessmentStreamsAlgorithm mapSpatMessageAssessmentAlgorithm;
    MapSpatMessageAssessmentParameters mapSpatMessageAssessmentParameters = new MapSpatMessageAssessmentParameters();

    @Mock BsmEventAlgorithmFactory bsmEventAlgorithmFactory;
    @Mock BsmEventStreamsAlgorithm bsmEventAlgorithm;
    BsmEventParameters bsmEventParameters = new BsmEventParameters();

    @Mock MessageIngestAlgorithmFactory messageIngestAlgorithmFactory;
    @Mock MessageIngestStreamsAlgorithm messageIngestAlgorithm;
    MessageIngestParameters messageIngestParameters = new MessageIngestParameters();

    @Mock
    EventStateProgressionAlgorithmFactory spatTransitionAlgorithmFactory;
    @Mock
    EventStateProgressionStreamsAlgorithm spatTransitionAlgorithm;
    EventStateProgressionParameters spatTransitionParameters = new EventStateProgressionParameters();

    @Mock LaneDirectionOfTravelAlgorithmFactory laneDirectionOfTravelAlgorithmFactory;
    @Mock LaneDirectionOfTravelAlgorithm laneDirectionOfTravelAlgorithm;
    LaneDirectionOfTravelParameters laneDirectionOfTravelParameters = new LaneDirectionOfTravelParameters();

    @Mock ConnectionOfTravelAlgorithmFactory connectionOfTravelAlgorithmFactory;
    @Mock ConnectionOfTravelAlgorithm connectionOfTravelAlgorithm;
    ConnectionOfTravelParameters connectionOfTravelParameters = new ConnectionOfTravelParameters();

    @Mock
    StopLinePassageAlgorithmFactory signalStateVehicleCrossesAlgorithmFactory;
    @Mock
    StopLinePassageAlgorithm signalStateVehicleCrossesAlgorithm;
    StopLinePassageParameters signalStateVehicleCrossesParameters = new StopLinePassageParameters();

    @Mock
    StopLineStopAlgorithmFactory signalStateVehicleStopsAlgorithmFactory;
    @Mock
    StopLineStopAlgorithm signalStateVehicleStopsAlgorithm;
    StopLineStopParameters signalStateVehicleStopsParameters = new StopLineStopParameters();

    @Mock IntersectionEventAlgorithmFactory intersectionEventAlgorithmFactory;
    @Mock IntersectionEventStreamsAlgorithm intersectionEventAlgorithm;
    
    @Mock StopLinePassageAssessmentAlgorithmFactory signalStateEventAssessmentAlgorithmFactory;
    @Mock StopLinePassageAssessmentStreamsAlgorithm signalStateEventAssessmentAlgorithm;
    StopLinePassageAssessmentParameters signalStateEventAssessmentParameters = new StopLinePassageAssessmentParameters();

    @Mock LaneDirectionOfTravelAssessmentAlgorithmFactory laneDirectionOfTravelAssessmentAlgorithmFactory;
    @Mock LaneDirectionOfTravelAssessmentStreamsAlgorithm laneDirectionOfTravelAssessmentAlgorithm;
    LaneDirectionOfTravelAssessmentParameters laneDirectionOfTravelAssessmentParameters = new LaneDirectionOfTravelAssessmentParameters();

    @Mock ConnectionOfTravelAssessmentAlgorithmFactory connectionOfTravelAssessmentAlgorithmFactory;
    @Mock ConnectionOfTravelAssessmentStreamsAlgorithm connectionOfTravelAssessmentAlgorithm;
    ConnectionOfTravelAssessmentParameters connectionOfTravelAssessmentParameters = new ConnectionOfTravelAssessmentParameters();

    @Mock StopLineStopAssessmentAlgorithmFactory stopLineStopAssessmentAlgorithmFactory;
    @Mock StopLineStopAssessmentStreamsAlgorithm stopLineStopAssessmentAlgorithm;
    StopLineStopAssessmentParameters stopLineStopAssessmentParameters = new StopLineStopAssessmentParameters();

    @Mock SpatMessageCountProgressionAlgorithmFactory spatMessageCountProgressionAlgorithmFactory;
    @Mock SpatMessageCountProgressionAlgorithm spatMessageCountProgressionAlgorithm;
    SpatMessageCountProgressionParameters spatMessageCountProgressionParameters = new SpatMessageCountProgressionParameters();

    @Mock MapMessageCountProgressionAlgorithmFactory mapMessageCountProgressionAlgorithmFactory;
    @Mock MapMessageCountProgressionAlgorithm mapMessageCountProgressionAlgorithm;
    MapMessageCountProgressionParameters mapMessageCountProgressionParameters = new MapMessageCountProgressionParameters();

    @Mock BsmMessageCountProgressionAlgorithmFactory bsmMessageCountProgressionAlgorithmFactory;
    @Mock BsmMessageCountProgressionAlgorithm bsmMessageCountProgressionAlgorithm;
    BsmMessageCountProgressionParameters bsmMessageCountProgressionParameters = new BsmMessageCountProgressionParameters();

    @Mock
    EventAlgorithmFactory eventAlgorithmFactory;
    @Mock
    EventAlgorithm eventAlgorithm;
    @Mock
    EventParameters eventParameters;

    MapIndex mapIndex = new MapIndex();
    
    @Test
    public void testConstructor() {

        final String defaultAlgo = "default";
        
        when(conflictMonitorProperties.getRepartitionAlgorithmFactory()).thenReturn(repartitionAlgorithmFactory);
        when(conflictMonitorProperties.getRepartitionAlgorithm()).thenReturn(defaultAlgo);
        when(repartitionAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(repartitionAlgorithm);
        when(conflictMonitorProperties.getRepartitionAlgorithmParameters()).thenReturn(repartitionParameters);
        
        when(conflictMonitorProperties.getNotificationAlgorithmFactory()).thenReturn(notificationAlgorithmFactory);
        when(conflictMonitorProperties.getNotificationAlgorithm()).thenReturn(defaultAlgo);
        when(notificationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(notificationAlgorithm);
        when(conflictMonitorProperties.getNotificationAlgorithmParameters()).thenReturn(notificationParameters);

        when(conflictMonitorProperties.getMapValidationAlgorithmFactory()).thenReturn(mapValidationAlgorithmFactory);
        when(conflictMonitorProperties.getMapValidationAlgorithm()).thenReturn(defaultAlgo);
        when(mapValidationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(mapValidationAlgorithm);
        when(conflictMonitorProperties.getMapValidationParameters()).thenReturn(mapValidationParameters);

        when(conflictMonitorProperties.getMapTimestampDeltaAlgorithmFactory()).thenReturn(mapTimestampDeltaAlgorithmFactory);
        when(conflictMonitorProperties.getMapTimestampDeltaAlgorithm()).thenReturn(defaultAlgo);
        when(mapTimestampDeltaAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(mapTimestampDeltaStreamsAlgorithm);
        when(conflictMonitorProperties.getMapTimestampDeltaParameters()).thenReturn(mapTimestampDeltaParameters);

        when(conflictMonitorProperties.getSpatValidationAlgorithmFactory()).thenReturn(spatValidationStreamsAlgorithmFactory);
        when(conflictMonitorProperties.getSpatValidationAlgorithm()).thenReturn(defaultAlgo);
        when(spatValidationStreamsAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(spatValidationAlgorithm);
        when(conflictMonitorProperties.getSpatValidationParameters()).thenReturn(spatValidationParameters);

        when(conflictMonitorProperties.getSpatTimestampDeltaAlgorithmFactory()).thenReturn(spatTimestampDeltaAlgorithmFactory);
        when(conflictMonitorProperties.getSpatTimestampDeltaAlgorithm()).thenReturn(defaultAlgo);
        when(spatTimestampDeltaAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(spatTimestampDeltaStreamsAlgorithm);
        when(conflictMonitorProperties.getSpatTimestampDeltaParameters()).thenReturn(spatTimestampDeltaParameters);

        when(conflictMonitorProperties.getAggregationParameters()).thenReturn(aggregationParameters);

        when(conflictMonitorProperties.getEventStateProgressionAggregationAlgorithmFactory()).thenReturn(eventStateProgressionAggregationAlgorithmFactory);
        when(conflictMonitorProperties.getEventStateProgressionAggregationAlgorithm()).thenReturn(defaultAlgo);
        when(eventStateProgressionAggregationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(eventStateProgressionAggregationAlgorithm);

        when(conflictMonitorProperties.getSpatMinimumDataAggregationAlgorithmFactory()).thenReturn(spatMinimumDataAggregationAlgorithmFactory);
        when(conflictMonitorProperties.getSpatMinimumDataAggregationAlgorithm()).thenReturn(defaultAlgo);
        when(spatMinimumDataAggregationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(spatMinimumDataAggregationAlgorithm);

        when(conflictMonitorProperties.getMapMinimumDataAggregationAlgorithmFactory()).thenReturn(mapMinimumDataAggregationAlgorithmFactory);
        when(conflictMonitorProperties.getMapMinimumDataAggregationAlgorithm()).thenReturn(defaultAlgo);
        when(mapMinimumDataAggregationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(mapMinimumDataAggregationAlgorithm);

        when(conflictMonitorProperties.getTimeChangeDetailsAggregationAlgorithmFactory()).thenReturn(timeChangeDetailsAggregationAlgorithmFactory);
        when(conflictMonitorProperties.getTimeChangeDetailsAggregationAlgorithm()).thenReturn(defaultAlgo);
        when(timeChangeDetailsAggregationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(timeChangeDetailsAggregationAlgorithm);

        when(conflictMonitorProperties.getIntersectionReferenceAlignmentAggregationAlgorithmFactory()).thenReturn(intersectionReferenceAlignmentAggregationAlgorithmFactory);
        when(conflictMonitorProperties.getIntersectionReferenceAlignmentAggregationAlgorithm()).thenReturn(defaultAlgo);
        when(intersectionReferenceAlignmentAggregationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(intersectionReferenceAlignmentAggregationAlgorithm);

        when(conflictMonitorProperties.getSignalGroupAlignmentAggregationAlgorithmFactory()).thenReturn(signalGroupAlignmentAggregationAlgorithmFactory);
        when(conflictMonitorProperties.getSignalGroupAlignmentAggregationAlgorithm()).thenReturn(defaultAlgo);
        when(signalGroupAlignmentAggregationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(signalGroupAlignmentAggregationAlgorithm);

        when(conflictMonitorProperties.getSignalStateConflictAggregationAlgorithmFactory()).thenReturn(signalStateConflictAggregationAlgorithmFactory);
        when(conflictMonitorProperties.getSignalStateConflictAggregationAlgorithm()).thenReturn(defaultAlgo);
        when(signalStateConflictAggregationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(signalStateConflictAggregationAlgorithm);

        when(conflictMonitorProperties.getMapMessageCountProgressionAggregationAlgorithmFactory()).thenReturn(mapMessageCountProgressionAggregationAlgorithmFactory);
        when(conflictMonitorProperties.getMapMessageCountProgressionAggregationAlgorithm()).thenReturn(defaultAlgo);
        when(mapMessageCountProgressionAggregationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(mapMessageCountProgressionAggregationAlgorithm);

        when(conflictMonitorProperties.getSpatMessageCountProgressionAggregationAlgorithmFactory()).thenReturn(spatMessageCountProgressionAggregationAlgorithmFactory);
        when(conflictMonitorProperties.getSpatMessageCountProgressionAggregationAlgorithm()).thenReturn(defaultAlgo);
        when(spatMessageCountProgressionAggregationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(spatMessageCountProgressionAggregationAlgorithm);

        // when(conflictMonitorProperties.getBsmMessageCountProgressionAggregationAlgorithmFactory()).thenReturn(bsmMessageCountProgressionAggregationAlgorithmFactory);
        // when(conflictMonitorProperties.getBsmMessageCountProgressionAggregationAlgorithm()).thenReturn(defaultAlgo);
        // when(bsmMessageCountProgressionAggregationAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(bsmMessageCountProgressionAggregationAlgorithm);

        when(conflictMonitorProperties.getSpatTimeChangeDetailsAlgorithmFactory()).thenReturn(spatTimeChangeDetailsAlgorithmFactory);
        when(conflictMonitorProperties.getSpatTimeChangeDetailsAlgorithm()).thenReturn(defaultAlgo);
        when(spatTimeChangeDetailsAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(spatTimeChangeDetailsAlgorithm);
        when(conflictMonitorProperties.getSpatTimeChangeDetailsParameters()).thenReturn(spatTimeChangeDetailsParameters);

        when(conflictMonitorProperties.getMapSpatMessageAssessmentAlgorithmFactory()).thenReturn(mapSpatMessageAssessmentAlgorithmFactory);
        when(conflictMonitorProperties.getMapSpatMessageAssessmentAlgorithm()).thenReturn(defaultAlgo);
        when(mapSpatMessageAssessmentAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(mapSpatMessageAssessmentAlgorithm);
        when(conflictMonitorProperties.getMapSpatMessageAssessmentParameters()).thenReturn(mapSpatMessageAssessmentParameters);

        when(conflictMonitorProperties.getBsmEventAlgorithmFactory()).thenReturn(bsmEventAlgorithmFactory);
        bsmEventParameters.setAlgorithm(defaultAlgo);
        when(bsmEventAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(bsmEventAlgorithm);
        when(conflictMonitorProperties.getBsmEventParameters()).thenReturn(bsmEventParameters);

        when(conflictMonitorProperties.getMessageIngestAlgorithmFactory()).thenReturn(messageIngestAlgorithmFactory);
        messageIngestParameters.setAlgorithm(defaultAlgo);
        when(messageIngestAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(messageIngestAlgorithm);
        when(conflictMonitorProperties.getMessageIngestParameters()).thenReturn(messageIngestParameters);

        when (conflictMonitorProperties.getSpatTransitionAlgorithmFactory()).thenReturn(spatTransitionAlgorithmFactory);
        when(conflictMonitorProperties.getSpatTransitionAlgorithm()).thenReturn(defaultAlgo);
        when(spatTransitionAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(spatTransitionAlgorithm);
        when(conflictMonitorProperties.getSpatTransitionParameters()).thenReturn(spatTransitionParameters);

        when(conflictMonitorProperties.getLaneDirectionOfTravelAlgorithmFactory()).thenReturn(laneDirectionOfTravelAlgorithmFactory);
        when(conflictMonitorProperties.getLaneDirectionOfTravelAlgorithm()).thenReturn(defaultAlgo);
        when(laneDirectionOfTravelAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(laneDirectionOfTravelAlgorithm);
        when(conflictMonitorProperties.getLaneDirectionOfTravelParameters()).thenReturn(laneDirectionOfTravelParameters);

        when(conflictMonitorProperties.getConnectionOfTravelAlgorithmFactory()).thenReturn(connectionOfTravelAlgorithmFactory);
        when(conflictMonitorProperties.getConnectionOfTravelAlgorithm()).thenReturn(defaultAlgo);
        when(connectionOfTravelAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(connectionOfTravelAlgorithm);
        when(conflictMonitorProperties.getConnectionOfTravelParameters()).thenReturn(connectionOfTravelParameters);

        when(conflictMonitorProperties.getSignalStateVehicleCrossesAlgorithmFactory()).thenReturn(signalStateVehicleCrossesAlgorithmFactory);
        when(conflictMonitorProperties.getSignalStateVehicleCrossesAlgorithm()).thenReturn(defaultAlgo);
        when(signalStateVehicleCrossesAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(signalStateVehicleCrossesAlgorithm);
        when(conflictMonitorProperties.getSignalStateVehicleCrossesParameters()).thenReturn(signalStateVehicleCrossesParameters);

        when(conflictMonitorProperties.getSignalStateVehicleStopsAlgorithmFactory()).thenReturn(signalStateVehicleStopsAlgorithmFactory);
        when(conflictMonitorProperties.getSignalStateVehicleStopsAlgorithm()).thenReturn(defaultAlgo);
        when(signalStateVehicleStopsAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(signalStateVehicleStopsAlgorithm);
        when(conflictMonitorProperties.getSignalStateVehicleStopsParameters()).thenReturn(signalStateVehicleStopsParameters);

        when(conflictMonitorProperties.getIntersectionEventAlgorithmFactory()).thenReturn(intersectionEventAlgorithmFactory);
        when(conflictMonitorProperties.getIntersectionEventAlgorithm()).thenReturn(defaultAlgo);
        when(intersectionEventAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(intersectionEventAlgorithm);
        
        when(conflictMonitorProperties.getSignalStateEventAssessmentAlgorithmFactory()).thenReturn(signalStateEventAssessmentAlgorithmFactory);
        when(conflictMonitorProperties.getSignalStateEventAssessmentAlgorithm()).thenReturn(defaultAlgo);
        when(signalStateEventAssessmentAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(signalStateEventAssessmentAlgorithm);
        when(conflictMonitorProperties.getSignalStateEventAssessmentAlgorithmParameters()).thenReturn(signalStateEventAssessmentParameters);

        when(conflictMonitorProperties.getLaneDirectionOfTravelAssessmentAlgorithmFactory()).thenReturn(laneDirectionOfTravelAssessmentAlgorithmFactory);
        when(conflictMonitorProperties.getLaneDirectionOfTravelAssessmentAlgorithm()).thenReturn(defaultAlgo);
        when(laneDirectionOfTravelAssessmentAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(laneDirectionOfTravelAssessmentAlgorithm);
        when(conflictMonitorProperties.getLaneDirectionOfTravelAssessmentAlgorithmParameters()).thenReturn(laneDirectionOfTravelAssessmentParameters);

        when(conflictMonitorProperties.getConnectionOfTravelAssessmentAlgorithmFactory()).thenReturn(connectionOfTravelAssessmentAlgorithmFactory);
        when(conflictMonitorProperties.getConnectionOfTravelAssessmentAlgorithm()).thenReturn(defaultAlgo);
        when(connectionOfTravelAssessmentAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(connectionOfTravelAssessmentAlgorithm);
        when(conflictMonitorProperties.getConnectionOfTravelAssessmentAlgorithmParameters()).thenReturn(connectionOfTravelAssessmentParameters);

        when(conflictMonitorProperties.getStopLineStopAssessmentAlgorithmFactory()).thenReturn(stopLineStopAssessmentAlgorithmFactory);
        when(conflictMonitorProperties.getStopLineStopAssessmentAlgorithm()).thenReturn(defaultAlgo);
        when(stopLineStopAssessmentAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(stopLineStopAssessmentAlgorithm);
        when(conflictMonitorProperties.getStopLineStopAssessmentAlgorithmParameters()).thenReturn(stopLineStopAssessmentParameters);

        when(conflictMonitorProperties.getMapMessageCountProgressionAlgorithmFactory()).thenReturn(mapMessageCountProgressionAlgorithmFactory);
        when(conflictMonitorProperties.getMapMessageCountProgressionAlgorithm()).thenReturn(defaultAlgo);
        when(mapMessageCountProgressionAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(mapMessageCountProgressionAlgorithm);
        when(conflictMonitorProperties.getMapMessageCountProgressionAlgorithmParameters()).thenReturn(mapMessageCountProgressionParameters);

        when(conflictMonitorProperties.getSpatMessageCountProgressionAlgorithmFactory()).thenReturn(spatMessageCountProgressionAlgorithmFactory);
        when(conflictMonitorProperties.getSpatMessageCountProgressionAlgorithm()).thenReturn(defaultAlgo);
        when(spatMessageCountProgressionAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(spatMessageCountProgressionAlgorithm);
        when(conflictMonitorProperties.getSpatMessageCountProgressionAlgorithmParameters()).thenReturn(spatMessageCountProgressionParameters);

        // when(conflictMonitorProperties.getBsmMessageCountProgressionAlgorithmFactory()).thenReturn(bsmMessageCountProgressionAlgorithmFactory);
        // when(conflictMonitorProperties.getBsmMessageCountProgressionAlgorithm()).thenReturn(defaultAlgo);
        // when(bsmMessageCountProgressionAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(bsmMessageCountProgressionAlgorithm);
        // when(conflictMonitorProperties.getBsmMessageCountProgressionAlgorithmParameters()).thenReturn(bsmMessageCountProgressionParameters);

        when(conflictMonitorProperties.getEventAlgorithmFactory()).thenReturn(eventAlgorithmFactory);
        when(conflictMonitorProperties.getEventAlgorithm()).thenReturn(defaultAlgo);
        when(eventAlgorithmFactory.getAlgorithm(defaultAlgo)).thenReturn(eventAlgorithm);
        when(conflictMonitorProperties.getEventParameters()).thenReturn(eventParameters);

        var monitorServiceController = new MonitorServiceController(
                conflictMonitorProperties,
                kafkaTemplate,
                configTopology,
                configParameters,
                configInitializer,
                mapIndex
        );
        assertThat(monitorServiceController, notNullValue());

        // Check all algorithms were started
        verify(repartitionAlgorithm, times(1)).start();
        verify(mapValidationAlgorithm, times(1)).start();
        verify(spatValidationAlgorithm, times(1)).start();
        verify(spatTimeChangeDetailsAlgorithm, times(1)).start();
        verify(mapSpatMessageAssessmentAlgorithm, times(1)).start();
        //verify(bsmEventAlgorithm, times(1)).start();
        //verify(messageIngestAlgorithm, times(1)).start();
        verify(intersectionEventAlgorithm, times(1)).start();
        verify(signalStateEventAssessmentAlgorithm, times(1)).start();
        verify(laneDirectionOfTravelAssessmentAlgorithm, times(1)).start();
        verify(connectionOfTravelAssessmentAlgorithm, times(1)).start();
        verify(stopLineStopAssessmentAlgorithm, times(1)).start();
        verify(mapMessageCountProgressionAlgorithm, times(1)).start();
        verify(spatMessageCountProgressionAlgorithm, times(1)).start();
    }
    
}
