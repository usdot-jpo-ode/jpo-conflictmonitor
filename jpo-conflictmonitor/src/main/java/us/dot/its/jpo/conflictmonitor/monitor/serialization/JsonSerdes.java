package us.dot.its.jpo.conflictmonitor.monitor.serialization;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.VehicleEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.SignalStateAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.SignalStateEventAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.SignalStateEventAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateStopEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.SpatBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetailAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization.BsmAggregatorDeserializer;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization.OdeBsmDataJsonDeserializer;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization.SpatTimeChangeDetailAggregatorDeserializer;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization.VehicleEventDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.IntersectionReferenceAlignmentNotification;
import us.dot.its.jpo.ode.model.OdeBsmData;

public class JsonSerdes {
   
    public static Serde<OdeBsmData> OdeBsm() {
        return Serdes.serdeFrom(
            new JsonSerializer<OdeBsmData>(), 
            new OdeBsmDataJsonDeserializer());
    }

    public static Serde<BsmAggregator> BsmDataAggregator() {
        return Serdes.serdeFrom(
            new JsonSerializer<BsmAggregator>(), 
            new BsmAggregatorDeserializer());
    }

    public static Serde<BsmEvent> BsmEvent(){
        return Serdes.serdeFrom(
            new JsonSerializer<BsmEvent>(),
            new JsonDeserializer<>(BsmEvent.class));
    }

    public static Serde<VehicleEvent> VehicleEvent(){
        return Serdes.serdeFrom(
          new JsonSerializer<VehicleEvent>(),
          new VehicleEventDeserializer());
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

    public static Serde<SignalStateEvent> SignalStateEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<SignalStateEvent>(),
            new JsonDeserializer<>(SignalStateEvent.class));
    }
    
    public static Serde<SignalStateStopEvent> SignalStateVehicleStopsEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<SignalStateStopEvent>(),
            new JsonDeserializer<>(SignalStateStopEvent.class));
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

    public static Serde<SpatTimeChangeDetailAggregator> SpatTimeChangeDetailAggregator() {
        return Serdes.serdeFrom(
            new JsonSerializer<SpatTimeChangeDetailAggregator>(),
            new SpatTimeChangeDetailAggregatorDeserializer());
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

    public static Serde<SignalStateEventAssessment> SignalStateEventAssessment() {
        return Serdes.serdeFrom(
            new JsonSerializer<SignalStateEventAssessment>(),
            new JsonDeserializer<>(SignalStateEventAssessment.class));
    }

    public static Serde<SignalStateEventAggregator> SignalStateEventAggregator() {
        return Serdes.serdeFrom(
            new JsonSerializer<SignalStateEventAggregator>(),
            new JsonDeserializer<>(SignalStateEventAggregator.class));
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

    public static Serde<BsmIntersectionKey> BsmIntersectionKey() {
        return Serdes.serdeFrom(
            new JsonSerializer<BsmIntersectionKey>(),
            new JsonDeserializer<>(BsmIntersectionKey.class));
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

    public static Serde<SpatMinimumDataEvent> SpatMinimumDataEvent() {
        return Serdes.serdeFrom(
            new JsonSerializer<SpatMinimumDataEvent>(),
            new JsonDeserializer<>(SpatMinimumDataEvent.class)
        );
    }

    
}
