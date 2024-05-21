package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;


@Slf4j
public class MapZeroRateChecker
    extends BaseZeroRateChecker<ProcessedMap<LineString>, MapBroadcastRateEvent> {

    public MapZeroRateChecker(int rollingPeriodSeconds, int outputIntervalSeconds, String inputTopicName, String stateStoreName) {
        super(rollingPeriodSeconds, outputIntervalSeconds, inputTopicName, stateStoreName);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected MapBroadcastRateEvent createEvent() {
        return new MapBroadcastRateEvent();
    }




}
