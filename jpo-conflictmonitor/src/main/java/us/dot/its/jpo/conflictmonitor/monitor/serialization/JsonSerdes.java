package us.dot.its.jpo.conflictmonitor.monitor.serialization;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.SpatBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization.BsmAggregatorDeserializer;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization.BsmEventDeserializer;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization.OdeBsmDataJsonDeserializer;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization.VehicleEventDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.conflictmonitor.monitor.models.VehicleEvent;

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
            new BsmEventDeserializer());
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
}
