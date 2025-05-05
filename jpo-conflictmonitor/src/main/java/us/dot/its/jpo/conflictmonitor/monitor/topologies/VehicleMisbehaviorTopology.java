package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior.VehicleMisbehaviorParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior.VehicleMisbehaviorStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.MisbehaviorAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.TimeAcceleration;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.VehicleMisbehaviorEvent;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.conflictmonitor.monitor.processors.VehicleMisbehaviorProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.processors.VehicleMisbehaviorProcessorSupplier;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior.VehicleMisbehaviorConstants.DEFAULT_VEHICLE_MISBEHAVIOR_ALGORITHM;

@Component(DEFAULT_VEHICLE_MISBEHAVIOR_ALGORITHM)
@Slf4j
public class VehicleMisbehaviorTopology
        extends BaseStreamsTopology<VehicleMisbehaviorParameters>
        implements VehicleMisbehaviorStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    // VehicleMisbehaviorAggregationStreamsAlgorithm aggregationAlgorithm;

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        final String processedBsmStateStore = parameters.getProcessedBsmStateStoreName();

        KStream<BsmRsuIdKey, ProcessedBsm<Point>> inputStream = builder.stream(parameters.getBsmInputTopicName(),
                Consumed.with(
                        us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey(),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedBsm()));

        // inputStream.print(Printed.toSysOut());


        // KTable<String, ConnectionOfTravelAggregator> connectionOfTravelAssessments = 
        //     connectionOfTravelEvents.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.ConnectionOfTravelEvent()))
        //     .aggregate(
        //         connectionOfTravelAssessmentInitializer,
        //         connectionOfTravelEventAggregator,
        //         Materialized.<String, ConnectionOfTravelAggregator, KeyValueStore<Bytes, byte[]>>as("connectionOfTravelAssessments")
        //             .withKeySerde(Serdes.String())
        //             .withValueSerde(JsonSerdes.ConnectionOfTravelAggregator())
        //     );

        Initializer<MisbehaviorAggregator> misbehaviorAggregatorInitializer = ()->{
            MisbehaviorAggregator agg = new MisbehaviorAggregator();
            return agg;
        };

        Aggregator<BsmRsuIdKey, ProcessedBsm<Point>, MisbehaviorAggregator> misbehaviorAggregator =
            (key, value, aggregate)-> {
                return aggregate.add(value);
            };

        // TimeWindows slidingWindow = ;
        // KTable<Windowed<BsmRsuIdKey>, MisbehaviorAggregator> accelerations = inputStream.groupByKey()
        //     .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofSeconds(2),Duration.ofMillis(500)))
        //     .aggregate(misbehaviorAggregatorInitializer, 
        //     misbehaviorAggregator,
        //     Materialized.<BsmRsuIdKey, MisbehaviorAggregator, KeyValueStore<Bytes, byte[]>>as("vehicleMisbehaviorStore")
        //             .withKeySerde(us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey())
        //             .withValueSerde(JsonSerdes.MisbehaviorAggregator())
        //     );


        // Initializer<StopLinePassageAggregator> signalStateAssessmentInitializer = ()->{
        //     StopLinePassageAggregator agg = new StopLinePassageAggregator();
        //     // agg.setMessageDurationDays(parameters.getLookBackPeriodDays());

        //     // logger.info("Setting up Signal State Event Assessment Topology \n\n\n\n");
        //     return agg;
        // };

        // Aggregator<String, StopLinePassageEvent, StopLinePassageAggregator> signalStateEventAggregator =
        //     (key, value, aggregate)-> {
        //         return aggregate.add(value);
        //     };

        // KTable<String, StopLinePassageAggregator> signalStateAssessments = 
        //     signalStateEvents.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.StopLinePassageEvent()))
        //     .aggregate(
        //         signalStateAssessmentInitializer,
        //         signalStateEventAggregator,
        //         Materialized.<String, StopLinePassageAggregator, KeyValueStore<Bytes, byte[]>>as("stopLinePassageAssessments")
        //             .withKeySerde(Serdes.String())
        //             .withValueSerde(JsonSerdes.SignalStateEventAggregator())
        //     );

        

        // accelerations.toStream();



        // builder.addStateStore(Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(processedBsmStateStore),
        // us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey(), JsonSerdes.ProcessedBsm()).withLoggingDisabled());

        // var misbehaviorEventStream = inputStream.process(new VehicleMisbehaviorProcessorSupplier(parameters), processedBsmStateStore);

        
        // misbehaviorEventStream.to(parameters.getVehicleMisbehaviorEventOutputTopicName(),
        //         Produced.with(
        //                 us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey(),
        //                 us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.VehicleMisbehaviorEvent()));


        
        return builder.build();
    }


}