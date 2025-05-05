package us.dot.its.jpo.conflictmonitor.monitor.processors;

import java.time.format.DateTimeFormatter;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior.VehicleMisbehaviorParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.VehicleMisbehaviorEvent;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

public class VehicleMisbehaviorProcessorSupplier implements ProcessorSupplier<BsmRsuIdKey, ProcessedBsm<Point>, BsmRsuIdKey, VehicleMisbehaviorEvent>{

    String storeName;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    VehicleMisbehaviorParameters params;

    public VehicleMisbehaviorProcessorSupplier(VehicleMisbehaviorParameters params){
        this.params = params;
        this.storeName = params.getProcessedBsmStateStoreName();

        System.out.println("Store Name in Supplier" + this.storeName);
        
    }

    @Override
    public Processor<BsmRsuIdKey, ProcessedBsm<Point>, BsmRsuIdKey, VehicleMisbehaviorEvent> get() {
        return new VehicleMisbehaviorProcessor(params);
    }
}
