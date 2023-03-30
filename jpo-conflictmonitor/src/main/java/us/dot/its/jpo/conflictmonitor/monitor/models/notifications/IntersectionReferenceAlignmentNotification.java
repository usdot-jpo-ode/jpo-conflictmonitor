package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("CmIntersectionReferenceAlignmentNotification")
public class IntersectionReferenceAlignmentNotification extends Notification {
    public IntersectionReferenceAlignmentNotification() {
        super("IntersectionReferenceAlignmentNotification");
    }

    @Getter private IntersectionReferenceAlignmentEvent event;

    public void setEvent(IntersectionReferenceAlignmentEvent event){
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