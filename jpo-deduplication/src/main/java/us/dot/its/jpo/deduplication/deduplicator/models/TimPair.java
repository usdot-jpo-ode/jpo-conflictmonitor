package us.dot.its.jpo.deduplication.deduplicator.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.dot.its.jpo.ode.model.OdeTimData;

@NoArgsConstructor
@Setter
@Getter
public class TimPair {

    public OdeTimData message;
    public boolean shouldSend;

    public TimPair(OdeTimData message, boolean shouldSend){
        this.message = message;
        this.shouldSend = shouldSend;
    }
}
