package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.kstream.Produced;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithms;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.analytics.LaneDirectionOfTravelAnalytics;
import us.dot.its.jpo.conflictmonitor.monitor.models.VehicleEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Intersection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.IntersectionLine;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateStopEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class IntersectionEventTopology {

    public static String getBsmID(OdeBsmData value){
        return ((J2735Bsm)value.getPayload().getData()).getCoreData().getId();
    }

    public static BsmAggregator getBsmsByTimeVehicle(ReadOnlyWindowStore bsmWindowStore, Instant start, Instant end, String id){

        Instant timeFrom = start.minusSeconds(60);
        Instant timeTo = start.plusSeconds(60);

        long startMillis = start.toEpochMilli();
        long endMillis = end.toEpochMilli();

        KeyValueIterator<Windowed<String>, OdeBsmData> bsmRange = bsmWindowStore.fetchAll(timeFrom, timeTo);

        BsmAggregator agg = new BsmAggregator();
        while(bsmRange.hasNext()){
            KeyValue<Windowed<String>, OdeBsmData> next = bsmRange.next();
            long ts = BsmTimestampExtractor.getBsmTimestamp(next.value);

            if(startMillis <= ts && endMillis >= ts && getBsmID(next.value).equals(id)){
                agg.add(next.value);
            }
        }

        bsmRange.close();
        agg.sort();

        return agg;
    }

    public static SpatAggregator getSpatByTime(ReadOnlyWindowStore spatWindowStore, Instant start, Instant end){

        Instant timeFrom = start.minusSeconds(60);
        Instant timeTo = start.plusSeconds(60);

        long startMillis = start.toEpochMilli();
        long endMillis = end.toEpochMilli();

        KeyValueIterator<Windowed<String>, ProcessedSpat> spatRange = spatWindowStore.fetchAll(timeFrom, timeTo);

        SpatAggregator spatAggregator = new SpatAggregator();
        while(spatRange.hasNext()){
            KeyValue<Windowed<String>, ProcessedSpat> next = spatRange.next();
            long ts = SpatTimestampExtractor.getSpatTimestamp(next.value);


            //if(startMillis <= ts && endMillis >= ts){ Add this back in later once geojson converter timestamps are fixed
                spatAggregator.add(next.value);
            //}
        }
        spatRange.close();
        spatAggregator.sort();

        return spatAggregator;
    }


    public static MapFeatureCollection getMap(ReadOnlyKeyValueStore mapStore, String key){
        return (MapFeatureCollection) mapStore.get(key);
    }


    public static Topology build(ConflictMonitorProperties conflictMonitorProps, ReadOnlyWindowStore bsmWindowStore, ReadOnlyWindowStore spatWindowStore, ReadOnlyKeyValueStore mapStore) {
        
        StreamsBuilder builder = new StreamsBuilder();

        // // Setup Lane Direction of Travel Factory
        LaneDirectionOfTravelAlgorithmFactory ldotAlgoFactory = conflictMonitorProps.getLaneDirectionOfTravelAlgorithmFactory();
        String ldotAlgo = conflictMonitorProps.getLaneDirectionOfTravelAlgorithm();
        LaneDirectionOfTravelAlgorithm laneDirectionOfTravelAlgorithm = ldotAlgoFactory.getAlgorithm(ldotAlgo);
        LaneDirectionOfTravelParameters ldotParams = conflictMonitorProps.getLaneDirectionOfTravelParameters();
        
        laneDirectionOfTravelAlgorithm.setParameters(ldotParams);
        laneDirectionOfTravelAlgorithm.start();


        // Setup Connection of Travel Factory
        ConnectionOfTravelAlgorithmFactory cotAlgoFactory = conflictMonitorProps.getConnectionOfTravelAlgorithmFactory();
        String cotAlgo = conflictMonitorProps.getConnectionOfTravelAlgorithm();
        ConnectionOfTravelAlgorithm connectionOfTravelAlgorithm = cotAlgoFactory.getAlgorithm(cotAlgo);
        ConnectionOfTravelParameters cotParams = conflictMonitorProps.getConnectionOfTravelParameters();
        
        connectionOfTravelAlgorithm.setParameters(cotParams);
        connectionOfTravelAlgorithm.start();


        // Setup Signal State Vehicle Crosses Factory
        SignalStateVehicleCrossesAlgorithmFactory ssvcAlgoFactory = conflictMonitorProps.getSignalStateVehicleCrossesAlgorithmFactory();
        String ssvcAlgo = conflictMonitorProps.getSignalStateVehicleCrossesAlgorithm();
        SignalStateVehicleCrossesAlgorithm signalStateVehicleCrossesAlgorithm = ssvcAlgoFactory.getAlgorithm(ssvcAlgo);
        SignalStateVehicleCrossesParameters ssvcParams = conflictMonitorProps.getSignalStateVehicleCrossesParameters();
        
        signalStateVehicleCrossesAlgorithm.setParameters(ssvcParams);
        signalStateVehicleCrossesAlgorithm.start();


        // Setup Signal State Vehicle Stops Factory
        SignalStateVehicleStopsAlgorithmFactory ssvsAlgoFactory = conflictMonitorProps.getSignalStateVehicleStopsAlgorithmFactory();
        String ssvsAlgo = conflictMonitorProps.getSignalStateVehicleStopsAlgorithm();
        SignalStateVehicleStopsAlgorithm signalStateVehicleStopsAlgorithm = ssvsAlgoFactory.getAlgorithm(ssvsAlgo);
        SignalStateVehicleStopsParameters ssvsParams = conflictMonitorProps.getSignalStateVehicleStopsParameters();
        
        signalStateVehicleStopsAlgorithm.setParameters(ssvsParams);
        signalStateVehicleStopsAlgorithm.start();

        
        KStream<String, BsmEvent> bsmEventStream = 
            builder.stream(
                conflictMonitorProps.getKafkaTopicCmBsmEvent(), 
                Consumed.with(
                    Serdes.String(),
                    JsonSerdes.BsmEvent())
                );


        // Join Spats, Maps and BSMS
        KStream<String, VehicleEvent> vehicleEventsStream = bsmEventStream.flatMap(
            (key, value)->{

                
                List<KeyValue<String, VehicleEvent>> result = new ArrayList<KeyValue<String, VehicleEvent>>();

                if(value.getStartingBsm() == null || value.getEndingBsm() == null){
                    return result;
                }

                String vehicleId = getBsmID(value.getStartingBsm());
                

                Instant firstBsmTime = Instant.ofEpochMilli(BsmTimestampExtractor.getBsmTimestamp(value.getStartingBsm()));
                Instant lastBsmTime = Instant.ofEpochMilli(BsmTimestampExtractor.getBsmTimestamp(value.getEndingBsm()));

                MapFeatureCollection map = null;
                BsmAggregator bsms = getBsmsByTimeVehicle(bsmWindowStore, firstBsmTime, lastBsmTime, vehicleId);
                SpatAggregator spats = getSpatByTime(spatWindowStore, firstBsmTime, lastBsmTime);

                if(spats.getSpats().size() > 0){
                    ProcessedSpat firstSpat = spats.getSpats().get(0);
                    String ip = firstSpat.getOriginIp();
                    int intersectionId = firstSpat.getIntersectionId();

                    String mapLookupKey = ip +":"+ intersectionId;
                    map = getMap(mapStore, mapLookupKey);

                    

                    if(map != null){
                        
                        Intersection intersection = Intersection.fromMapFeatureCollection(map);
                        VehicleEvent event = new VehicleEvent(bsms, spats, intersection);

                        String vehicleEventKey = intersection.getIntersectionId() + "_" + vehicleId;
                        result.add(new KeyValue<>(vehicleEventKey, event));
    
                        
                    }else{
                        System.out.println("Map was Null");
                    }

                }


                System.out.println("Detected Vehicle Event");
                System.out.println("Vehicle ID: " + ((J2735Bsm)value.getStartingBsm().getPayload().getData()).getCoreData().getId());
                System.out.println("Captured Bsms:  " + bsms.getBsms().size());
                System.out.println("Captured Spats: " + spats.getSpats().size());
                return result;
            }
        );


        // Perform Analytics on Lane direction of Travel Events
        KStream<String, LaneDirectionOfTravelEvent> laneDirectionOfTravelEventStream = vehicleEventsStream.flatMap(
            (key, value)->{
                VehiclePath path = new VehiclePath(value.getBsms(), value.getIntersection());

                List<KeyValue<String, LaneDirectionOfTravelEvent>> result = new ArrayList<KeyValue<String, LaneDirectionOfTravelEvent>>();
                ArrayList<LaneDirectionOfTravelEvent> events = laneDirectionOfTravelAlgorithm.getLaneDirectionOfTravelEvents(path);
                
                for(LaneDirectionOfTravelEvent event: events){
                    result.add(new KeyValue<>(event.getKey(), event));
                }

                return result;
            }
        );

        laneDirectionOfTravelEventStream.to(
            conflictMonitorProps.getKafkatopicCmLaneDirectionOfTravelEvent(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.LaneDirectionOfTravelEvent()));

        

        // Perform Analytics on Lane direction of Travel Events
        KStream<String, ConnectionOfTravelEvent> connectionTravelEventsStream = vehicleEventsStream.flatMap(
            (key, value)->{
                VehiclePath path = new VehiclePath(value.getBsms(), value.getIntersection());

                List<KeyValue<String, ConnectionOfTravelEvent>> result = new ArrayList<KeyValue<String, ConnectionOfTravelEvent>>();
                ConnectionOfTravelEvent event = connectionOfTravelAlgorithm.getConnectionOfTravelEvent(path);
                if(event != null){
                    result.add(new KeyValue<>(event.getKey(), event));
                }
                return result;
            }
        );

        connectionTravelEventsStream.to(
            conflictMonitorProps.getKafkaTopicCmConnectionOfTravelEvent(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.ConnectionOfTravelEvent()));


        // Perform Analytics of Signal State Vehicle Crossing Intersection
        KStream<String, SignalStateEvent> signalStateVehicleCrossingEventsStream = vehicleEventsStream.flatMap(
            (key, value)->{
                VehiclePath path = new VehiclePath(value.getBsms(), value.getIntersection());

                List<KeyValue<String, SignalStateEvent>> result = new ArrayList<KeyValue<String, SignalStateEvent>>();
                SignalStateEvent event = signalStateVehicleCrossesAlgorithm.getSignalStateEvent(path, value.getSpats());
                if(event != null){
                    result.add(new KeyValue<>(event.getKey(), event));
                }

                return result;
            }
        );

        signalStateVehicleCrossingEventsStream.to(
            conflictMonitorProps.getKafkaTopicCmSignalStateEvent(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.SignalStateEvent()));



        // Perform Analytics of Signal State Vehicle Crossing Intersection
        KStream<String, SignalStateStopEvent> signalStateVehicleStopEventsStream = vehicleEventsStream.flatMap(
            (key, value)->{

                VehiclePath path = new VehiclePath(value.getBsms(), value.getIntersection());

                List<KeyValue<String, SignalStateStopEvent>> result = new ArrayList<KeyValue<String, SignalStateStopEvent>>();
                SignalStateStopEvent event = signalStateVehicleStopsAlgorithm.getSignalStateStopEvent(path, value.getSpats());
                if(event != null){
                    result.add(new KeyValue<>(event.getKey(), event));
                }

                return result;
            }
        );

        signalStateVehicleStopEventsStream.to(
            conflictMonitorProps.getKafakTopicCmVehicleStopEvent(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.SignalStateVehicleStopsEvent()));

        return builder.build();
    }
}
