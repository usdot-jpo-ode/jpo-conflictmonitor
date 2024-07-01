package us.dot.its.jpo.deduplicator.deduplicator.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.dot.its.jpo.ode.model.OdeBsmData;

@NoArgsConstructor
@Setter
@Getter
public class OdeBsmPair {

    public OdeBsmData message;
    public boolean shouldSend;

    public OdeBsmPair(OdeBsmData message, boolean shouldSend){
        this.message = message;
        this.shouldSend = shouldSend;
    }
}
