package us.dot.its.jpo.conflictmonitor.monitor.models.events.app_health;

import lombok.Setter;
import lombok.Getter;
import lombok.Generated;

@Getter
@Setter
@Generated
public class KafkaStreamsStateChangeEvent extends KafkaStreamsEvent {

    public KafkaStreamsStateChangeEvent() {
        super("KafkaStreamsStateChangeEvent");
    }

    /**
     * String value of {@link org.apache.kafka.streams.KafkaStreams.State}
     */
    public String newState;

    /**
     * String value of {@link org.apache.kafka.streams.KafkaStreams.State}
     */
    public String oldState;
    
}
