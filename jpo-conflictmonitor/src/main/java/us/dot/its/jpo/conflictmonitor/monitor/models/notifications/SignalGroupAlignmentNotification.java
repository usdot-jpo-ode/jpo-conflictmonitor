package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document("CmSignalGroupAlignmentNotification")
public class SignalGroupAlignmentNotification extends Notification {
    public SignalGroupAlignmentNotification() {
        super("SignalGroupAlignmentNotification");
    }

    @Getter private SignalGroupAlignmentEvent event;

    public void setEvent(SignalGroupAlignmentEvent event){
        if(event != null){
            this.event = event;
            this.setIntersectionID(event.getIntersectionID());
            this.setRoadRegulatorID(event.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s", 
            this.getNotificationType(), 
            event.getRoadRegulatorID(),
            event.getIntersectionID()
            );
    }
}