package us.dot.its.jpo.conflictmonitor.monitor.models.events.app_health;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Generated
public class KafkaStreamsUnhandledExceptionEvent extends KafkaStreamsEvent {
    public KafkaStreamsUnhandledExceptionEvent() {
        super("KafkaStreamsUnhandledExceptionEvent");
    }

    @JsonIgnore
    Throwable exception;
}
