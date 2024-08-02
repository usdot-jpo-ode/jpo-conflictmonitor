package us.dot.its.jpo.deduplicator.deduplicator.processors;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.model.OdeMapPayload;

public class OdeMapJsonProcessor extends DeduplicationProcessor<OdeMapData>{

    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    DeduplicatorProperties props;

    public OdeMapJsonProcessor(DeduplicatorProperties props){
        this.props = props;
        this.storeName = props.getKafkaStateStoreOdeMapJsonName();
    }


    @Override
    public Instant getMessageTime(OdeMapData message) {
        try {
            String time = ((OdeMapMetadata)message.getMetadata()).getOdeReceivedAt();
            return Instant.from(formatter.parse(time));
        } catch (Exception e) {
            return Instant.ofEpochMilli(0);
        }
    }

    @Override
    public boolean isDuplicate(OdeMapData lastMessage, OdeMapData newMessage) {

        Instant newValueTime = getMessageTime(newMessage);
        Instant oldValueTime = getMessageTime(lastMessage);

        if(newValueTime.minus(Duration.ofHours(1)).isAfter(oldValueTime)){
            return false;
            
        }else{
            OdeMapPayload oldPayload = (OdeMapPayload)lastMessage.getPayload();
            OdeMapPayload newPayload = (OdeMapPayload)newMessage.getPayload();

            Integer oldTimestamp = oldPayload.getMap().getTimeStamp();
            Integer newTimestamp = newPayload.getMap().getTimeStamp();
            

            newPayload.getMap().setTimeStamp(oldTimestamp);

            int oldHash = hashMapMessage(lastMessage);
            int newhash = hashMapMessage(newMessage);

            if(oldHash != newhash){
                newPayload.getMap().setTimeStamp(newTimestamp);
                return false;
            }
        }
        return true;
    }

    public int hashMapMessage(OdeMapData map){
        OdeMapPayload payload = (OdeMapPayload)map.getPayload();
        return Objects.hash(payload.toJson());
        
    }
}
