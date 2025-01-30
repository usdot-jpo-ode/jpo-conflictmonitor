package us.dot.its.jpo.conflictmonitor.monitor.serialization;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression.EventStateProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalStateConflictAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.VehicleEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.SignalStateAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfigKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.SpatBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.MapTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.SpatTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapBoundingBox;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.MapTimestampDeltaNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.SpatTimestampDeltaNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetailAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.SpatMovementState;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.SpatMovementStateTransition;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization.GenericJsonDeserializer;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdKey;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.broadcast_rate.SpatBroadcastRateNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.broadcast_rate.MapBroadcastRateNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.Assessment;


public class JsonSerdes {

    public static Serde<BsmAggregator> BsmDataAggregator() {
        return Serdes.serdeFrom(
            new JsonSerializer<BsmAggregator>(), 
            new JsonDeserializer<>(BsmAggregator.class));
    }

    public static Serde<BsmEvent> BsmEvent(){
        return Serdes.serdeFrom(
            new JsonSerializer<BsmEvent>(),
            new JsonDeserializer<>(BsmEvent.class));
    }

    public static Serde<VehicleEvent> VehicleEvent(){
        return Serdes.serdeFrom(
          new JsonSerializer<VehicleEvent>(),
          new JsonDeserializer<>(VehicleEvent.class));
    }

    public static Serde<MapBroadcastRateEvent> MapBroadcastRateEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<MapBroadcastRateEvent>(),
            new JsonDeserializer<>(MapBroadcastRateEvent.class));
    }

    public static Serde<SpatBroadcastRateEvent> SpatBroadcastRateEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<SpatBroadcastRateEvent>(),
            new JsonDeserializer<>(SpatBroadcastRateEvent.class));
    }

    public static Serde<LaneDirectionOfTravelEvent> LaneDirectionOfTravelEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<LaneDirectionOfTravelEvent>(),
            new JsonDeserializer<>(LaneDirectionOfTravelEvent.class));
    }

    public static Serde<ConnectionOfTravelEvent> ConnectionOfTravelEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<ConnectionOfTravelEvent>(),
            new JsonDeserializer<>(ConnectionOfTravelEvent.class));
    }

    public static Serde<StopLinePassageEvent> StopLinePassageEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<StopLinePassageEvent>(),
            new JsonDeserializer<>(StopLinePassageEvent.class));
    }
    
    public static Serde<StopLineStopEvent> StopLineStopEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<StopLineStopEvent>(),
            new JsonDeserializer<>(StopLineStopEvent.class));
    }

    public static Serde<IntersectionReferenceAlignmentEvent> IntersectionReferenceAlignmentEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<IntersectionReferenceAlignmentEvent>(),
            new JsonDeserializer<>(IntersectionReferenceAlignmentEvent.class));
    }

    public static Serde<SignalGroupAlignmentEvent> SignalGroupAlignmentEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<SignalGroupAlignmentEvent>(),
            new JsonDeserializer<>(SignalGroupAlignmentEvent.class));
    }

    public static Serde<SignalStateConflictEvent> SignalStateConflictEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<SignalStateConflictEvent>(),
            new JsonDeserializer<>(SignalStateConflictEvent.class));
    }

    public static Serde<TimeChangeDetailsEvent> TimeChangeDetailsEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<TimeChangeDetailsEvent>(),
            new JsonDeserializer<>(TimeChangeDetailsEvent.class));
    }

    public static Serde<MapMessageCountProgressionEvent> MapMessageCountProgressionEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<MapMessageCountProgressionEvent>(),
            new JsonDeserializer<>(MapMessageCountProgressionEvent.class));
    }

    public static Serde<SpatMessageCountProgressionEvent> SpatMessageCountProgressionEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<SpatMessageCountProgressionEvent>(),
            new JsonDeserializer<>(SpatMessageCountProgressionEvent.class));
    }

    public static Serde<BsmMessageCountProgressionEvent> BsmMessageCountProgressionEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<BsmMessageCountProgressionEvent>(),
            new JsonDeserializer<>(BsmMessageCountProgressionEvent.class));
    }


    public static Serde<SpatTimeChangeDetailAggregator> SpatTimeChangeDetailAggregator() {
        return Serdes.serdeFrom(
            new JsonSerializer<SpatTimeChangeDetailAggregator>(),
            new JsonDeserializer<>(SpatTimeChangeDetailAggregator.class));
    }

    public static Serde<SpatMap> SpatMap() {
        return Serdes.serdeFrom(
            new JsonSerializer<SpatMap>(),
            new JsonDeserializer<>(SpatMap.class));
    }

    public static Serde<SignalStateAssessment> SignalStateAssessment() {
        return Serdes.serdeFrom(
            new JsonSerializer<SignalStateAssessment>(),
            new JsonDeserializer<>(SignalStateAssessment.class));
    }

    public static Serde<StopLinePassageAssessment> StopLinePassageAssessment() {
        return Serdes.serdeFrom(
            new JsonSerializer<StopLinePassageAssessment>(),
            new JsonDeserializer<>(StopLinePassageAssessment.class));
    }

    public static Serde<StopLinePassageAggregator> SignalStateEventAggregator() {
        return Serdes.serdeFrom(
            new JsonSerializer<StopLinePassageAggregator>(),
            new JsonDeserializer<>(StopLinePassageAggregator.class));
    }

    public static Serde<StopLineStopAssessment> StopLineStopAssessment() {
        return Serdes.serdeFrom(
            new JsonSerializer<StopLineStopAssessment>(),
            new JsonDeserializer<>(StopLineStopAssessment.class));
    }

    public static Serde<StopLineStopNotification> StopLineStopNotification() {
        return Serdes.serdeFrom(
            new JsonSerializer<StopLineStopNotification>(),
            new JsonDeserializer<>(StopLineStopNotification.class));
    }

    public static Serde<StopLinePassageNotification> StopLinePassageNotification() {
        return Serdes.serdeFrom(
            new JsonSerializer<StopLinePassageNotification>(),
            new JsonDeserializer<>(StopLinePassageNotification.class));
    }

    public static Serde<StopLineStopAggregator> StopLineStopAggregator() {
        return Serdes.serdeFrom(
            new JsonSerializer<StopLineStopAggregator>(),
            new JsonDeserializer<>(StopLineStopAggregator.class));
    }

    public static Serde<LaneDirectionOfTravelAssessment> LaneDirectionOfTravelAssessment() {
        return Serdes.serdeFrom(
            new JsonSerializer<LaneDirectionOfTravelAssessment>(),
            new JsonDeserializer<>(LaneDirectionOfTravelAssessment.class));
    }

    public static Serde<LaneDirectionOfTravelAggregator> LaneDirectionOfTravelAggregator() {
        return Serdes.serdeFrom(
            new JsonSerializer<LaneDirectionOfTravelAggregator>(),
            new JsonDeserializer<>(LaneDirectionOfTravelAggregator.class));
    }

    public static Serde<ConnectionOfTravelAssessment> ConnectionOfTravelAssessment() {
        return Serdes.serdeFrom(
            new JsonSerializer<ConnectionOfTravelAssessment>(),
            new JsonDeserializer<>(ConnectionOfTravelAssessment.class));
    }

    public static Serde<ConnectionOfTravelAggregator> ConnectionOfTravelAggregator() {
        return Serdes.serdeFrom(
            new JsonSerializer<ConnectionOfTravelAggregator>(),
            new JsonDeserializer<>(ConnectionOfTravelAggregator.class));
    }

    public static Serde<BsmIntersectionIdKey> BsmIntersectionIdKey() {
        return Serdes.serdeFrom(
                new JsonSerializer<BsmIntersectionIdKey>(),
                new JsonDeserializer<>(BsmIntersectionIdKey.class));
    }

    public static Serde<IntersectionReferenceAlignmentNotification> IntersectionReferenceAlignmentNotification() {
        return Serdes.serdeFrom(
            new JsonSerializer<IntersectionReferenceAlignmentNotification>(),
            new JsonDeserializer<>(IntersectionReferenceAlignmentNotification.class));
    }

    public static Serde<MapMinimumDataEvent> MapMinimumDataEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<MapMinimumDataEvent>(),
            new JsonDeserializer<>(MapMinimumDataEvent.class)
        );
    }

    public static Serde<MapMinimumDataEventAggregation> MapMinimumDataEventAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(MapMinimumDataEventAggregation.class)
        );
    }

    public static Serde<SpatMinimumDataEvent> SpatMinimumDataEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<SpatMinimumDataEvent>(),
            new JsonDeserializer<>(SpatMinimumDataEvent.class)
        );
    }

    public static Serde<SpatMinimumDataEventAggregation> SpatMinimumDataEventAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(SpatMinimumDataEventAggregation.class)
        );
    }

    public static Serde<SignalGroupAlignmentNotification> SignalGroupAlignmentNotification() {
        return Serdes.serdeFrom(
            new JsonSerializer<SignalGroupAlignmentNotification>(),
            new JsonDeserializer<>(SignalGroupAlignmentNotification.class)
        );
    }

    public static Serde<LaneDirectionOfTravelNotification> LaneDirectionOfTravelAssessmentNotification() {
        return Serdes.serdeFrom(
            new JsonSerializer<LaneDirectionOfTravelNotification>(),
            new JsonDeserializer<>(LaneDirectionOfTravelNotification.class)
        );
    }

    public static Serde<ConnectionOfTravelNotification> ConnectionOfTravelNotification() {
        return Serdes.serdeFrom(
            new JsonSerializer<ConnectionOfTravelNotification>(),
            new JsonDeserializer<>(ConnectionOfTravelNotification.class)
        );
    }

    public static Serde<SignalStateConflictNotification> SignalStateConflictNotification() {
        return Serdes.serdeFrom(
            new JsonSerializer<SignalStateConflictNotification>(),
            new JsonDeserializer<>(SignalStateConflictNotification.class)
        );
    }

    public static Serde<TimeChangeDetailsNotification> TimeChangeDetailsNotification() {
        return Serdes.serdeFrom(
            new JsonSerializer<TimeChangeDetailsNotification>(),
            new JsonDeserializer<>(TimeChangeDetailsNotification.class)
        );
    }

    public static Serde<Notification> Notification() {
        return Serdes.serdeFrom(
            new JsonSerializer<Notification>(),
            new JsonDeserializer<>(Notification.class)
        );
    }

    public static Serde<Assessment> Assessment() {
        return Serdes.serdeFrom(
            new JsonSerializer<Assessment>(),
            new JsonDeserializer<>(Assessment.class)
        );
    }

    public static Serde<Event> Event() {
        return Serdes.serdeFrom(
            new JsonSerializer<Event>(),
            new JsonDeserializer<>(Event.class)
        );
    }

    public static Serde<DefaultConfig<?>> DefaultConfig() {
        return Serdes.serdeFrom(
            new JsonSerializer<DefaultConfig<?>>(),
            new GenericJsonDeserializer<DefaultConfig<?>>(DefaultConfig.class)
        );
    }

    public static Serde<IntersectionConfig<?>> IntersectionConfig() {
        return Serdes.serdeFrom(
            new JsonSerializer<IntersectionConfig<?>>(),
            new GenericJsonDeserializer<IntersectionConfig<?>>(IntersectionConfig.class)
        );
    }

    public static Serde<SpatBroadcastRateNotification> SpatBroadcastRateNotification() {
        return Serdes.serdeFrom(
            new JsonSerializer<SpatBroadcastRateNotification>(),
            new GenericJsonDeserializer<SpatBroadcastRateNotification>(SpatBroadcastRateNotification.class)
        );
    }

    public static Serde<MapBroadcastRateNotification> MapBroadcastRateNotification() {
        return Serdes.serdeFrom(
            new JsonSerializer<MapBroadcastRateNotification>(),
            new GenericJsonDeserializer<MapBroadcastRateNotification>(MapBroadcastRateNotification.class)
        );
    }


    public static Serde<IntersectionConfigKey> IntersectionConfigKey() {
        return Serdes.serdeFrom(
                new JsonSerializer<IntersectionConfigKey>(),
                new JsonDeserializer<>(IntersectionConfigKey.class)
        );
    }
    
    public static Serde<MapBoundingBox> MapBoundingBox() {
        return Serdes.serdeFrom(
                new JsonSerializer<MapBoundingBox>(),
                new JsonDeserializer<>(MapBoundingBox.class)
        );
    }

    public static Serde<MapTimestampDeltaEvent> MapTimestampDeltaEvent() {
        return Serdes.serdeFrom(
                new JsonSerializer<MapTimestampDeltaEvent>(),
                new JsonDeserializer<>(MapTimestampDeltaEvent.class)
        );
    }

    public static Serde<SpatTimestampDeltaEvent> SpatTimestampDeltaEvent() {
        return Serdes.serdeFrom(
                new JsonSerializer<SpatTimestampDeltaEvent>(),
                new JsonDeserializer<>(SpatTimestampDeltaEvent.class)
        );
    }

    public static Serde<MapTimestampDeltaNotification> MapTimestampDeltaNotification() {
        return Serdes.serdeFrom(
                new JsonSerializer<MapTimestampDeltaNotification>(),
                new JsonDeserializer<>(MapTimestampDeltaNotification.class)
        );
    }

    public static Serde<SpatTimestampDeltaNotification> SpatTimestampDeltaNotification() {
        return Serdes.serdeFrom(
                new JsonSerializer<SpatTimestampDeltaNotification>(),
                new JsonDeserializer<>(SpatTimestampDeltaNotification.class)
        );
    }

    public static Serde<RsuIntersectionSignalGroupKey> RsuIntersectionSignalGroupKey() {
        return Serdes.serdeFrom(
                new JsonSerializer<RsuIntersectionSignalGroupKey>(),
                new JsonDeserializer<>(RsuIntersectionSignalGroupKey.class));
    }

    public static Serde<SpatMovementStateTransition> SpatMovementStateTransition() {
        return Serdes.serdeFrom(
                new JsonSerializer<SpatMovementStateTransition>(),
                new JsonDeserializer<>(SpatMovementStateTransition.class));
    }

    public static Serde<SpatMovementState> SpatMovementState() {
        return Serdes.serdeFrom(
                new JsonSerializer<SpatMovementState>(),
                new JsonDeserializer<>(SpatMovementState.class));
    }

    public static Serde<EventStateProgressionEvent> EventStateProgressionEvent() {
        return Serdes.serdeFrom(
                new JsonSerializer<EventStateProgressionEvent>(),
                new JsonDeserializer<>(EventStateProgressionEvent.class));
    }

    public static Serde<EventStateProgressionNotification> EventStateProgressionNotification() {
        return Serdes.serdeFrom(
                new JsonSerializer<EventStateProgressionNotification>(),
                new JsonDeserializer<>(EventStateProgressionNotification.class));
    }

    public static Serde<EventStateProgressionAggregationKey> EventStateProgressionAggregationKey() {
        return Serdes.serdeFrom(
                new JsonSerializer<EventStateProgressionAggregationKey>(),
                new JsonDeserializer<>(EventStateProgressionAggregationKey.class));
    }

    public static Serde<EventStateProgressionEventAggregation> EventStateProgressionEventAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<EventStateProgressionEventAggregation>(),
                new JsonDeserializer<>(EventStateProgressionEventAggregation.class));
    }

    public static Serde<IntersectionReferenceAlignmentEventAggregation> IntersectionReferenceAlignmentEventAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<IntersectionReferenceAlignmentEventAggregation>(),
                new JsonDeserializer<>(IntersectionReferenceAlignmentEventAggregation.class));
    }

    public static Serde<SignalGroupAlignmentEventAggregation> SignalGroupAlignmentEventAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<SignalGroupAlignmentEventAggregation>(),
                new JsonDeserializer<>(SignalGroupAlignmentEventAggregation.class));
    }

    public static Serde<SignalStateConflictAggregationKey> SignalStateConflictAggregationKey() {
        return Serdes.serdeFrom(
                new JsonSerializer<SignalStateConflictAggregationKey>(),
                new JsonDeserializer<>(SignalStateConflictAggregationKey.class));
    }

    public static Serde<SignalStateConflictEventAggregation> SignalStateConflictEventAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<SignalStateConflictEventAggregation>(),
                new JsonDeserializer<>(SignalStateConflictEventAggregation.class));
    }

    public static Serde<TimeChangeDetailsAggregationKey> TimeChangeDetailsAggregationKey() {
        return Serdes.serdeFrom(
                new JsonSerializer<TimeChangeDetailsAggregationKey>(),
                new JsonDeserializer<>(TimeChangeDetailsAggregationKey.class));
    }

    public static Serde<TimeChangeDetailsEventAggregation> TimeChangeDetailsEventAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<TimeChangeDetailsEventAggregation>(),
                new JsonDeserializer<>(TimeChangeDetailsEventAggregation.class));
    }

    public static Serde<BsmMessageCountProgressionEventAggregation> BsmMessageCountProgressionEventAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(BsmMessageCountProgressionEventAggregation.class));
    }

    public static Serde<BsmMessageCountProgressionAggregationKey> BsmMessageCountProgressionAggregationKey() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(BsmMessageCountProgressionAggregationKey.class));
    }

    public static Serde<MapMessageCountProgressionEventAggregation> MapMessageCountProgressionEventAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(MapMessageCountProgressionEventAggregation.class));
    }

    public static Serde<MapMessageCountProgressionAggregationKey> MapMessageCountProgressionAggregationKey() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(MapMessageCountProgressionAggregationKey.class));
    }

    public static Serde<SpatMessageCountProgressionEventAggregation> SpatMessageCountProgressionEventAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(SpatMessageCountProgressionEventAggregation.class));
    }

    public static Serde<SpatMessageCountProgressionAggregationKey> SpatMessageCountProgressionAggregationKey() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(SpatMessageCountProgressionAggregationKey.class));
    }

    public static Serde<EventStateProgressionNotificationAggregation> EventStateProgressionNotificationAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(EventStateProgressionNotificationAggregation.class));
    }

    public static Serde<IntersectionReferenceAlignmentNotificationAggregation> IntersectionReferenceAlignmentNotificationAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(IntersectionReferenceAlignmentNotificationAggregation.class));
    }

    public static Serde<SignalGroupAlignmentNotificationAggregation> SignalGroupAlignmentNotificationAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(SignalGroupAlignmentNotificationAggregation.class));
    }

    public static Serde<SignalStateConflictNotificationAggregation> SignalStateConflictNotificationAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(SignalStateConflictNotificationAggregation.class));
    }

    public static Serde<TimeChangeDetailsNotificationAggregation> TimeChangeDetailsNotificationAggregation() {
        return Serdes.serdeFrom(
                new JsonSerializer<>(),
                new JsonDeserializer<>(TimeChangeDetailsNotificationAggregation.class));
    }
}
