package us.dot.its.jpo.deduplicator.deduplicator.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.dot.its.jpo.ode.model.OdeMapData;

@NoArgsConstructor
@Setter
@Getter
public class OdeMapPair {

    public OdeMapData message;
    public boolean shouldSend;

    public OdeMapPair(OdeMapData message, boolean shouldSend){
        this.message = message;
        this.shouldSend = shouldSend;
    }
}
