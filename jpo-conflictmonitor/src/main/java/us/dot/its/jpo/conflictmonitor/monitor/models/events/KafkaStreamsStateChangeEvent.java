package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

@Data
@AllArgsConstructor
@Generated
public class KafkaStreamsStateChangeEvent {

    public String topology;
    public String newState;
    public String oldState;
    
}
