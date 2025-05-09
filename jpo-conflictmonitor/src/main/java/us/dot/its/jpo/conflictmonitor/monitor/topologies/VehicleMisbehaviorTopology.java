package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.SlidingWindows;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior.VehicleMisbehaviorParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior.VehicleMisbehaviorStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.MisbehaviorAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.ProcessedBsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.VehicleMisbehaviorEvent;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
// import us.dot.its.jpo.conflictmonitor.monitor.processors.VehicleMisbehaviorProcessor;
// import us.dot.its.jpo.conflictmonitor.monitor.processors.VehicleMisbehaviorProcessorSupplier;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // final String processedBsmStateStore = parameters.getProcessedBsmStateStoreName();

        KStream<BsmRsuIdKey, ProcessedBsm<Point>> inputStream = builder.stream(parameters.getBsmInputTopicName(),
                Consumed.with(
                        us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey(),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedBsm()).withTimestampExtractor(new ProcessedBsmTimestampExtractor()));

        KTable<Windowed<BsmRsuIdKey>, MisbehaviorAggregator> accelerations = inputStream
            .groupByKey()
            .windowedBy(SlidingWindows.ofTimeDifferenceAndGrace(Duration.ofSeconds(2),Duration.ofMillis(500)))
            .aggregate(
                MisbehaviorAggregator::new,
                (key, value, aggregate) -> aggregate.add(value),
                Materialized.with(us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey(), JsonSerdes.MisbehaviorAggregator()));

        inputStream.print(Printed.toSysOut());


        KStream<BsmRsuIdKey, VehicleMisbehaviorEvent> vehicleMisbehaviorEventsStream = accelerations
            .toStream()
            .flatMap((key, value)->{
                List<KeyValue<BsmRsuIdKey, VehicleMisbehaviorEvent>> result = new ArrayList<>();

                if( (value.getNumEvents() >=2 && (Math.abs(value.getAverageLateralAcceleration()) > parameters.getAccelerationRangeLateral() ||
                    Math.abs(value.getAverageLongitudinalAcceleration()) > parameters.getAccelerationRangeLongitudinal() ||
                    Math.abs(value.getAverageVerticalAcceleration()) > parameters.getAccelerationRangeVertical() ||
                    Math.abs(value.getCalculatedSpeed() - value.getVehicleSpeed()) > parameters.getSpeedRange() ||
                    Math.abs(value.getCalculatedYawRate() - value.getYawRate()) > parameters.getYawRateRange())) ||
                    Math.abs(value.getVehicleSpeed()) > parameters.getAllowableMaxSpeed() ||
                    Math.abs(value.getHeading()) > parameters.getAllowableMaxHeadingDelta()
                    ){
                    
                    VehicleMisbehaviorEvent event = new VehicleMisbehaviorEvent();
                    event.setSource(key.toString());
                    event.setTimeStamp(value.getLastRecordTime());
                    event.setVehicleID(value.getVehicleId());
                    
                    event.setYawRate(value.getYawRate());
                    event.setReportedSpeed(value.getVehicleSpeed());
                    event.setReportedAccelerationLat(value.getAverageLateralAcceleration());
                    event.setReportedAccelerationLon(value.getAverageLongitudinalAcceleration());
                    event.setReportedAccelerationVert(value.getAverageVerticalAcceleration());

                    event.setSpeedRange(parameters.getSpeedRange());
                    event.setAccelerationRangeLat(parameters.getAccelerationRangeLateral());
                    event.setAccelerationRangeLon(parameters.getAccelerationRangeLongitudinal());
                    event.setAccelerationRangeVert(parameters.getAccelerationRangeVertical());

                    event.setCalculatedHeading(value.getCalculatedYawRate());
                    event.setCalculatedSpeed(value.getCalculatedSpeed());

                    System.out.println(event);

                    result.add(new KeyValue<BsmRsuIdKey, VehicleMisbehaviorEvent>(key.key(), event));
                }

                

                return result;
            }
        );

        
        vehicleMisbehaviorEventsStream.to(parameters.getVehicleMisbehaviorEventOutputTopicName(),
                Produced.with(
                        us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey(),
                        us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.VehicleMisbehaviorEvent()));


        
        return builder.build();
    }


}