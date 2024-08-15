package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.SpatTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.SpatTimestampDeltaNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.utils.SpatUtils;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.TimestampDeltaConstants.DEFAULT_SPAT_TIMESTAMP_DELTA_ALGORITHM;

@Component(DEFAULT_SPAT_TIMESTAMP_DELTA_ALGORITHM)
@Slf4j
public class SpatTimestampDeltaTopology
    extends BaseTimestampDeltaTopology<ProcessedSpat, SpatTimestampDeltaParameters, SpatTimestampDeltaEvent,
        SpatTimestampDeltaNotification>
    implements SpatTimestampDeltaStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected long extractMessageTimestamp(ProcessedSpat processedSpat) {
        return SpatUtils.getTimestamp(processedSpat);
    }

    @Override
    protected long extractOdeReceivedAt(ProcessedSpat processedSpat) {
        return SpatUtils.getOdeReceivedAt(processedSpat);
    }

    @Override
    protected SpatTimestampDeltaEvent constructEvent() {
        return new SpatTimestampDeltaEvent();
    }

    @Override
    protected Serde<SpatTimestampDeltaEvent> eventSerde() {
        return JsonSerdes.SpatTimestampDeltaEvent();
    }

    @Override
    protected Serde<SpatTimestampDeltaNotification> notificationSerde() {
        return JsonSerdes.SpatTimestampDeltaNotification();
    }

    @Override
    protected BaseTimestampDeltaNotificationProcessor<SpatTimestampDeltaEvent, SpatTimestampDeltaNotification> constructProcessor(Duration retentionTime, String eventStoreName, String keyStoreName) {
        return new SpatTimestampDeltaNotificationProcessor(retentionTime, eventStoreName, keyStoreName);
    }

}
