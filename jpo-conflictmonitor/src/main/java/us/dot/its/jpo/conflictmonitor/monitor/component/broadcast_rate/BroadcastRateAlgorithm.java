package us.dot.its.jpo.conflictmonitor.monitor.component.broadcast_rate;

import us.dot.its.jpo.conflictmonitor.monitor.component.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.geojsonconverter.geojson.map.MapFeatureCollection;

public interface MapBroadcastRateAlgorithm 
    extends Algorithm<BroadcastRateParameters> {

    }